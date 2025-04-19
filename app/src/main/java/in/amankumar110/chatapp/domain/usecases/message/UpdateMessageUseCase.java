package in.amankumar110.chatapp.domain.usecases.message;

import in.amankumar110.chatapp.domain.repository.MessageRepository;
import in.amankumar110.chatapp.models.chat.Message;
import jakarta.inject.Inject;

public class UpdateMessageUseCase {

    private final MessageRepository messageRepository;

    @Inject
    public UpdateMessageUseCase(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void execute(Message message, String sessionId, MessageRepository.MessageListener<Void> listener) {
        messageRepository.updateMessage(message, sessionId,listener);
    }

}
