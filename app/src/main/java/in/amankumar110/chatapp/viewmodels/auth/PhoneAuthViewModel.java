package in.amankumar110.chatapp.viewmodels.auth;

import static java.lang.Boolean.FALSE;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
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
import in.amankumar110.chatapp.utils.CountryCodeUtil;

@HiltViewModel
public class PhoneAuthViewModel extends ViewModel {

    private final SendOtpUseCase sendOtpUseCase;
    private final VerifyOtpUseCase verifyOtpUseCase;
    private final Context appContext;
    private String verificationId = "";

    private final MutableLiveData<Boolean> _isVerified = new MutableLiveData<>(null);
    public LiveData<Boolean> isVerified = _isVerified;

    // error message is used to show the error message to the user
    private final MutableLiveData<Exception> _error= new MutableLiveData<>(null);
    public LiveData<Exception> error = _error;

    private MutableLiveData<Boolean> _isPhoneValid = new MutableLiveData<>(null);
    public LiveData<Boolean> isPhoneValid = _isPhoneValid;

    // is otp sent is used for tracking the status of otp sent to show/hide the visibility of otp view
    private final MutableLiveData<Boolean> _isOtpSent = new MutableLiveData<>(null);
    public LiveData<Boolean> isOtpSent = _isOtpSent;
    MutableLiveData<Boolean> _phoneNumberExists = new MutableLiveData<>(null);
    public LiveData<Boolean> phoneNumberExists = _phoneNumberExists;
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    @Inject
    public PhoneAuthViewModel(@ApplicationContext Context context, SendOtpUseCase sendOtpUseCase, VerifyOtpUseCase verifyOtpUseCase) {
        this.sendOtpUseCase = sendOtpUseCase;
        this.appContext = context;
        this.verifyOtpUseCase = verifyOtpUseCase;
    }

    public void sendOtp(String phoneCode,String number) {

        String phoneNumber = phoneCode + number;

        if(phoneNumber.isEmpty() || !CountryCodeUtil.isPhoneNumberValid(phoneNumber)) {
            _error.postValue(new InvalidPhoneNumberException(appContext.getString(R.string.invalid_contact_number_message)));
            _isVerified.postValue(false);
            _isPhoneValid.postValue(false);
            return;
        }

        try {
            sendOtpUseCase.execute(phoneNumber, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    _isVerified.postValue(true);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {

                    if(e instanceof FirebaseAuthUserCollisionException) {
                        _phoneNumberExists.postValue(true);
                    }
                    _error.postValue(e);
                    _isVerified.postValue(false);
                }

                @Override
                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    _isOtpSent.postValue(true);
                    PhoneAuthViewModel.this.verificationId = verificationId;
                }
            });

        } catch (InvalidPhoneNumberException e) {
                _error.postValue(new InvalidPhoneNumberException(appContext.getString(R.string.invalid_contact_number_message)));
        } catch (RuntimeException e) {
                _error.postValue(new RuntimeException(appContext.getString(R.string.unknown_exception_message)));
        }
    }

    public void verifyOtp(String otp) {

        _isLoading.postValue(true);

        verifyOtpUseCase.verifyOtp(verificationId, otp, task -> {

            _isLoading.postValue(false);

            if(task.isSuccessful())
                _isVerified.postValue(true);
            else {
                _isVerified.postValue(false);
                _error.postValue(task.getException());
            }
        });
    }

    public boolean isIdle() {
        return Objects.equals(isLoading.getValue(), FALSE);
    }

    public boolean isOtpSent() {
        return isOtpSent.getValue() != null && isOtpSent.getValue();
    }

    public boolean doesUserExist() {
        return phoneNumberExists.getValue() != null && phoneNumberExists.getValue();
    }

    public boolean isPhoneValid() {
        return isPhoneValid.getValue() != null && isPhoneValid.getValue();
    }

    public void clearError() {
        this._error.postValue(null);
    }

    public void reset() {
        _isPhoneValid.postValue(null);
        _isVerified.postValue(null);
        _isOtpSent.postValue(null);
        _phoneNumberExists.postValue(null);
    }
}
