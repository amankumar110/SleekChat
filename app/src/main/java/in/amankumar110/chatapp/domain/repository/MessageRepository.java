package in.amankumar110.chatapp.domain.repository;

import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import in.amankumar110.chatapp.models.chat.ChatSession;
import in.amankumar110.chatapp.models.chat.Message;

public interface MessageRepository {

    void getMessages(String sessionId,MessageListener<List<Message>> listener);
    void addMessage(Message message, String sessionId, MessageListener<Void> listener);
    void addMessages(List<Message> messages, String sessionId, MessageListener<Void> listener);
    void markMessageAsSeen(List<Message> messages, ChatSession chatSession, MessageListener<Void> listener);
    void updateMessage(Message message, String sessionId, MessageListener<Void> listener);

    interface MessageListener<T> {

        void onSuccess(T result);
        void onError(Exception exception);
    }
}
