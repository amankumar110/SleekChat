package in.amankumar110.chatapp.domain.usecases.message.updates;

import in.amankumar110.chatapp.domain.repository.RealtimeMessageUpdateRepository;
import in.amankumar110.chatapp.models.chat.Message;
import jakarta.inject.Inject;

public class AddToUpdatedMessageUseCase {

    private final RealtimeMessageUpdateRepository repository;

    @Inject
    public AddToUpdatedMessageUseCase(RealtimeMessageUpdateRepository repository) {
        this.repository = repository;
    }

    public void execute(Message message, String sessionId, RealtimeMessageUpdateRepository.RealtimeMessageUpdateListener<Void> listener) {
        repository.addToUpdatedMessages(message,sessionId,listener);
    }

}
