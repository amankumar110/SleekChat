package in.amankumar110.chatapp.data.repository;

import android.content.Context;
import android.util.Log;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Map;

import dagger.hilt.android.qualifiers.ApplicationContext;
import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.data.remote.UserService;
import in.amankumar110.chatapp.domain.repository.UserRepository;
import in.amankumar110.chatapp.exceptions.UserNotExistException;
import in.amankumar110.chatapp.models.user.User;
import jakarta.inject.Inject;

public class UserRepositoryImpl implements UserRepository {

    private final UserService userService;
    private final Context appContext;

    @Inject
    public UserRepositoryImpl(@ApplicationContext Context context, UserService userService) {
        this.userService = userService;
        this.appContext = context;
    }

    @Override
    public void saveUser(User user, UserListener<Void> userListener) {
        userService.saveUser(user, task -> {

            if (task.isSuccessful())
                userListener.onSuccess(null);
            else {

                Log.e("ChatApp12", task.getException().getLocalizedMessage());
                userListener.onError(task.getException());
            }
        });
    }

    @Override
    public void getUserByUId(String UId, UserListener<User> userListener) {
        userService.getUser(UId, task -> {

            if (task.isSuccessful()) {

                DocumentSnapshot document = task.getResult();

                if (document != null && document.exists())
                    userListener.onSuccess(document.toObject(User.class));
                else
                    userListener.onError(new UserNotExistException(appContext.getString(R.string.user_not_exist_message)));
            } else {
                Log.e("ChatApp12", task.getException().getLocalizedMessage());
                userListener.onError(task.getException());
            }
        });
    }

    @Override
    public void updateUser(String UId, Map<String, Object> updates, UserListener<Void> userListener) {

        userService.updateUser(UId, updates, task -> {

            if (task.isSuccessful())
                userListener.onSuccess(null);
            else {
                Log.e("ChatApp12", task.getException().getLocalizedMessage());
                userListener.onError(task.getException());
            }
        });
    }

    @Override
    public void getUserByPhoneNumber(String phoneNumber, UserListener<User> userListener) {

        userService.getUserByPhoneNumber(phoneNumber, task -> {
            if (!task.isSuccessful()) {
                Exception exception = task.getException();
                Log.e(getClass().getSimpleName(), exception != null ? exception.getLocalizedMessage() : "Unknown error");
                userListener.onError(exception);
                return;
            }

            List<DocumentSnapshot> documents = task.getResult().getDocuments();
            if (documents.isEmpty()) {
                userListener.onError(new UserNotExistException("User Doesn't Exist: " + phoneNumber));
                return;
            }

            DocumentSnapshot document = documents.get(0);
            User user = document.toObject(User.class);
            if (user == null) {
                userListener.onError(new NullPointerException("Failed to convert document to User object"));
                return;
            }

            userListener.onSuccess(user);
        });
    }

    @Override
    public void saveMessagingToken(String uId, String token, UserListener<Void> userListener) {

        userService.saveMessagingToken(uId, token, task -> {
            if (task.isSuccessful())
                userListener.onSuccess(null);
            else {
                Log.e("ChatApp12", task.getException().getLocalizedMessage());
                userListener.onError(task.getException());
            }
        });
    }
}

