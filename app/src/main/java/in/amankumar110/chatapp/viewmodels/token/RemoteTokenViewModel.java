package in.amankumar110.chatapp.viewmodels.token;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.amankumar110.chatapp.domain.repository.RemoteTokenRepository;
import in.amankumar110.chatapp.domain.usecases.token.RemoteTokenUseCase;
import in.amankumar110.chatapp.exceptions.UserNotLoggedInException;

@HiltViewModel
public class RemoteTokenViewModel extends ViewModel {

    private final RemoteTokenUseCase remoteTokenUseCase;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;
    private final MutableLiveData<String> _token = new MutableLiveData<>(null);
    public LiveData<String> token = _token;
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>(null);
    public LiveData<String> errorMessage = _errorMessage;
    private final MutableLiveData<Boolean> _shouldSignIn = new MutableLiveData<>(null);
    public LiveData<Boolean> shouldSignIn = _shouldSignIn;

    @Inject
    public RemoteTokenViewModel(RemoteTokenUseCase remoteTokenUseCase) {
        this.remoteTokenUseCase = remoteTokenUseCase;
    }


    public void getRemoteToken() {

        _isLoading.postValue(true);

        remoteTokenUseCase.getRemoteToken(new RemoteTokenRepository.TokenCallback() {
            @Override
            public void onSuccess(String token) {
                _token.postValue(token);
                _isLoading.postValue(false);
            }

            @Override
            public void onError(Exception exception) {

                if(exception instanceof UserNotLoggedInException)
                    _shouldSignIn.postValue(true);
                else
                    _errorMessage.postValue(exception.getMessage());

                _isLoading.postValue(false);
            }
        });
    }

    public String getErrorMessage() {

        return _errorMessage.getValue() == null ? null : errorMessage.getValue();
    }

    public boolean isIdle() {
        return Objects.equals(_isLoading.getValue(), false);
    }
}
