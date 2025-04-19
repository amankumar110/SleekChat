package in.amankumar110.chatapp.data.repository;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.Display;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthProvider;

import javax.inject.Inject;

import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.data.remote.PhoneAuthService;
import in.amankumar110.chatapp.domain.repository.PhoneAuthRepository;

public class PhoneAuthRepositoryImpl implements PhoneAuthRepository {

    private final PhoneAuthService phoneAuthService;


    @Inject
    public PhoneAuthRepositoryImpl(PhoneAuthService phoneAuthService) {
        this.phoneAuthService = phoneAuthService;
    }

    @Override
    public void sendOtp(String phoneNumber, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks) {
      // Sending Otp using FirebaseAuthService
        phoneAuthService.sendVerificationCode(phoneNumber,callbacks);
    }

    @Override
    public void verifyOtp(String verificationId, String otp, OnCompleteListener<AuthResult> onCompleteListener) {
        phoneAuthService.verifyOtp(verificationId, otp,onCompleteListener);
    }

    @Override
    public String getErrorMessage(Exception e, Context context) {

        return context.getString(R.string.unknown_exception_message);
    }
}
