package in.amankumar110.chatapp.domain.usecases.message;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import in.amankumar110.chatapp.domain.repository.MessageRepository;
import in.amankumar110.chatapp.models.chat.Message;

public class GetMessagesUseCase {

    private final MessageRepository messageRepository;

    @Inject
    public GetMessagesUseCase(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void execute(String sessionId, MessageRepository.MessageListener<List<Message>> listener) {

        messageRepository.getMessages(sessionId, new MessageRepository.MessageListener<>() {
            @Override
            public void onSuccess(List<Message> result) {

                result.sort(Comparator.comparingLong(Message::getSentAt));
                listener.onSuccess(result);
            }

            @Override
            public void onError(Exception exception) {
                listener.onError(exception);
            }
        });
    }
}
