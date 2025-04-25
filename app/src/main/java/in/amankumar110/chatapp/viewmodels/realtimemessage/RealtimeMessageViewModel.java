package in.amankumar110.chatapp.viewmodels.realtimemessage;

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
import in.amankumar110.chatapp.domain.usecases.message.messages.GenerateMessageIdUseCase;
import in.amankumar110.chatapp.domain.usecases.message.messages.GetMessagesUseCase;
import in.amankumar110.chatapp.domain.usecases.message.messagestatus.MarkMessageAsSeenUseCase;
import in.amankumar110.chatapp.domain.usecases.message.messagestatus.UpdateMessagesStatusUseCase;
import in.amankumar110.chatapp.domain.usecases.message.realtimesync.SyncMessagesUseCaseWrapper;
import in.amankumar110.chatapp.domain.usecases.message.messages.UpdateMessageUseCase;
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
    private final UpdateMessagesStatusUseCase updateMessagesStatusUseCase;

    private final MutableLiveData<List<Message>> _messages = new MutableLiveData<>(null);
    public LiveData<List<Message>> messages = _messages;

    private final MutableLiveData<Message> _newMessage = new MutableLiveData<>(null);
    public LiveData<Message> newMessage = _newMessage;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>(null);
    public LiveData<String> errorMessage = _errorMessage;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<Boolean> _isMessageSent = new MutableLiveData<>(null);
    public LiveData<Boolean> isMessageSent = _isMessageSent;
    private final MutableLiveData<Boolean> _messagesMarkedAsRead = new MutableLiveData<>(null);
    public LiveData<Boolean> messagesMarkedAsRead = _messagesMarkedAsRead;
    private final List<Message> unsyncedMessages = new ArrayList<>();
    private final MutableLiveData<Boolean> _isMessageUpdated = new MutableLiveData<>(null);
    public LiveData<Boolean> isMessageUpdated = _isMessageUpdated;
    private final MutableLiveData<Boolean> _messagesStatusUpdated = new MutableLiveData<>(null);
    public LiveData<Boolean> messagesStatusUpdated = _messagesStatusUpdated;

    @Inject
    public RealtimeMessageViewModel(GetRealtimeMessageUseCase getRealtimeMessageUseCase, SendMessageUseCase sendMessageUseCase, GetMessagesUseCase getMessagesUseCase, GenerateMessageIdUseCase generateMessageIdUseCase, SyncMessagesUseCaseWrapper syncMessagesUseCaseWrapper, MarkMessageAsSeenUseCase markMessagesAsSeenUseCase, UpdateMessageUseCase updateMessageUseCase, UpdateMessagesStatusUseCase updateMessagesStatusUseCase) {
        this.getRealtimeMessageUseCase = getRealtimeMessageUseCase;
        this.sendMessageUseCase = sendMessageUseCase;
        this.getMessagesUseCase = getMessagesUseCase;
        this.markMessagesAsSeenUseCase = markMessagesAsSeenUseCase;
        this.generateMessageIdUseCase = generateMessageIdUseCase;
        this.syncMessagesUseCaseWrapper = syncMessagesUseCaseWrapper;
        this.updateMessagesStatusUseCase = updateMessagesStatusUseCase;
    }

    public void addMessage(Message message, String sessionId) {

        _isLoading.postValue(true);

        sendMessageUseCase.execute(message, sessionId, new RealtimeMessageRepository.RealtimeMessageListener<>() {
            @Override
            public void onSuccess(Void data) {
                _isLoading.postValue(false);
                _isMessageSent.postValue(true);
            }

            @Override
            public void onError(Exception exception) {
                _isLoading.postValue(false);
                _errorMessage.postValue(exception.getMessage());
                _isMessageSent.postValue(false);
            }
        });
    }

    public void observeNewMessage(String sessionId) {

        _isLoading.postValue(true);

        getRealtimeMessageUseCase.execute(sessionId, new RealtimeMessageRepository.RealtimeMessageListener<Message>() {
            @Override
            public void onSuccess(Message data) {
                Log.v("MessageFromViewModel",data.toString());
                _isLoading.postValue(false);
                unsyncedMessages.add(data);
                _newMessage.postValue(data);
            }

            @Override
            public void onError(Exception exception) {
                Log.v("MessageFromViewModel",exception.getMessage());
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

    public void syncMessages(List<Message> realtimeMessages,
                             List<Message> updateMessages,
                             List<Message> deleteMessages,
                             String sessionId) {

        syncMessagesUseCaseWrapper.syncInBackground(realtimeMessages,updateMessages,deleteMessages,sessionId);
        unsyncedMessages.clear();
        updateMessages.clear();
        deleteMessages.clear();
    }

    public void updateSenderMessagesStatus(ChatSession chatSession, Message.MessageStatus messageStatus) {

        _isLoading.postValue(true);

        List<Message> filteredMessages = Objects.requireNonNull(_messages.getValue()).stream()
                .filter(this::isNotSentByMe)
                .collect(Collectors.toList());

        if(filteredMessages.isEmpty()) {
            _isLoading.postValue(false);
            return;
        }

        filteredMessages.forEach(message -> message.setMessageStatus(messageStatus.getStatus()));

        updateMessagesStatusUseCase.execute(_messages.getValue(), chatSession, new MessageRepository.MessageListener<Void>() {

            @Override
            public void onSuccess(Void result) {

                _isLoading.postValue(false);
                _messagesStatusUpdated.postValue(true);
            }

            @Override
            public void onError(Exception exception) {
                _isLoading.postValue(false);
                _messagesStatusUpdated.postValue(false);
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


    public void resetMessageSent() {
        this._isMessageSent.setValue(null);
    }

    public void resetNewMessage() {
        this._newMessage.setValue(null);
    }

    public void resetMessagesMarkedAsRead() {
        this._messagesMarkedAsRead.setValue(null);
    }
    public void setMessagesStatus(Message.MessageStatus messageStatus) {
        this.unsyncedMessages.forEach(message -> {
            if(Message.MessageStatus.shouldUpdateStatus(message.getMessageStatus(),messageStatus.getStatus()))
                message.setMessageStatus(messageStatus.getStatus());
        });
    }
}
