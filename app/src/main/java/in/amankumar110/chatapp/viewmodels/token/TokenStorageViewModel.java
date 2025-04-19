package in.amankumar110.chatapp.viewmodels.token;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.amankumar110.chatapp.domain.common.Result;
import in.amankumar110.chatapp.domain.usecases.token.TokenUseCase;

@HiltViewModel
public class TokenStorageViewModel extends ViewModel {

    private final TokenUseCase tokenUseCase;

    private final MutableLiveData<String> _token = new MutableLiveData<>();
    public LiveData<String> token = _token;

    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>();
    public LiveData<String> errorMessage = _errorMessage;

    @Inject
    public TokenStorageViewModel(TokenUseCase tokenUseCase) {
        this.tokenUseCase = tokenUseCase;
    }

    public void storeToken(String token) {
        Result<Void> result = tokenUseCase.storeToken(token);
        if (result instanceof Result.Error) {
            _errorMessage.postValue(((Result.Error<Void>) result).getError().getMessage());
        }
    }

    public void getToken() {
        Result<String> token = tokenUseCase.getToken();

        if (token instanceof Result.Success) {
            _token.postValue(((Result.Success<String>) token).getData());
        } else if (token instanceof Result.Error) {
            _errorMessage.postValue(((Result.Error<String>) token).getError().getMessage());
        }
    }

}
