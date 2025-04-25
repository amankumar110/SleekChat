package in.amankumar110.chatapp.data.repository;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;

import in.amankumar110.chatapp.data.remote.ChatSessionService;
import in.amankumar110.chatapp.domain.common.Result;
import in.amankumar110.chatapp.domain.repository.ChatSessionRepository;
import in.amankumar110.chatapp.exceptions.ChatSessionException;
import in.amankumar110.chatapp.models.chat.ChatSession;

public class ChatSessionRepositoryImpl implements ChatSessionRepository {

    private final ChatSessionService chatSessionService;

    @Inject
    public ChatSessionRepositoryImpl(ChatSessionService chatSessionService) {
        this.chatSessionService = chatSessionService;
    }


    @Override
    public void createChatSession(ChatSession senderSession, ChatSession receiverSession, ChatSessionListener<ChatSession> listener) {

        chatSessionService.createChatSession(senderSession, receiverSession, task -> {

            if(task.isSuccessful())
                listener.onSuccess(senderSession);
            else
                handleError(listener, "Chat session could not be created");
        });
    }

    @Override
    public void getChatSessions(String uid, ChatSessionListener<List<ChatSession>> listener) {

        chatSessionService.getChatSessions(uid, task -> {
            if (!task.isSuccessful()) {
                handleError(listener, "Chat sessions could not be fetched");
                return;
            }

            List<ChatSession> sessions = task.getResult().getDocuments().stream()
                    .map(doc -> doc.toObject(ChatSession.class))
                    .collect(Collectors.toList());

            listener.onSuccess(sessions);
        });
    }

    @Override
    public void getChatSession(String uid, String sessionId, ChatSessionListener<ChatSession> chatSessionListener) {

        chatSessionService.getChatSession(uid, sessionId, task -> {

            if(task.isSuccessful()) {

                ChatSession chatSession = task.getResult().toObject(ChatSession.class);
                chatSessionListener.onSuccess(chatSession);
            } else {
                handleError(chatSessionListener, "Chat session could not be fetched");
            }

        });

    }

    public void updateLastMessage(ChatSession chatSession, String lastMessage, ChatSessionListener<Void> listener) {

        chatSessionService.updateLastMessage(chatSession, lastMessage, task -> {
            if (!task.isSuccessful()) {
                handleError(listener, task.getException().getMessage());
                return;
            }

            listener.onSuccess(null);
        });
    }

    @Override
    public void deleteSession(String sessionId, ChatSessionListener<Void> listener) {
        chatSessionService.deleteSession(sessionId, task -> {
            if (!task.isSuccessful()) {
                handleError(listener, "Session could not be deleted");
                return;
            }
            listener.onSuccess(null);
        });
    }

    private void handleError(ChatSessionListener<?> listener, String errorMessage) {
        listener.onError(new ChatSessionException(errorMessage));
    }

    @Override
    public String generateSessionId(String senderUid, String receiverUid) {
        return senderUid.compareTo(receiverUid) < 0
                ? senderUid + "_" + receiverUid
                : receiverUid + "_" + senderUid;
    }

}
