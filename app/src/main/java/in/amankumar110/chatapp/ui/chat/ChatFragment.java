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
import com.google.gson.reflect.TypeToken;

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
import in.amankumar110.chatapp.viewmodels.messageupdate.MessageUpdateViewModel;
import in.amankumar110.chatapp.viewmodels.realtimemessage.RealtimeMessageViewModel;
import in.amankumar110.chatapp.viewmodels.user.RealtimeStatusViewModel;

@AndroidEntryPoint
public class ChatFragment extends Fragment {

    public static final String ARG_CHAT_SESSION = "chat_session";
    private static final String ARG_RECYCLERVIEW_STATE = "recyclerview_state";
    private static final String ARG_MESSAGES_JSON = "messages";
    private MessagesAdapter messagesAdapter;
    private List<Message> messages = new ArrayList<>();
    private ChatSessionViewModel chatSessionViewModel;
    private RealtimeMessageViewModel realtimeMessageViewModel;
    private RealtimeStatusViewModel realtimeStatusViewModel;
    private MessageUpdateViewModel messageUpdateViewModel;
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
        messageUpdateViewModel = new ViewModelProvider(this).get(MessageUpdateViewModel.class);
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
        messageUpdateViewModel.onNewDeletedMessage(chatSession.getSessionId());
        messageUpdateViewModel.onNewUpdatedMessage(chatSession.getSessionId());
    }

    private void showInChat() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UserStatus userStatus = new UserStatus(UserStatus.Status.IN_CHAT, System.currentTimeMillis());
        userStatus.setSessionId(chatSession.getSessionId());
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
        observeMessageDelete();
    }

    private void observeMessageUpdate() {

        messageUpdateViewModel.newUpdatedMessage.observe(getViewLifecycleOwner(),updatedMessage -> {



            if(!messageUpdateViewModel.isIdle() || updatedMessage==null)
                return;

            Log.v("ChatFragment","A Message was Updated");

            messagesAdapter.reflectUpdateMessage(updatedMessage);
            messages.set(messages.indexOf(updatedMessage),updatedMessage);

            messageUpdateViewModel.resetUpdatedMessage();
        });
    }

    private void observeMessageDelete() {

        messageUpdateViewModel.newDeletedMessage.observe(getViewLifecycleOwner(),deletedMessage -> {

            if(!messageUpdateViewModel.isIdle() || deletedMessage==null)
                return;

            Log.v("ChatFragment","A Message was Deleted");
            messagesAdapter.reflectDeletedMessage(deletedMessage);
            messages.remove(deletedMessage);

            messageUpdateViewModel.resetDeletedMessage();
        });
    }

    private void observeReceiverOnlineStatus() {

        realtimeStatusViewModel.userStatus.observe(getViewLifecycleOwner(), userStatus -> {

            if (userStatus == null)
                return;

            Log.v("isOnline", ChatFragmentViewUtil.isOnline(userStatus.getStatus()) + "");
            Log.v("isInChat", ChatFragmentViewUtil.isInChat(userStatus.getStatus()) + "");
            Log.v("isOffline", ChatFragmentViewUtil.isOffline(userStatus.getStatus()) + "");


            if (ChatFragmentViewUtil.isInChat(userStatus.getStatus())) {
                messagesAdapter.markMessagesAsSeen();
            }
            showUserStatus(userStatus);
        });
    }

    private void showUserStatus(UserStatus userStatus) {

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

            if (!realtimeMessageViewModel.isIdle() || message == null)
                return;

            if (!messagesAdapter.contains(message)) {

                if (message.getSenderUId().equals(FirebaseAuth.getInstance().getUid()) &&
                        realtimeStatusViewModel.userStatus.getValue()!=null &&
                        ChatFragmentViewUtil.isInChat(
                                realtimeStatusViewModel.userStatus.getValue().getStatus())
                ) {
                    realtimeMessageViewModel.markLiveMessageAsSeen(message);
                    message.setIsSeen(true);
                }
                messagesAdapter.addMessage(message);
                messages.add(message);
            }
        });

        realtimeMessageViewModel.resetNewMessage();
    }

    private void observeMessagesLoaded() {

        realtimeMessageViewModel.messages.observe(getViewLifecycleOwner(), messageList -> {

            if (messageList == null || messageList.isEmpty())
                return;

            messages.clear();  // Clear old data (if necessary)
            messages.addAll(messageList); // Add the new data

            // Append live message if any live session is going on
            List<Message> liveMessages = realtimeMessageViewModel.getLiveMessagesIfAvailable();

            if(liveMessages!=null)
                messages.addAll(liveMessages);

            showMessages(messages);  // Update RecyclerView
            // Mark Unseen Messages as Seen of the other sender
            realtimeMessageViewModel.markSenderMessagesAsSeenIfRequired(chatSession);
        });
    }

    public void observeMessagesMarkedAsSeen() {

        realtimeMessageViewModel.messagesMarkedAsRead.observe(getViewLifecycleOwner(), isMarked -> {

            if (!realtimeMessageViewModel.isIdle() || isMarked==null)
                return;

            if (Boolean.TRUE.equals(isMarked)) {
                messagesAdapter.markMessagesAsSeen();
            }

            realtimeMessageViewModel.resetMessagesMarkedAsRead();
        });
    }

    private void initializeRecyclerView() {
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        binding.rvMessages.setAdapter(messagesAdapter);
    }

    private void showMessages(List<Message> messageList) {
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

            if (!realtimeMessageViewModel.isIdle() || isSent==null)
                return;

            if (isSent) {
                SoundPlayer.playSound(requireContext(), R.raw.message_sent);
                return;
            }

            if (realtimeMessageViewModel.errorMessage.getValue() != null) {
                String errorMessage = realtimeMessageViewModel.errorMessage.getValue();
                UiHelper.showMessage(requireContext(), errorMessage);
            }

            realtimeMessageViewModel.resetMessageSent();
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save RecyclerView scroll state
        String chatSessionJson = new Gson().toJson(chatSession);
        String messagesJson = new Gson().toJson(messages);
        recyclerViewState = binding.rvMessages.getLayoutManager().onSaveInstanceState();

        outState.putString(ARG_CHAT_SESSION, chatSessionJson);
        outState.putString(ARG_MESSAGES_JSON,messagesJson);
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
            String messagesJson = savedInstanceState.getString(ARG_MESSAGES_JSON);
            this.messages = new Gson().fromJson(messagesJson, new TypeToken<List<Message>>() {}.getType());
        }
    }

    private void syncMessages() {
        List<Message> updateMessages = messageUpdateViewModel.getUpdatedMessages();
        List<Message> deleteMessages = messageUpdateViewModel.getDeletedMessages();
        List<Message> syncMessages = realtimeMessageViewModel.getLiveMessagesIfAvailable();
        realtimeMessageViewModel.syncMessages(syncMessages,updateMessages,deleteMessages,chatSession.getSessionId());
    }

    @Override
    public void onPause() {
        super.onPause();
        showOnline();
        syncMessages();
        if (!messages.isEmpty()) {
            chatSessionViewModel.updateLastMessage(chatSession, messages.get(messages.size() - 1).getMessage());
        }
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
