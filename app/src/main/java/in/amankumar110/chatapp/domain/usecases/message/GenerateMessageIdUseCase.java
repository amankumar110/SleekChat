package in.amankumar110.chatapp.domain.usecases.message;

import javax.inject.Inject;

import in.amankumar110.chatapp.domain.repository.RealtimeMessageRepository;

public class GenerateMessageIdUseCase {

    private final RealtimeMessageRepository realtimeMessageRepository;

    @Inject
    public GenerateMessageIdUseCase(RealtimeMessageRepository realtimeMessageRepository) {
        this.realtimeMessageRepository = realtimeMessageRepository;
    }

    public String execute(String senderId, String receiverId) {
        return realtimeMessageRepository.generateUniqueId(senderId,receiverId, System.currentTimeMillis());
    }

}
