package in.amankumar110.chatapp.viewmodels.chat;

import static java.lang.Boolean.FALSE;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.amankumar110.chatapp.domain.repository.ChatSessionRepository;
import in.amankumar110.chatapp.domain.usecases.chatsession.CreateSessionUseCase;
import in.amankumar110.chatapp.domain.usecases.chatsession.GenerateSessionIdUseCase;
import in.amankumar110.chatapp.domain.usecases.chatsession.GetChatSessionUseCase;
import in.amankumar110.chatapp.domain.usecases.chatsession.GetSessionsUseCase;
import in.amankumar110.chatapp.domain.usecases.chatsession.UpdateLastMessageUseCase;
import in.amankumar110.chatapp.models.chat.ChatSession;

@HiltViewModel
public class ChatSessionViewModel extends ViewModel {

    private final CreateSessionUseCase createSessionUseCase;
    private final GetSessionsUseCase getSessionsUseCase;
    private final GetChatSessionUseCase getChatSessionUseCase;
    private final UpdateLastMessageUseCase updateLastMessageUseCase;

    private final MutableLiveData<ChatSession> _createdChatSession = new MutableLiveData<>(null);
    public LiveData<ChatSession> createdChatSession = _createdChatSession;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>(null);
    public LiveData<String> errorMessage = _errorMessage;
    private final MutableLiveData<List<ChatSession>> _sessions = new MutableLiveData<>(null);
    public LiveData<List<ChatSession>> sessions = _sessions;
    private final MutableLiveData<ChatSession> _chatSession = new MutableLiveData<>(null);
    public LiveData<ChatSession> chatSession = _chatSession;

    @Inject
    public ChatSessionViewModel(CreateSessionUseCase createSessionUseCase, GetSessionsUseCase getSessionsUseCase, GetChatSessionUseCase getChatSessionUseCase, UpdateLastMessageUseCase updateLastMessageUseCase) {
        this.createSessionUseCase = createSessionUseCase;
        this.getSessionsUseCase = getSessionsUseCase;
        this.getChatSessionUseCase = getChatSessionUseCase;
        this.updateLastMessageUseCase = updateLastMessageUseCase;
    }

    public void createSession(String senderPhoneNumber, String receiverPhoneNumber) {

        _isLoading.postValue(true);

        createSessionUseCase.execute(senderPhoneNumber, receiverPhoneNumber,
                new ChatSessionRepository.ChatSessionListener<>() {

                    @Override
                    public void onSuccess(ChatSession result) {
                        _isLoading.postValue(false);
                        _createdChatSession.postValue(result);
                    }

                    @Override
                    public void onError(Exception exception) {
                        _isLoading.postValue(false);
                        _errorMessage.postValue(exception.getMessage());
                        _createdChatSession.postValue(null);
                    }
                });
    }


    public void getSessions(String uid) {

        _isLoading.postValue(true);

        getSessionsUseCase.execute(uid, new ChatSessionRepository.ChatSessionListener<>() {
            @Override
            public void onSuccess(List<ChatSession> result) {
                _isLoading.postValue(false);
                _sessions.postValue(result);
            }

            @Override
            public void onError(Exception exception) {
                _isLoading.postValue(false);
                _sessions.postValue(new ArrayList<>());
                _errorMessage.postValue(exception.getMessage());
            }
        });
    }

    public void updateLastMessage(ChatSession chatSession, String lastMessage) {

        _isLoading.postValue(true);

        updateLastMessageUseCase.execute(chatSession, lastMessage, new ChatSessionRepository.ChatSessionListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                _isLoading.postValue(false);

                Log.v("CSVM","Last message updated");
            }

            @Override
            public void onError(Exception exception) {
                _isLoading.postValue(false);
                Log.v("CSVM","Last message Failed To update");
                Log.v("CSVM: reason", exception.getMessage());
            }
        });
    }

    public void getChatSession(String uid, String sessionId) {

        _isLoading.postValue(true);

        getChatSessionUseCase.execute(uid, sessionId, new ChatSessionRepository.ChatSessionListener<ChatSession>() {
            @Override
            public void onSuccess(ChatSession result) {
                _isLoading.postValue(false);
                _chatSession.postValue(result);
            }

            @Override
            public void onError(Exception exception) {
                _isLoading.postValue(false);
                _chatSession.postValue(null);
                _errorMessage.postValue(exception.getMessage());
            }
        });
    }

    public boolean isIdle() {
        return Objects.equals(isLoading.getValue(), FALSE);
    }

    public void reset() {
        _createdChatSession.postValue(null);
        _errorMessage.postValue(null);
        _chatSession.postValue(null);
    }
}
