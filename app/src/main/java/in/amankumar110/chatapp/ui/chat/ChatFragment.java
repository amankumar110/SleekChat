package in.amankumar110.chatapp.ui.chat;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.databinding.FragmentChatBinding;
import in.amankumar110.chatapp.models.chat.ChatSession;
import in.amankumar110.chatapp.models.chat.Message;
import in.amankumar110.chatapp.models.user.UserStatus;
import in.amankumar110.chatapp.ui.chat.adapters.MessagesAdapter;
import in.amankumar110.chatapp.ui.chat.fragments.MessageEditDialogFragment;
import in.amankumar110.chatapp.utils.DateHelper;
import in.amankumar110.chatapp.utils.SoundPlayer;
import in.amankumar110.chatapp.utils.UiHelper;
import in.amankumar110.chatapp.utils.ui.ChatFragmentViewUtil;
import in.amankumar110.chatapp.viewmodels.chat.ChatSessionViewModel;
import in.amankumar110.chatapp.viewmodels.message.MessageViewModel;
import in.amankumar110.chatapp.viewmodels.realtimemessage.RealtimeMessageViewModel;
import in.amankumar110.chatapp.viewmodels.user.RealtimeStatusViewModel;


@AndroidEntryPoint
public class ChatFragment extends Fragment {

    public static final String ARG_CHAT_SESSION = "chat_session";
    private static final String ARG_RECYCLERVIEW_STATE = "recyclerview_state";
    private MessagesAdapter messagesAdapter;
    private final List<Message> messages = new ArrayList<>();
    private ChatSessionViewModel chatSessionViewModel;
    private RealtimeMessageViewModel realtimeMessageViewModel;
    private RealtimeStatusViewModel realtimeStatusViewModel;
    private FragmentChatBinding binding;
    private ChatSession chatSession;
    private NavController navController;
    private Parcelable recyclerViewState;

    public ChatFragment() {
    }

    public static ChatFragment newInstance(ChatSession chatSession) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CHAT_SESSION, chatSession);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            chatSession = (ChatSession) getArguments().getSerializable(ARG_CHAT_SESSION);
            messagesAdapter = new MessagesAdapter(this, chatSession.getSessionId());
        }

        realtimeStatusViewModel = new ViewModelProvider(requireActivity()).get(RealtimeStatusViewModel.class);
        realtimeMessageViewModel = new ViewModelProvider(requireActivity()).get(RealtimeMessageViewModel.class);
        chatSessionViewModel = new ViewModelProvider(requireActivity()).get(ChatSessionViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        showInChat();
        setupTopBar();
        initializeRecyclerView();
        setupSendButtonListener();
        setupObservers();

        if (messages.isEmpty() && realtimeMessageViewModel.messages.getValue() == null) {
            realtimeMessageViewModel.loadMessagesFromDatabase(chatSession.getSessionId());
        }

        realtimeMessageViewModel.observeNewMessage(chatSession.getSessionId());
        realtimeStatusViewModel.getUserStatus(chatSession.getReceiverUid());
    }

    private void showInChat() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserStatus userStatus = new UserStatus(UserStatus.Status.IN_CHAT, System.currentTimeMillis());
        realtimeStatusViewModel.setUserStatus(uid, userStatus);
    }

    private void setupTopBar() {
        binding.chatTopBarLayout.setSession(chatSession);
        navController = Navigation.findNavController(requireView());
        binding.chatTopBarLayout.btnBack.setOnClickListener(v ->
                navController.navigate(R.id.action_chatFragment_to_mainFragment)
        );
    }

    private void setupSendButtonListener() {

        binding.chatBottomBarLayout.btnSend.setOnClickListener(v -> {
            // Extract text message
            String message = binding.chatBottomBarLayout.etMessage.getText().toString().trim();

            // Validate message
            if (message.isEmpty()) {
                UiHelper.showMessage(requireContext(), R.string.empty_text_warning);
                return;
            }
            // Send if everything is right
            sendMessage(message);
        });
    }

    private void setupObservers() {
        observeMessagesLoaded();
        observeMessageSent();
        listenToNewMessage();
        observeReceiverOnlineStatus();
        observeMessagesMarkedAsSeen();
        observeMessageUpdate();
    }

    private void observeMessageUpdate() {

        realtimeMessageViewModel.isMessageUpdated.observe(getViewLifecycleOwner(), isUpdated -> {

            if(isUpdated==null)
                return;

            if(isUpdated) {
                List<Message> messageList = new ArrayList<>();

                List<Message> messages = realtimeMessageViewModel.messages.getValue();
                messageList.addAll(messages);

                List<Message> liveMessages = realtimeMessageViewModel.getLiveMessagesIfAvailable();
                if (liveMessages != null)
                    messageList.addAll(liveMessages);

                Log.v("ChatFragment","dbmessages : \n"+messages.toString());
                Log.v("ChatFragment","liveMEssages : \n"+liveMessages);

                showMessages(messageList);  // Update RecyclerView
            }
        });

    }

    private void observeReceiverOnlineStatus() {

        realtimeStatusViewModel.userStatus.observe(getViewLifecycleOwner(), userStatus -> {

            if (userStatus == null)
                return;

            Log.v("isOnline", ChatFragmentViewUtil.isOnline(userStatus.getStatus()) + "");
            Log.v("isInChat", ChatFragmentViewUtil.isInChat(userStatus.getStatus()) + "");
            Log.v("isOffline", ChatFragmentViewUtil.isOffline(userStatus.getStatus()) + "");


            if (ChatFragmentViewUtil.isInChat(userStatus.getStatus()))
                messagesAdapter.markMessagesAsSeen();

            showOnlineStatus(userStatus);
        });
    }

    private void showOnlineStatus(UserStatus userStatus) {

        if (ChatFragmentViewUtil.isOnline(userStatus.getStatus())) {

            binding.chatTopBarLayout.tvUserLastTime.setText(R.string.online_status);

        } else if (ChatFragmentViewUtil.isInChat(userStatus.getStatus())) {

            binding.chatTopBarLayout.tvUserLastTime.setText(R.string.in_chat_status);

        } else {
            String formattedTime = DateHelper.getFormattedTime(userStatus.getLastSeen());
            String message = getString(R.string.last_message_time_text) + " " + formattedTime;
            binding.chatTopBarLayout.tvUserLastTime.setText(message);
        }
    }

    private void listenToNewMessage() {

        realtimeMessageViewModel.newMessage.observe(getViewLifecycleOwner(), message -> {

            if (!realtimeMessageViewModel.isIdle())
                return;

            if (message == null)
                return;

            if (!messagesAdapter.contains(message)) {

                if (message.getSenderUId().equals(FirebaseAuth.getInstance().getUid()) &&
                        realtimeStatusViewModel.userStatus.getValue()!=null &&
                        ChatFragmentViewUtil.isInChat(
                                realtimeStatusViewModel.userStatus.getValue().getStatus())
                ) {
                    realtimeMessageViewModel.markLiveMessageAsSeen(message);
                    message.setSeen(true);
                }
                messagesAdapter.addMessage(message);
                messages.add(message);
            }
        });
    }

    private void observeMessagesLoaded() {

        realtimeMessageViewModel.messages.observe(getViewLifecycleOwner(), messageList -> {

            if (messageList == null || messageList.isEmpty())
                return;

            messages.clear();  // Clear old data (if necessary)
            messages.addAll(messageList); // Add the new data

            Log.v("DBMESSAGE",messageList.toString());

            // Append live message if any live session is going on
            List<Message> liveMessages = realtimeMessageViewModel.getLiveMessagesIfAvailable();
            if(liveMessages!=null)
                messages.addAll(liveMessages);
            Log.v("ALLMESSAGES",messageList.toString());
            showMessages(messages);  // Update RecyclerView

            // Mark Unseen Messages as Seen of the other sender
            realtimeMessageViewModel.markSenderMessagesAsSeenIfRequired(chatSession);
        });
    }

    public void observeMessagesMarkedAsSeen() {

        realtimeMessageViewModel.messagesMarkedAsRead.observe(getViewLifecycleOwner(), isMarked -> {

            if (!realtimeMessageViewModel.isIdle())
                return;

            if (Boolean.TRUE.equals(isMarked)) {
                messagesAdapter.markMessagesAsSeen();
            }

        });
    }

    private void initializeRecyclerView() {

        binding.rvMessages.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        binding.rvMessages.setAdapter(messagesAdapter);
    }

    private void showMessages(List<Message> messageList) {

        Log.v(getClass().getName(), "Messages: " + messageList.size());
        messagesAdapter.setMessageList(messageList);  // Pass the updated list
        ChatFragmentViewUtil.restoreScrollIfPossible(recyclerViewState, binding);
    }

    private void sendMessage(String messageText) {

        Message message = new Message();
        message.setMessage(messageText);
        message.setSentAt(System.currentTimeMillis());
        message.setSenderUId(chatSession.getSenderUid());
        message.setReceiverUId(chatSession.getReceiverUid());
        message.setId(realtimeMessageViewModel.generateUniqueMessageId(message.getSenderUId(), message.getReceiverUId()));

        realtimeMessageViewModel.addMessage(message, chatSession.getSessionId());
    }

    private void observeMessageSent() {

        realtimeMessageViewModel.isMessageSent.observe(getViewLifecycleOwner(), isSent -> {

            ChatFragmentViewUtil.clearMessageField(requireContext(), binding);

            if (!realtimeMessageViewModel.isIdle())
                return;

            if (isSent) {
                SoundPlayer.playSound(requireContext(), R.raw.message_sent);
                return;
            }

            if (realtimeMessageViewModel.errorMessage.getValue() != null) {
                String errorMessage = realtimeMessageViewModel.errorMessage.getValue();
                UiHelper.showMessage(requireContext(), errorMessage);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save RecyclerView scroll state
        String chatSessionJson = new Gson().toJson(chatSession);
        outState.putString(ARG_CHAT_SESSION, chatSessionJson);
        recyclerViewState = binding.rvMessages.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(ARG_RECYCLERVIEW_STATE, recyclerViewState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        // Restore scroll state if available
        if (savedInstanceState != null) {
            recyclerViewState = savedInstanceState.getParcelable(ARG_RECYCLERVIEW_STATE);
            String chatSessionJson = savedInstanceState.getString(ARG_CHAT_SESSION);
            this.chatSession = new Gson().fromJson(chatSessionJson, ChatSession.class);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        showOnline();
        realtimeMessageViewModel.syncMessages(chatSession.getSessionId());
        chatSessionViewModel.updateLastMessage(chatSession, messages.get(messages.size() - 1).getMessage());
    }

    private void showOnline() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserStatus userStatus = new UserStatus(UserStatus.Status.ONLINE, System.currentTimeMillis());
        realtimeStatusViewModel.setUserStatus(uid, userStatus);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
