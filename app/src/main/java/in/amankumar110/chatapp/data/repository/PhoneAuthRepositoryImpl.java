package in.amankumar110.chatapp.data.repository;


import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Display;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;

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
        phoneAuthService.sendVerificationCode(phoneNumber, callbacks);
    }

    @Override
    public void verifyOtp(String verificationId, String otp, OnCompleteListener<AuthResult> onCompleteListener) {

        phoneAuthService.verifyOtp(verificationId, otp, onCompleteListener);
    }

    @Override
    public String getErrorMessage(Exception e, Context context) {

        return context.getString(R.string.unknown_exception_message);
    }
}
