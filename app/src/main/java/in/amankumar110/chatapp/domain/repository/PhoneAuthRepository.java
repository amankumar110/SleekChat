package in.amankumar110.chatapp.domain.repository;

import android.content.Context;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthProvider;

public interface PhoneAuthRepository {

    void sendOtp(String phoneNumber, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks);
    void verifyOtp(String verificationId, String otp, OnCompleteListener<AuthResult> onCompleteListener);
    String getErrorMessage(Exception e, Context context);
    static boolean isPhoneNumberValid(String phoneNumber) {
        return phoneNumber.matches("^\\+?[1-9]\\d{9,14}$");
    }

}
