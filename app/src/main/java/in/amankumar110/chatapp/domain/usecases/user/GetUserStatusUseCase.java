package in.amankumar110.chatapp.domain.usecases.user;

import in.amankumar110.chatapp.domain.repository.RealtimeStatusRepository;
import in.amankumar110.chatapp.domain.repository.UserRepository;
import in.amankumar110.chatapp.models.user.UserStatus;
import jakarta.inject.Inject;

public class GetUserStatusUseCase {

    private final RealtimeStatusRepository realtimeStatusRepository;

    @Inject
    public GetUserStatusUseCase(RealtimeStatusRepository realtimeStatusRepository) {

        this.realtimeStatusRepository = realtimeStatusRepository;
    }

    public void execute(String uid, RealtimeStatusRepository.RealtimeStatusListener<UserStatus> listener) {
        realtimeStatusRepository.getStatus(uid,listener);
    }
}
