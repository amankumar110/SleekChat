package in.amankumar110.chatapp.domain.usecases.message.updates;

import in.amankumar110.chatapp.domain.repository.RealtimeMessageUpdateRepository;
import in.amankumar110.chatapp.models.chat.Message;
import jakarta.inject.Inject;

public class GetNewUpdatedMessageUseCase {

    private final RealtimeMessageUpdateRepository repository;

    @Inject
    public GetNewUpdatedMessageUseCase(RealtimeMessageUpdateRepository repository) {
        this.repository = repository;
    }

    public void execute(String sessionId, RealtimeMessageUpdateRepository.RealtimeMessageUpdateListener<Message> listener) {
        repository.onNewUpdatedMessage(sessionId,listener);
    }

}
