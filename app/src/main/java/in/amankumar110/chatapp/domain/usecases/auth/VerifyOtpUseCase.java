package in.amankumar110.chatapp.domain.usecases.auth;

import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;

import javax.inject.Inject;

import in.amankumar110.chatapp.domain.repository.PhoneAuthRepository;

public class VerifyOtpUseCase {

    private final PhoneAuthRepository phoneAuthRepository;

    @Inject
    public VerifyOtpUseCase(PhoneAuthRepository phoneAuthRepository) {
        this.phoneAuthRepository = phoneAuthRepository;
    }

    public void verifyOtp(String verificationId, String code, OnCompleteListener<AuthResult> completeListener) {
        phoneAuthRepository.verifyOtp(verificationId,code,completeListener);
    }

    public String getErrorMessage(Exception e, Context context) {
        return phoneAuthRepository.getErrorMessage(e, context);
    }

}
