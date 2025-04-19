package in.amankumar110.chatapp.viewmodels.chat;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.amankumar110.chatapp.domain.repository.ChatSessionRepository;
import in.amankumar110.chatapp.domain.usecases.chat.CreateSessionUseCase;
import in.amankumar110.chatapp.domain.usecases.chat.GetSessionsUseCase;
import in.amankumar110.chatapp.domain.usecases.chat.UpdateLastMessageUseCase;
import in.amankumar110.chatapp.models.chat.ChatSession;

@HiltViewModel
public class ChatSessionViewModel extends ViewModel {

    private final CreateSessionUseCase createSessionUseCase;
    private final GetSessionsUseCase getSessionsUseCase;
    private final UpdateLastMessageUseCase updateLastMessageUseCase;
    private final MutableLiveData<Boolean> _isSessionCreated = new MutableLiveData<>(null);
    public LiveData<Boolean> isSessionCreated = _isSessionCreated;
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>(null);
    public LiveData<String> errorMessage = _errorMessage;
    private final MutableLiveData<List<ChatSession>> _sessions = new MutableLiveData<>(null);
    public LiveData<List<ChatSession>> sessions = _sessions;

    @Inject
    public ChatSessionViewModel(CreateSessionUseCase createSessionUseCase, GetSessionsUseCase getSessionsUseCase, UpdateLastMessageUseCase updateLastMessageUseCase) {
        this.createSessionUseCase = createSessionUseCase;
        this.getSessionsUseCase = getSessionsUseCase;
        this.updateLastMessageUseCase = updateLastMessageUseCase;
    }

    public void createSession(String senderPhoneNumber, String receiverPhoneNumber) {

        createSessionUseCase.execute(senderPhoneNumber, receiverPhoneNumber,
                new ChatSessionRepository.ChatSessionListener<>() {

                    @Override
                    public void onSuccess(Void result) {
                        _isSessionCreated.postValue(true);
                    }

                    @Override
                    public void onError(Exception exception) {
                        _isSessionCreated.postValue(false);
                        _errorMessage.postValue(exception.getMessage());
                    }
                });
    }

    public void getSessions(String uid) {

        getSessionsUseCase.execute(uid, new ChatSessionRepository.ChatSessionListener<>() {
            @Override
            public void onSuccess(List<ChatSession> result) {
                _sessions.postValue(result);
            }

            @Override
            public void onError(Exception exception) {
                _sessions.postValue(new ArrayList<>());
                _errorMessage.postValue(exception.getMessage());
            }
        });
    }

    public void updateLastMessage(ChatSession chatSession, String lastMessage) {

        updateLastMessageUseCase.execute(chatSession, lastMessage, new ChatSessionRepository.ChatSessionListener<Void>() {
            @Override
            public void onSuccess(Void result) {

                Log.v("CSVM","Last message updated");
            }

            @Override
            public void onError(Exception exception) {
                Log.v("CSVM","Last message Failed To update");
                Log.v("CSVM: reason", exception.getMessage());
            }
        });
    }

}
