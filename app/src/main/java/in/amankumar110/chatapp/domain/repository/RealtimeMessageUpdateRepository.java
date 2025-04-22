package in.amankumar110.chatapp.domain.repository;

import in.amankumar110.chatapp.models.chat.Message;

public interface RealtimeMessageUpdateRepository {

    void addToUpdatedMessages(Message message, String sessionId,RealtimeMessageUpdateListener<Void> listener);
    void addToDeletedMessages(Message message, String sessionId,RealtimeMessageUpdateListener<Void> listener);
    void onNewDeletedMessage(String sessionId,RealtimeMessageUpdateListener<Message> listener);
    void onNewUpdatedMessage(String sessionId,RealtimeMessageUpdateListener<Message> listener);
    void removeUpdatedMessages(String sessionId,RealtimeMessageUpdateListener<Void> listener);
    void removeDeletedMessages(String sessionId,RealtimeMessageUpdateListener<Void> listener);

    interface RealtimeMessageUpdateListener<T> {
        void onSuccess(T data);
        void onError(Exception exception);
    }
}
