package in.amankumar110.chatapp.domain.usecases.user;

import android.util.Log;

import in.amankumar110.chatapp.domain.repository.UserRepository;
import jakarta.inject.Inject;

public class SaveMessagingTokenUseCase {

    private final UserRepository userRepository;

    @Inject
    public SaveMessagingTokenUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    public void execute(String uId, String token, UserRepository.UserListener<Void> userListener) {

        Log.v("Messaging","I Got The Work Done");
        Log.v("Messaging:uId",uId);
        Log.v("Messaging:token",token);
        userRepository.saveMessagingToken(uId, token, userListener);
    }
}
