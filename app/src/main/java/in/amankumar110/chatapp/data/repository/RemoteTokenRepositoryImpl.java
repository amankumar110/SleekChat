package in.amankumar110.chatapp.data.repository;

import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import dagger.hilt.android.qualifiers.ApplicationContext;
import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.domain.repository.RemoteTokenRepository;
import in.amankumar110.chatapp.exceptions.UserNotLoggedInException;

public class RemoteTokenRepositoryImpl implements RemoteTokenRepository {

    private final FirebaseUser firebaseUser;
    private final Context appContext;

    public RemoteTokenRepositoryImpl(@ApplicationContext Context appContext) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        this.appContext = appContext;
    }
    @Override
    public void getRemoteToken(TokenCallback tokenCallback) {

        if(firebaseUser!=null)
            firebaseUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                        tokenCallback.onSuccess(task.getResult().getToken());
                    else
                        tokenCallback.onError(task.getException());
                });
        else
            tokenCallback.onError(new UserNotLoggedInException(
                    appContext.getString(R.string.user_not_logged_in_error_message)));
    }



}
