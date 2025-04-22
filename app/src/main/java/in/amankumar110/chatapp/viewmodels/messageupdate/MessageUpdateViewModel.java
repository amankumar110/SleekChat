package in.amankumar110.chatapp.viewmodels.messageupdate;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.amankumar110.chatapp.domain.repository.RealtimeMessageUpdateRepository;
import in.amankumar110.chatapp.domain.usecases.message.updates.AddToDeletedMessageUseCase;
import in.amankumar110.chatapp.domain.usecases.message.updates.AddToUpdatedMessageUseCase;
import in.amankumar110.chatapp.domain.usecases.message.updates.GetNewDeletedMessageUseCase;
import in.amankumar110.chatapp.domain.usecases.message.updates.GetNewUpdatedMessageUseCase;
import in.amankumar110.chatapp.models.chat.Message;
import jakarta.inject.Inject;

@HiltViewModel
public class MessageUpdateViewModel extends ViewModel {

    private final AddToUpdatedMessageUseCase addToUpdatedMessageUseCase;
    private final AddToDeletedMessageUseCase addToDeletedMessageUseCase;
    private final GetNewDeletedMessageUseCase getNewDeletedMessageUseCase;
    private final GetNewUpdatedMessageUseCase getNewUpdatedMessageUseCase;


    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public final LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>(null);
    public final LiveData<String> errorMessage = _errorMessage;

    private final MutableLiveData<Boolean> _isMessageUpdated = new MutableLiveData<>(null);
    public final LiveData<Boolean> isMessageUpdated = _isMessageUpdated;

    private final MutableLiveData<Boolean> _isMessageDeleted = new MutableLiveData<>(null);
    public final LiveData<Boolean> isMessageDeleted = _isMessageDeleted;
    private final MutableLiveData<Message> _newUpdatedMessage = new MutableLiveData<>(null);
    public final LiveData<Message> newUpdatedMessage = _newUpdatedMessage;
    private final MutableLiveData<Message> _newDeletedMessage = new MutableLiveData<>(null);
    public final LiveData<Message> newDeletedMessage = _newDeletedMessage;


    private final List<Message> updateMessages = new ArrayList<>();
    private final List<Message> deletedMessages = new ArrayList<>();

    @Inject
    public MessageUpdateViewModel(AddToUpdatedMessageUseCase addToUpdatedMessageUseCase, AddToDeletedMessageUseCase addToDeletedMessageUseCase, GetNewDeletedMessageUseCase getNewDeletedMessageUseCase, GetNewUpdatedMessageUseCase getNewUpdatedMessageUseCase) {
        this.addToUpdatedMessageUseCase = addToUpdatedMessageUseCase;
        this.addToDeletedMessageUseCase = addToDeletedMessageUseCase;
        this.getNewDeletedMessageUseCase = getNewDeletedMessageUseCase;
        this.getNewUpdatedMessageUseCase = getNewUpdatedMessageUseCase;
    }

    public void updateMessage(Message message, String sessionId) {

        _isLoading.postValue(true);

        addToUpdatedMessageUseCase.execute(message, sessionId, new RealtimeMessageUpdateRepository.RealtimeMessageUpdateListener<Void>() {
            @Override
            public void onSuccess(Void data) {

                Log.v("MessageFromVM","I got it even till here");
                _isLoading.postValue(false);
                _isMessageUpdated.postValue(true);
            }

            @Override
            public void onError(Exception exception) {

                _isLoading.postValue(false);
                _errorMessage.postValue(exception.getMessage());
                _isMessageUpdated.postValue(false);
            }
        });
    }

    public void deleteMessage(Message message, String sessionId) {

        _isLoading.postValue(true);

        addToDeletedMessageUseCase.execute(message, sessionId, new RealtimeMessageUpdateRepository.RealtimeMessageUpdateListener<Void>() {
            @Override
            public void onSuccess(Void data) {

                _isLoading.postValue(false);
                _isMessageDeleted.postValue(true);
            }

            @Override
            public void onError(Exception exception) {

                _isLoading.postValue(false);
                _errorMessage.postValue(exception.getMessage());
                _isMessageDeleted.postValue(false);
            }
        });
    }

    public void onNewUpdatedMessage(String sessionId) {

        _isLoading.postValue(true);

        getNewUpdatedMessageUseCase.execute(sessionId, new RealtimeMessageUpdateRepository.RealtimeMessageUpdateListener<Message>() {
            @Override
            public void onSuccess(Message data) {

                updateMessages.add(data);
                _isLoading.postValue(false);
                _newUpdatedMessage.postValue(data);
            }

            @Override
            public void onError(Exception exception) {

                _isLoading.postValue(false);
                _errorMessage.postValue(exception.getMessage());
                _newUpdatedMessage.postValue(null);
            }
        });
    }

    public void onNewDeletedMessage(String sessionId) {

        _isLoading.postValue(true);

        getNewDeletedMessageUseCase.execute(sessionId, new RealtimeMessageUpdateRepository.RealtimeMessageUpdateListener<Message>() {
            @Override
            public void onSuccess(Message data) {
                deletedMessages.add(data);
                _isLoading.postValue(false);
                _newDeletedMessage.postValue(data);
            }

            @Override
            public void onError(Exception exception) {

                _isLoading.postValue(false);
                _errorMessage.postValue(exception.getMessage());
                _newDeletedMessage.postValue(null);
            }
        });
    }

    public boolean isIdle() {
        return Boolean.FALSE.equals(this.isLoading.getValue());
    }

    public void reset() {
        this._isLoading.postValue(null);
        this._errorMessage.postValue(null);
        this._isMessageUpdated.postValue(null);
        this._isMessageDeleted.postValue(null);
        this._newUpdatedMessage.postValue(null);
        this._newDeletedMessage.postValue(null);
    }

    public List<Message> getUpdatedMessages() {
        return this.updateMessages;
    }

    public List<Message> getDeletedMessages() {
        return this.deletedMessages;
    }

    public void resetUpdatedMessage() {
        this._newUpdatedMessage.setValue(null);
    }

    public void resetDeletedMessage() {
        this._newDeletedMessage.setValue(null);
    }
}
