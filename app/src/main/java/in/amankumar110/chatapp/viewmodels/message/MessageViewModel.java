package in.amankumar110.chatapp.viewmodels.message;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.amankumar110.chatapp.domain.repository.MessageRepository;
import in.amankumar110.chatapp.domain.usecases.message.messages.GetMessagesUseCase;
import in.amankumar110.chatapp.domain.usecases.message.messageseen.MarkMessageAsSeenUseCase;
import in.amankumar110.chatapp.domain.usecases.message.messages.SendMessageUseCase;
import in.amankumar110.chatapp.models.chat.Message;

@HiltViewModel
public class MessageViewModel extends ViewModel {

    private final MarkMessageAsSeenUseCase markMessageAsSeenUseCase;
    private final SendMessageUseCase sendMessageUseCase;
    private final GetMessagesUseCase getMessagesUseCase;

    private final MutableLiveData<List<Message>> _messages = new MutableLiveData<>(null);
    public LiveData<List<Message>> messages = _messages;

    private MutableLiveData<Boolean> _isMessageSent = new MutableLiveData<>(false);
    public LiveData<Boolean> isMessageSent = _isMessageSent;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>(null);
    public LiveData<String> errorMessage = _errorMessage;

    private MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    @Inject
    public MessageViewModel(MarkMessageAsSeenUseCase markMessageAsSeenUseCase, SendMessageUseCase sendMessageUseCase, GetMessagesUseCase getMessagesUseCase) {
        this.markMessageAsSeenUseCase = markMessageAsSeenUseCase;
        this.sendMessageUseCase = sendMessageUseCase;
        this.getMessagesUseCase = getMessagesUseCase;
    }

    public void sendMessage(Message message, String sessionId) {

        _isMessageSent.postValue(false);
        _isLoading.postValue(true);

        sendMessageUseCase.execute(message, sessionId, new MessageRepository.MessageListener<>() {
            @Override
            public void onSuccess(Void result) {
                _isMessageSent.postValue(true);
                _isLoading.postValue(false);
            }

            @Override
            public void onError(Exception exception) {
                _errorMessage.postValue(exception.getMessage());
                _isLoading.postValue(false);
            }
        });
    }

    public void getMessages(String sessionId) {

        _isLoading.postValue(true);

        getMessagesUseCase.execute(sessionId, new MessageRepository.MessageListener<>() {
            @Override
            public void onSuccess(List<Message> result) {
                _messages.postValue(result);
                _isLoading.postValue(false);
            }

            @Override
            public void onError(Exception exception) {
                _errorMessage.postValue(exception.getMessage());
                _isLoading.postValue(false);
            }
        });
    }



}
