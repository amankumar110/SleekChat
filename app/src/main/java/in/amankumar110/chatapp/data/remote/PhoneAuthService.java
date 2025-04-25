package in.amankumar110.chatapp.data.remote;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import in.amankumar110.chatapp.MainActivity;

public class PhoneAuthService {

    private final FirebaseAuth firebaseAuth;
    private final long TIME_OUT_SECONDS = 60L;

    @Inject
    public PhoneAuthService() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
    }

    public void sendVerificationCode(String phoneNumber, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks) {

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(TIME_OUT_SECONDS, TimeUnit.SECONDS)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void verifyOtp(String verificationId, String otp, OnCompleteListener<AuthResult> callback) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(callback);
    }


}
