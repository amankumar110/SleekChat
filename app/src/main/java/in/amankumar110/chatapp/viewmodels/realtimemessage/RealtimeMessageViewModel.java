package in.amankumar110.chatapp.viewmodels.realtimemessage;

import static java.lang.Boolean.TRUE;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.amankumar110.chatapp.domain.repository.MessageRepository;
import in.amankumar110.chatapp.domain.repository.RealtimeMessageRepository;
import in.amankumar110.chatapp.domain.usecases.message.GenerateMessageIdUseCase;
import in.amankumar110.chatapp.domain.usecases.message.GetMessagesUseCase;
import in.amankumar110.chatapp.domain.usecases.message.MarkMessageAsSeenUseCase;
import in.amankumar110.chatapp.domain.usecases.message.SyncMessagesUseCaseWrapper;
import in.amankumar110.chatapp.domain.usecases.message.UpdateMessageUseCase;
import in.amankumar110.chatapp.domain.usecases.realtimemessaging.GetRealtimeMessageUseCase;
import in.amankumar110.chatapp.domain.usecases.realtimemessaging.SendMessageUseCase;
import in.amankumar110.chatapp.models.chat.ChatSession;
import in.amankumar110.chatapp.models.chat.Message;


// This viewmodel is used to initially loading the database messages and then managing the
// communication using realtime database for instant messaging

@HiltViewModel
public class RealtimeMessageViewModel extends ViewModel {
    private final GetRealtimeMessageUseCase getRealtimeMessageUseCase;
    private final SendMessageUseCase sendMessageUseCase;
    private final GetMessagesUseCase getMessagesUseCase;
    private final GenerateMessageIdUseCase generateMessageIdUseCase;
    private final SyncMessagesUseCaseWrapper syncMessagesUseCaseWrapper;
    private final MarkMessageAsSeenUseCase markMessagesAsSeenUseCase;
    private final UpdateMessageUseCase updateMessageUseCase;

    private final MutableLiveData<List<Message>> _messages = new MutableLiveData<>(null);
    public LiveData<List<Message>> messages = _messages;

    private final MutableLiveData<Message> _newMessage = new MutableLiveData<>(null);
    public LiveData<Message> newMessage = _newMessage;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>(null);
    public LiveData<String> errorMessage = _errorMessage;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<Boolean> _isMessageSent = new MutableLiveData<>(false);
    public LiveData<Boolean> isMessageSent = _isMessageSent;
    private final MutableLiveData<Boolean> _messagesMarkedAsRead = new MutableLiveData<>(null);
    public LiveData<Boolean> messagesMarkedAsRead = _messagesMarkedAsRead;
    private final List<Message> unsyncedMessages = new ArrayList<>();
    private final MutableLiveData<Boolean> _isMessageUpdated = new MutableLiveData<>(null);
    public LiveData<Boolean> isMessageUpdated = _isMessageUpdated;

    @Inject
    public RealtimeMessageViewModel(GetRealtimeMessageUseCase getRealtimeMessageUseCase, SendMessageUseCase sendMessageUseCase, GetMessagesUseCase getMessagesUseCase, GenerateMessageIdUseCase generateMessageIdUseCase, SyncMessagesUseCaseWrapper syncMessagesUseCaseWrapper, MarkMessageAsSeenUseCase markMessagesAsSeenUseCase, UpdateMessageUseCase updateMessageUseCase) {
        this.getRealtimeMessageUseCase = getRealtimeMessageUseCase;
        this.sendMessageUseCase = sendMessageUseCase;
        this.getMessagesUseCase = getMessagesUseCase;
        this.markMessagesAsSeenUseCase = markMessagesAsSeenUseCase;
        this.generateMessageIdUseCase = generateMessageIdUseCase;
        this.syncMessagesUseCaseWrapper = syncMessagesUseCaseWrapper;
        this.updateMessageUseCase = updateMessageUseCase;
    }

    public void addMessage(Message message, String sessionId) {

        _isLoading.postValue(true);

        message.setSynced(false);

        sendMessageUseCase.execute(message, sessionId, new RealtimeMessageRepository.RealtimeMessageListener<>() {
            @Override
            public void onSuccess(Void data) {
                _isLoading.postValue(false);
                _isMessageSent.postValue(true);
            }

            @Override
            public void onError(Exception exception) {
                _isLoading.postValue(false);
                _isMessageSent.postValue(false);
                _errorMessage.postValue(exception.getMessage());
            }
        });
    }

    public void observeNewMessage(String sessionId) {

        _isLoading.postValue(true);

        getRealtimeMessageUseCase.execute(sessionId, new RealtimeMessageRepository.RealtimeMessageListener<Message>() {
            @Override
            public void onSuccess(Message data) {
                _isLoading.postValue(false);
                unsyncedMessages.add(data);
                _newMessage.postValue(data);
            }

            @Override
            public void onError(Exception exception) {
                _errorMessage.postValue(exception.getMessage());
                _isLoading.postValue(false);
                _newMessage.postValue(null);
            }
        });
    }

    public void loadMessagesFromDatabase(String sessionId) {

        _isLoading.postValue(true);

        getMessagesUseCase.execute(sessionId, new MessageRepository.MessageListener<>() {
            @Override
            public void onSuccess(List<Message> result) {

                Log.v("MessagesFromViewModel",result.toString());
                _isLoading.postValue(false);
                _messages.postValue(result);
            }

            @Override
            public void onError(Exception exception) {

                _isLoading.postValue(false);
                _errorMessage.postValue(exception.getMessage());
                _messages.postValue(new ArrayList<>());
            }
        });
    }

    public String generateUniqueMessageId(String senderId, String receiverId) {
        return generateMessageIdUseCase.execute(senderId,receiverId);
    }

    public void syncMessages(String sessionId) {

        if(isIdle())
            Log.v("syncing",unsyncedMessages.toString());
        syncMessagesUseCaseWrapper.syncInBackground(unsyncedMessages,sessionId);
        unsyncedMessages.clear();
    }

    public void markSenderMessagesAsSeenIfRequired(ChatSession chatSession) {

        _isLoading.postValue(true);

        List<Message> filteredMessages = _messages.getValue().stream()
                .filter(message -> isNotSentByMe(message) && !message.isSeen())
                .collect(Collectors.toList());

        if(filteredMessages.isEmpty()) {
            _isLoading.postValue(false);
            return;
        }

        markMessagesAsSeenUseCase.execute(filteredMessages,
                chatSession,
                new MessageRepository.MessageListener<>() {

            @Override
            public void onSuccess(Void result) {
                _isLoading.postValue(false);
                _messagesMarkedAsRead.postValue(true);
            }

            @Override
            public void onError(Exception exception) {
                _isLoading.postValue(false);
                _messagesMarkedAsRead.postValue(false);
                _errorMessage.postValue(exception.getMessage());
            }
        });
    }

    public void updateMessage(Message message, String sessionId) {

        _isLoading.postValue(true);

        Log.v("unsyncedMessage",message.toString());
        Log.v("unsyncedMessages",unsyncedMessages.toString());


        for (Message m : unsyncedMessages) {
            if (m.getId().equals(message.getId())) {
                m.setMessage(message.getMessage());
                _isLoading.postValue(false);
                _isMessageUpdated.postValue(true);
                Log.v("LocalUpdate","Message was Updated Locally");
                Log.v("updatedUnsyncMessages",unsyncedMessages.toString());
                return; // Message found and updated locally, no need to update remotely
            }
        }

        Log.v("isUpdatedNotFound","Heading to Remote");

        // If not found locally, update remotely
        updateMessageUseCase.execute(message, sessionId, new MessageRepository.MessageListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                _isLoading.postValue(false);
                updateMessageInLiveData(message);
                _isMessageUpdated.postValue(true);
            }

            @Override
            public void onError(Exception exception) {
                _isLoading.postValue(false);
                _isMessageUpdated.postValue(false);
                _errorMessage.postValue(exception.getMessage());

            }
        });
    }

    public boolean isNotSentByMe(Message message) {
        String uid  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        return !message.getSenderUId().equals(uid);
    }

    public List<Message> getLiveMessagesIfAvailable() {

        if(unsyncedMessages!=null && !unsyncedMessages.isEmpty())
            return unsyncedMessages;

        return null;
    }

    public boolean isIdle () {
        return !isLoading.getValue().equals(true);
    }

    public void markLiveMessageAsSeen(Message message) {
        _messages.getValue().stream()
                .filter(message::equals)
                .forEach(message2 -> message2.setSeen(true));
    }

    private void updateMessageInLiveData(Message updatedMessage) {
        List<Message> currentList = _messages.getValue();
        if (currentList != null) {
            List<Message> updatedList = new ArrayList<>(currentList);
            for (int i = 0; i < updatedList.size(); i++) {
                if (updatedList.get(i).getId().equals(updatedMessage.getId())) {
                    updatedList.get(i).setMessage(updatedMessage.getMessage());
                    break;
                }
            }
            _messages.postValue(updatedList);
        }
    }

    public void consumeMessageUpdate() {
        _isMessageUpdated.setValue(null);
    }
}
