package in.amankumar110.chatapp.domain.usecases.realtimemessaging;

import javax.inject.Inject;

import in.amankumar110.chatapp.domain.repository.RealtimeMessageRepository;
import in.amankumar110.chatapp.models.chat.Message;

public class SendMessageUseCase {

    private final RealtimeMessageRepository repository;

    @Inject
    public SendMessageUseCase(RealtimeMessageRepository repository) {
        this.repository = repository;
    }

    public void execute(Message message, String sessionId, RealtimeMessageRepository.RealtimeMessageListener<Void> listener) {
        repository.addMessage(message,sessionId,listener);
    }

}
