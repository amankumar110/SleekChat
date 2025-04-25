package in.amankumar110.chatapp.domain.usecases.message.messagestatus;

import java.util.List;

import in.amankumar110.chatapp.domain.repository.MessageRepository;
import in.amankumar110.chatapp.models.chat.ChatSession;
import in.amankumar110.chatapp.models.chat.Message;
import jakarta.inject.Inject;

public class UpdateMessagesStatusUseCase {

    private final MessageRepository messageRepository;

    @Inject
    public UpdateMessagesStatusUseCase(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void execute(List<Message> messages, ChatSession chatSession, MessageRepository.MessageListener<Void> listener) {
        messageRepository.updateMessagesStatus(messages, chatSession,listener);
    }
}
