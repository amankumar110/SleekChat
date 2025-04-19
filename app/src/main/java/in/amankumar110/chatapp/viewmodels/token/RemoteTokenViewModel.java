package in.amankumar110.chatapp.viewmodels.token;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.amankumar110.chatapp.domain.repository.RemoteTokenRepository;
import in.amankumar110.chatapp.domain.usecases.token.RemoteTokenUseCase;
import in.amankumar110.chatapp.exceptions.UserNotLoggedInException;

@HiltViewModel
public class RemoteTokenViewModel extends ViewModel {

    private final RemoteTokenUseCase remoteTokenUseCase;

    private final MutableLiveData<String> _token = new MutableLiveData<>();
    public LiveData<String> token = _token;
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;
    private final MutableLiveData<Boolean> _shouldSignIn = new MutableLiveData<>(false);
    public LiveData<Boolean> shouldSignIn = _shouldSignIn;

    @Inject
    public RemoteTokenViewModel(RemoteTokenUseCase remoteTokenUseCase) {
        this.remoteTokenUseCase = remoteTokenUseCase;
    }


    public void getRemoteToken() {
        remoteTokenUseCase.getRemoteToken(new RemoteTokenRepository.TokenCallback() {
            @Override
            public void onSuccess(String token) {
                _token.postValue(token);
            }

            @Override
            public void onError(Exception exception) {

                if(exception instanceof UserNotLoggedInException)
                    _shouldSignIn.postValue(true);
                else
                    _errorMessage.postValue(exception.getMessage());
            }
        });
    }
}
