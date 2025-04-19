package in.amankumar110.chatapp.domain.usecases.realtimemessaging;

import java.util.List;

import javax.inject.Inject;

import in.amankumar110.chatapp.domain.repository.RealtimeMessageRepository;
import in.amankumar110.chatapp.models.chat.Message;

public class GetRealtimeMessageUseCase {

    private final RealtimeMessageRepository repository;

    @Inject
    public GetRealtimeMessageUseCase(RealtimeMessageRepository repository) {
        this.repository = repository;
    }

    public void execute(String sessionId, RealtimeMessageRepository.RealtimeMessageListener<Message> listener) {
        repository.onNewMessage(sessionId,listener);
    }

}
