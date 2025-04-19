package in.amankumar110.chatapp.viewmodels.user;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import dagger.hilt.android.lifecycle.HiltViewModel;
import in.amankumar110.chatapp.domain.repository.RealtimeStatusRepository;
import in.amankumar110.chatapp.domain.usecases.user.GetUserStatusUseCase;
import in.amankumar110.chatapp.domain.usecases.user.SetStatusUseCase;
import in.amankumar110.chatapp.models.user.UserStatus;
import jakarta.inject.Inject;

@HiltViewModel
public class RealtimeStatusViewModel extends ViewModel {

    private final GetUserStatusUseCase getUserStatusUseCase;
    private final SetStatusUseCase setStatusUseCase;
    private final MutableLiveData<UserStatus> _userStatus = new MutableLiveData<>(null);
    public MutableLiveData<UserStatus> userStatus = _userStatus;
    private final MutableLiveData<String> _errorMessage = new MutableLiveData<>(null);
    public MutableLiveData<String> errorMessage = _errorMessage;
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isLoading = _isLoading;
    public MutableLiveData<Boolean> _isStatusSet = new MutableLiveData<>(null);
    public LiveData<Boolean> isStatusSet = _isStatusSet;

    @Inject
    public RealtimeStatusViewModel(GetUserStatusUseCase getUserStatusUseCase, SetStatusUseCase setStatusUseCase) {
        this.getUserStatusUseCase = getUserStatusUseCase;
        this.setStatusUseCase = setStatusUseCase;
    }


    public void getUserStatus(String uId) {

        _isLoading.postValue(true);
        getUserStatusUseCase.execute(uId, new RealtimeStatusRepository.RealtimeStatusListener<>() {
            @Override
            public void onSuccess(UserStatus data) {
                Log.v("VM","successInGetUser");
                Log.v("VM",data.toString());
                _userStatus.postValue(data);
                _isLoading.postValue(false);
            }

            @Override
            public void onError(Exception exception) {

                Log.v("VM","errorCALLED");
                _userStatus.postValue(null);
                _errorMessage.postValue(exception.getMessage());
                _isLoading.postValue(false);
            }
        });
    }

    public void setUserStatus(String uid,UserStatus userStatus) {

        _isLoading.postValue(true);

        setStatusUseCase.execute(uid, userStatus, new RealtimeStatusRepository.RealtimeStatusListener<Void>() {
            @Override
            public void onSuccess(Void data) {
                _isStatusSet.postValue(true);
                _isLoading.postValue(false);
            }

            @Override
            public void onError(Exception exception) {

                _isLoading.postValue(false);
                _isStatusSet.postValue(false);
                _errorMessage.postValue(exception.getMessage());
            }
        });
    }




}
