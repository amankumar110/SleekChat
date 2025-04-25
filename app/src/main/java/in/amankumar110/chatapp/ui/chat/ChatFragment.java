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
import in.amankumar110.chatapp.ui.internet.InternetNotAvailableFragment;
import in.amankumar110.chatapp.utils.DateHelper;
import in.amankumar110.chatapp.utils.InternetHelper;
import in.amankumar110.chatapp.utils.NetworkConnectionLiveData;
import in.amankumar110.chatapp.utils.SoundPlayer;
import in.amankumar110.chatapp.utils.UiHelper;
import in.amankumar110.chatapp.utils.ui.ChatFragmentViewUtil;
import in.amankumar110.chatapp.viewmodels.chat.ChatSessionViewModel;
import in.amankumar110.chatapp.viewmodels.messageupdate.MessageUpdateViewModel;
import in.amankumar110.chatapp.viewmodels.realtimemessage.RealtimeMessageViewModel;
import in.amankumar110.chatapp.viewmodels.user.RealtimeStatusViewModel;

@AndroidEntryPoint
public class ChatFragment extends Fragment {
    
    private final Gson gson = new Gson();
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
    private NetworkConnectionLiveData networkConnectionLiveData;

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

        observeWifi();
        showInChat();
        setupTopBar();
        initializeRecyclerView();
        setupSendButtonListener();
        setupObservers();

        if (messages.isEmpty() && realtimeMessageViewModel.messages.getValue() == null) {
            realtimeMessageViewModel.loadMessagesFromDatabase(chatSession.getSessionId());
        }

        realtimeMessageViewModel.observeNewMessage(chatSession.getSessionId());
        realtimeStatusViewModel.getUserStatusIfRequired(chatSession.getReceiverUid());
        messageUpdateViewModel.onNewDeletedMessage(chatSession.getSessionId());
        messageUpdateViewModel.onNewUpdatedMessage(chatSession.getSessionId());
    }

    private void observeWifi() {

        this.networkConnectionLiveData = new NetworkConnectionLiveData(requireContext());

        networkConnectionLiveData.observe(getViewLifecycleOwner(), isConnected -> {
            if(!isConnected) {
                InternetNotAvailableFragment internetNotAvailableFragment = InternetNotAvailableFragment.newInstance();
                internetNotAvailableFragment.show(getChildFragmentManager(),null);
            }
        });
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

            messagesAdapter.reflectDeletedMessage(deletedMessage);
            messages.remove(deletedMessage);

            messageUpdateViewModel.resetDeletedMessage();
        });
    }

    private void observeReceiverOnlineStatus() {
        realtimeStatusViewModel.userStatus.observe(getViewLifecycleOwner(), userStatus -> {

            Log.v("UserStatus",userStatus==null?"Null" : userStatus.getStatus());

            if (userStatus == null) return;

            showUserStatus(userStatus);

            Message.MessageStatus newStatus;
            if (isReceiverInCurrentChat()) {
                newStatus = Message.MessageStatus.SEEN;
            } else if (isReceiverOnline()) {
                newStatus = Message.MessageStatus.RECEIVER_ONLINE;
            } else {
                newStatus = Message.MessageStatus.SENT;
            }

            boolean updated = false;

            for (Message message : messages) {
                String currentStatus = message.getMessageStatus();
                if (Message.MessageStatus.shouldUpdateStatus(currentStatus, newStatus.getStatus())) {
                    message.setMessageStatus(newStatus.getStatus());
                    updated = true;
                }
            }

            if (updated) {
                messagesAdapter.setMessageList(messages);
                realtimeMessageViewModel.setMessagesStatus(newStatus); // Only update backend if needed
            }

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

            Log.v("NNM",message.toString());

            if (!messagesAdapter.contains(message)) {

                Log.v("ChatFragment","New Message Received");
                messagesAdapter.addMessage(message);
                messages.add(message);
            }
        });

        realtimeMessageViewModel.resetNewMessage();

    }

    private boolean isReceiverInCurrentChat() {
        return realtimeStatusViewModel.userStatus.getValue().getStatus().equals(UserStatus.Status.IN_CHAT.getName()) &&
                realtimeStatusViewModel.userStatus.getValue().getSessionId().equals(chatSession.getSessionId());
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

            Log.v("MessageListAfter",messages.toString());
            showMessages(messages);  // Update RecyclerView
            // Mark Unseen Messages as Seen of the other sender
            realtimeMessageViewModel.updateSenderMessagesStatus(chatSession, Message.MessageStatus.SEEN);
        });
    }

    private void initializeRecyclerView() {
        binding.rvMessages.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        binding.rvMessages.setAdapter(messagesAdapter);
    }

    private void showMessages(List<Message> messageList) {

        Log.v("ChatFragment","I m Showing");
        Log.v("ChatFragment","Generic");
        messagesAdapter.setMessageList(messageList);  // Pass the updated list
        ChatFragmentViewUtil.restoreScrollIfPossible(recyclerViewState, binding);
    }

    private void sendMessage(String messageText) {

        // Clear Message Input
        ChatFragmentViewUtil.clearMessageField(requireContext(), binding);

        if(!InternetHelper.isInternetAvailable(requireContext())) {
            UiHelper.showMessage(requireContext(), R.string.internet_required_message);
            return;
        }

        Message message = new Message();
        message.setMessage(messageText);
        message.setSentAt(System.currentTimeMillis());
        message.setSenderUId(chatSession.getSenderUid());
        message.setReceiverUId(chatSession.getReceiverUid());
        message.setId(realtimeMessageViewModel.generateUniqueMessageId(message.getSenderUId(), message.getReceiverUId()));

        if(isReceiverInCurrentChat())
            message.setMessageStatus(Message.MessageStatus.SEEN.getStatus());
        else if(isReceiverOnline())
            message.setMessageStatus(Message.MessageStatus.RECEIVER_ONLINE.getStatus());
        else
            message.setMessageStatus(Message.MessageStatus.SENT.getStatus());

        realtimeMessageViewModel.addMessage(message, chatSession.getSessionId());
    }

    private boolean isReceiverOnline() {
        return realtimeStatusViewModel.userStatus.getValue().getStatus().equals(UserStatus.Status.ONLINE.getName());
    }

    private void observeMessageSent() {
        realtimeMessageViewModel.isMessageSent.observe(getViewLifecycleOwner(), isSent -> {

            Log.v("IsMessageSent",isSent==null?"Null":isSent.toString());

            // If the message is idle or 'isSent' is null, we don't want to proceed
            if (!realtimeMessageViewModel.isIdle() || isSent == null) {
                return;
            }

            // Ensure that the message is cleared after the state changes to null (message sent)
            if (isSent) {
                SoundPlayer.playSound(requireContext(), R.raw.message_sent);

                return; // Exit early, no further action needed
            }

            // If the message wasn't sent, check for any error message and display it
            if (realtimeMessageViewModel.errorMessage.getValue() != null) {
                String errorMessage = realtimeMessageViewModel.errorMessage.getValue();
                UiHelper.showMessage(requireContext(), errorMessage);
            }

            // Reset the message sent flag
            realtimeMessageViewModel.resetMessageSent();
        });
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save RecyclerView scroll state
        String chatSessionJson = gson.toJson(chatSession);
        String messagesJson = gson.toJson(messages);
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
            this.chatSession = gson.fromJson(chatSessionJson, ChatSession.class);
            String messagesJson = savedInstanceState.getString(ARG_MESSAGES_JSON);
            this.messages = gson.fromJson(messagesJson, new TypeToken<List<Message>>() {}.getType());
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
        userStatus.setSessionId("");
        realtimeStatusViewModel.setUserStatus(uid, userStatus);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

}
