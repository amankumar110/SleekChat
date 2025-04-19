package in.amankumar110.chatapp.domain.usecases.user;

import in.amankumar110.chatapp.domain.repository.PhoneAuthRepository;
import in.amankumar110.chatapp.domain.repository.UserRepository;
import in.amankumar110.chatapp.exceptions.InvalidPhoneNumberException;
import in.amankumar110.chatapp.models.user.User;
import jakarta.inject.Inject;

public class GetUserByPhoneUseCase {

    private final UserRepository userRepository;

    @Inject
    public GetUserByPhoneUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public void execute(String phoneNumber, UserRepository.UserListener<User> userListener) {

        if(PhoneAuthRepository.isPhoneNumberValid(phoneNumber))
            userRepository.getUserByPhoneNumber(phoneNumber,userListener);
        else
            userListener.onError(new InvalidPhoneNumberException(phoneNumber+" Is Not A Valid Phone Number"));
    }

}
