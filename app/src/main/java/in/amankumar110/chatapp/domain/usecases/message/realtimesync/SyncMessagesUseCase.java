package in.amankumar110.chatapp.domain.usecases.message.realtimesync;

import java.util.List;

import in.amankumar110.chatapp.domain.repository.MessageRepository;
import in.amankumar110.chatapp.domain.repository.RealtimeMessageRepository;
import in.amankumar110.chatapp.models.chat.Message;
import jakarta.inject.Inject;

public class SyncMessagesUseCase {

    private final MessageRepository messageRepository;
    private final RealtimeMessageRepository realtimeMessageRepository;

    @Inject
    public SyncMessagesUseCase(MessageRepository messageRepository, RealtimeMessageRepository realtimeMessageRepository) {
        this.messageRepository = messageRepository;
        this.realtimeMessageRepository = realtimeMessageRepository;
    }

    public void execute(List<Message> messageList, String sessionId, MessageRepository.MessageListener<Void> messageListener) {
        addMessagesToFirestoreDatabase(messageList, sessionId, messageListener);
    }

    private void addMessagesToFirestoreDatabase(List<Message> messageList, String sessionId, MessageRepository.MessageListener<Void> messageListener) {


        if(messageList==null || messageList.isEmpty()) {
            messageListener.onSuccess(null);
            return;
        }
        messageRepository.addMessages(messageList, sessionId, new MessageRepository.MessageListener<>() {
            @Override
            public void onSuccess(Void result) {
                deleteMessagesFromRealtimeDatabase(sessionId,messageListener);
            }

            @Override
            public void onError(Exception exception) {
                messageListener.onError(exception);
            }
        });
    }

    private void deleteMessagesFromRealtimeDatabase(String sessionId, MessageRepository.MessageListener<Void> listener) {

        realtimeMessageRepository.deleteAllMessages(sessionId, new RealtimeMessageRepository.RealtimeMessageListener<Void>() {

            @Override
            public void onSuccess(Void data) {
                listener.onSuccess(null);
            }

            @Override
            public void onError(Exception exception) {
                listener.onError(exception);
            }
        });
    }

}
