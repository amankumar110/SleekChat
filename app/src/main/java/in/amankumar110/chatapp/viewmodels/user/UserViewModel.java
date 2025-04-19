package in.amankumar110.chatapp.viewmodels.user;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.amankumar110.chatapp.domain.usecases.user.GetUserByPhoneUseCase;
import in.amankumar110.chatapp.domain.usecases.user.SaveUserUseCase;
import in.amankumar110.chatapp.models.user.User;
import in.amankumar110.chatapp.domain.repository.UserRepository;

@HiltViewModel
public class UserViewModel extends ViewModel {

    private final MutableLiveData<Boolean> _userSavedToDatabase = new MutableLiveData<>(null);
    public LiveData<Boolean> userSavedToDatabase = _userSavedToDatabase;
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>(null);
    public LiveData<String> errorMessage = _errorMessage;

    private final MutableLiveData<User> _userByPhoneNumber = new MutableLiveData<>(null);
    public LiveData<User> userByPhoneNumber = _userByPhoneNumber;

    // Use Cases
    private final SaveUserUseCase saveUserUseCase;
    private final GetUserByPhoneUseCase getUserByPhoneUseCase;

    @Inject
    public UserViewModel(SaveUserUseCase saveUserUseCase,GetUserByPhoneUseCase getUserByPhoneUseCase) {
        this.saveUserUseCase = saveUserUseCase;
        this.getUserByPhoneUseCase = getUserByPhoneUseCase;
    }

    public void saveUser(User user) {
        saveUserUseCase.execute(user, new UserRepository.UserListener<>() {
            @Override
            public void onSuccess(Void  nullValue) {
                _userSavedToDatabase.postValue(true);
            }

            @Override
            public void onError(Exception exception) {
                _userSavedToDatabase.postValue(false);
                _errorMessage.postValue(exception.getMessage());
            }
        });
    }

    public void getUserByPhoneNumber(String phoneNumber) {

        getUserByPhoneUseCase.execute(phoneNumber, new UserRepository.UserListener<>() {
            @Override
            public void onSuccess(User user) {
                _userByPhoneNumber.postValue(user);
            }

            @Override
            public void onError(Exception exception) {
                _userByPhoneNumber.postValue(new User());
                _errorMessage.postValue(exception.getMessage());
            }
        });
    }

}
