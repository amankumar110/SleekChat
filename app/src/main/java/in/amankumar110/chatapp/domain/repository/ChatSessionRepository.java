package in.amankumar110.chatapp.domain.repository;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.List;

import in.amankumar110.chatapp.domain.common.Result;
import in.amankumar110.chatapp.models.chat.ChatSession;

public interface ChatSessionRepository {

    void createChatSession(ChatSession senderSession, ChatSession receiverSession, ChatSessionListener<Void> listener);
    void getChatSessions(String uid, ChatSessionListener<List<ChatSession>> listener);
    void updateLastMessage(ChatSession chatSession, String lastMessage, ChatSessionListener<Void> listener);
    void deleteSession(String uid, ChatSessionListener<Void> listener);
    String generateSessionId(String senderUid, String receiverUid);

    interface ChatSessionListener<T> {
        void onSuccess(T result);
        void onError(Exception exception);
    }
}
