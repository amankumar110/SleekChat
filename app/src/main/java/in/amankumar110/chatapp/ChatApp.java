package in.amankumar110.chatapp;

import android.app.Application;

import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.HiltAndroidApp;
import jakarta.inject.Inject;

@HiltAndroidApp
public class ChatApp extends Application {
    @Inject
    UserOnlineStatusObserver observer;

    @Override
    public void onCreate() {
        super.onCreate();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(observer);
    }
}
