package in.amankumar110.chatapp.domain.usecases.auth;

import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.PhoneAuthProvider;

import javax.inject.Inject;

import in.amankumar110.chatapp.domain.repository.PhoneAuthRepository;
import in.amankumar110.chatapp.domain.repository.UserRepository;
import in.amankumar110.chatapp.exceptions.InvalidPhoneNumberException;
import in.amankumar110.chatapp.exceptions.UserNotExistException;
import in.amankumar110.chatapp.models.user.User;

public class SendOtpUseCase {

    private final PhoneAuthRepository phoneAuthRepository;
    private final UserRepository userRepository;

    @Inject
    public SendOtpUseCase(PhoneAuthRepository phoneAuthRepository, UserRepository userRepository1) {
        this.phoneAuthRepository = phoneAuthRepository;
        this.userRepository = userRepository1;
    }

    public void execute(String phoneNumber, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks) {

        if(!PhoneAuthRepository.isPhoneNumberValid(phoneNumber))
            throw new InvalidPhoneNumberException("Invalid Phone Number");

        userRepository.getUserByPhoneNumber(phoneNumber, new UserRepository.UserListener<>() {
            @Override
            public void onSuccess(User user) {
                Log.v("ABC","User Exists");
                callbacks.onVerificationFailed(new FirebaseAuthUserCollisionException("User Already Exists","Exist"));
            }

            @Override
            public void onError(Exception exception) {

                if(exception instanceof UserNotExistException) {

                    Log.v("ABC","Sending OTP");
                    phoneAuthRepository.sendOtp(phoneNumber, callbacks);

                }else {
                    Log.v("ABC","Exception Occured");
                    callbacks.onVerificationFailed(new FirebaseNetworkException("Unexpected Exception Occured"));
                }
            }
        });

    }

}
