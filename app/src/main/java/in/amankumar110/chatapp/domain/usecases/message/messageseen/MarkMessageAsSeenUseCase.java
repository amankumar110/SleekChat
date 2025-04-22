package in.amankumar110.chatapp.domain.usecases.message.messageseen;

import java.util.List;

import in.amankumar110.chatapp.domain.repository.MessageRepository;
import in.amankumar110.chatapp.models.chat.ChatSession;
import in.amankumar110.chatapp.models.chat.Message;
import jakarta.inject.Inject;

public class MarkMessageAsSeenUseCase {

    private final MessageRepository messageRepository;

    @Inject
    public MarkMessageAsSeenUseCase(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void execute(List<Message> messageList, ChatSession chatSession, MessageRepository.MessageListener<Void> listener) {
        messageRepository.markMessageAsSeen(messageList, chatSession, listener);
    }
}
