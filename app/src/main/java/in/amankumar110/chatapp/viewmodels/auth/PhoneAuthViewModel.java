package in.amankumar110.chatapp.viewmodels.auth;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.domain.usecases.auth.SendOtpUseCase;
import in.amankumar110.chatapp.domain.usecases.auth.VerifyOtpUseCase;
import in.amankumar110.chatapp.exceptions.InvalidPhoneNumberException;

@HiltViewModel
public class PhoneAuthViewModel extends ViewModel {

    private final SendOtpUseCase sendOtpUseCase;
    private final VerifyOtpUseCase verifyOtpUseCase;
    private final Context appContext;
    private String verificationId = "";

    private final MutableLiveData<Boolean> _isVerified = new MutableLiveData<>(false);
    public LiveData<Boolean> isVerified = _isVerified;

    // error message is used to show the error message to the user
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>("");
    public LiveData<String> errorMessage = _errorMessage;

    // is otp sent is used for tracking the status of otp sent to show/hide the visibility of otp view
    private final MutableLiveData<Boolean> _isOtpSent = new MutableLiveData<>(false);
    public LiveData<Boolean> isOtpSent = _isOtpSent;

    @Inject
    public PhoneAuthViewModel(@ApplicationContext Context context, SendOtpUseCase sendOtpUseCase, VerifyOtpUseCase verifyOtpUseCase) {
        this.sendOtpUseCase = sendOtpUseCase;
        this.appContext = context;
        this.verifyOtpUseCase = verifyOtpUseCase;
    }

    public void sendOtp( String phoneNumber) {

        try {
            sendOtpUseCase.sendOtp(phoneNumber, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    _isVerified.postValue(true);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    _isVerified.postValue(false);
                    Log.e("PAE","Occured Due to Wrong Num");
                    _errorMessage.postValue(e.getMessage());
                }

                @Override
                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    _isOtpSent.postValue(true);
                    PhoneAuthViewModel.this.verificationId = verificationId;
                }
            });

        } catch (InvalidPhoneNumberException e) {
                _errorMessage.postValue(appContext.getString(R.string.invalid_contact_number_message));
        } catch (RuntimeException e) {
                _errorMessage.postValue(appContext.getString(R.string.unknown_exception_message));
        }
    }

    public void verifyOtp(String otp) {

        verifyOtpUseCase.verifyOtp(verificationId, otp, task -> {

            if(task.isSuccessful())
                _isVerified.postValue(true);
            else {
                _isVerified.postValue(false);
                _errorMessage.postValue(Objects.requireNonNull(task.getException()).getMessage());
            }
        });
    }

}
