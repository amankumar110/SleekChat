package in.amankumar110.chatapp.domain.usecases.message;

import javax.inject.Inject;

import in.amankumar110.chatapp.domain.repository.MessageRepository;
import in.amankumar110.chatapp.models.chat.Message;

public class SendMessageUseCase {


    private final MessageRepository messageRepository;

    @Inject
    public SendMessageUseCase(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void execute(Message message, String sessionId, MessageRepository.MessageListener<Void> listener) {
        messageRepository.addMessage(message,sessionId,listener);
    }

}
