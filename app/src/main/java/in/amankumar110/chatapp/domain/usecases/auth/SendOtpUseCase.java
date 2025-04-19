package in.amankumar110.chatapp.domain.usecases.auth;

import android.content.Context;

import com.google.firebase.auth.PhoneAuthProvider;

import javax.inject.Inject;

import in.amankumar110.chatapp.domain.repository.PhoneAuthRepository;
import in.amankumar110.chatapp.exceptions.InvalidPhoneNumberException;

public class SendOtpUseCase {

    private final PhoneAuthRepository phoneAuthRepository;

    @Inject
    public SendOtpUseCase(PhoneAuthRepository phoneAuthRepository) {
        this.phoneAuthRepository = phoneAuthRepository;
    }

    public void sendOtp(String phoneNumber, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks) {

        if(PhoneAuthRepository.isPhoneNumberValid(phoneNumber))
            phoneAuthRepository.sendOtp(phoneNumber,callbacks);
        else
            throw new InvalidPhoneNumberException("Invalid Phone Number");
    }

    public String getErrorMessage(Exception e, Context context) {
        return phoneAuthRepository.getErrorMessage(e, context);
    }
}
