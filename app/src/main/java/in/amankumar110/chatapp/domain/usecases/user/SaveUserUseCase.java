package in.amankumar110.chatapp.domain.usecases.user;

import in.amankumar110.chatapp.domain.repository.UserRepository;
import in.amankumar110.chatapp.models.user.User;
import jakarta.inject.Inject;

public class SaveUserUseCase {

    private final UserRepository userRepository;

    @Inject
    public SaveUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(User user, UserRepository.UserListener<Void> listener) {
        userRepository.saveUser(user,listener);
    }
}
