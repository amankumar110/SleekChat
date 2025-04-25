package in.amankumar110.chatapp.utils;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import dagger.hilt.android.EntryPointAccessors;
import dagger.hilt.android.qualifiers.ApplicationContext;
import in.amankumar110.chatapp.domain.repository.UserRepository;
import in.amankumar110.chatapp.domain.usecases.user.SaveMessagingTokenUseCase;
import in.amankumar110.chatapp.module.MessagingServiceEntryPoint;

public class MessagingTokenHelper {

    private final SaveMessagingTokenUseCase saveMessagingTokenUseCase;

    public MessagingTokenHelper(Context context) {

        saveMessagingTokenUseCase = EntryPointAccessors.fromApplication(
                context,
                MessagingServiceEntryPoint.class
        ).provideSaveMessagingTokenUseCase();

    }

    public void getTokenIfRequired() {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful())
                storeTokenIfRequired(task.getResult());
        });
    }

    public void storeTokenIfRequired(String token) {

        String uid = null;

        if(FirebaseAuth.getInstance().getCurrentUser()==null)
            return;
        else
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        saveMessagingTokenUseCase.execute(uid,token, new UserRepository.UserListener<>() {

            @Override
            public void onSuccess(Void user) {

                Log.v("Messaging","I Got The Work Done");
            }

            @Override
            public void onError(Exception exception) {

                Log.v("Messaging","I Couldn't Do It");
            }
        });
    }
}
