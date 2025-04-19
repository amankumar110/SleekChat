package in.amankumar110.chatapp.domain.repository;

import java.util.List;

import in.amankumar110.chatapp.models.chat.Message;

public interface RealtimeMessageRepository {

    void addMessage(Message message,String sessionId, RealtimeMessageListener<Void> listener);
    void onNewMessage(String sessionId, RealtimeMessageListener<Message> listener);
    void deleteAllMessages(String sessionId, RealtimeMessageListener<Void> listener);
    String generateUniqueId(String sender, String receiver, Long sentAt);

    interface RealtimeMessageListener<T>{
        void onSuccess(T data);
        void onError(Exception exception);
    }
}
