package in.amankumar110.chatapp;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import in.amankumar110.chatapp.domain.repository.RealtimeStatusRepository;
import in.amankumar110.chatapp.domain.usecases.user.SetStatusUseCase;
import in.amankumar110.chatapp.models.user.UserStatus;

public class UserOnlineStatusObserver implements LifecycleObserver {

    private final SetStatusUseCase setStatusUseCase;

    @Inject
    public UserOnlineStatusObserver(SetStatusUseCase setStatusUseCase) {
        this.setStatusUseCase = setStatusUseCase;
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
        Log.d("AppLifecycle", "App entered foreground");

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            UserStatus userStatus = new UserStatus(UserStatus.Status.ONLINE, System.currentTimeMillis());

            setStatusUseCase.execute(uid, userStatus, new RealtimeStatusRepository.RealtimeStatusListener<Void>() {
                @Override
                public void onSuccess(Void data) { }

                @Override
                public void onError(Exception exception) { }
            });
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        Log.d("AppLifecycle", "App entered background");

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            UserStatus userStatus = new UserStatus(UserStatus.Status.OFFLINE, System.currentTimeMillis());

            setStatusUseCase.execute(uid, userStatus, new RealtimeStatusRepository.RealtimeStatusListener<Void>() {
                @Override
                public void onSuccess(Void data) { }

                @Override
                public void onError(Exception exception) { }
            });
        }
    }
}
