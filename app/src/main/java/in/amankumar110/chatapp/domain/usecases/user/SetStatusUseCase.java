package in.amankumar110.chatapp.domain.usecases.user;

import in.amankumar110.chatapp.domain.repository.RealtimeStatusRepository;
import in.amankumar110.chatapp.models.user.UserStatus;
import jakarta.inject.Inject;

public class SetStatusUseCase {

    private final RealtimeStatusRepository realtimeStatusRepository;

    @Inject
    public SetStatusUseCase(RealtimeStatusRepository realtimeStatusRepository) {
        this.realtimeStatusRepository = realtimeStatusRepository;
    }

    public void execute(String uid, UserStatus userStatus, RealtimeStatusRepository.RealtimeStatusListener<Void> listener) {
        realtimeStatusRepository.setStatus(uid,userStatus,listener);
    }
}
