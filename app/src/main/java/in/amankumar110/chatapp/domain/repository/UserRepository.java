package in.amankumar110.chatapp.domain.repository;

import java.util.Map;

import in.amankumar110.chatapp.models.user.User;

public interface UserRepository {

    void saveUser(User user, UserListener<Void> userListener);
    void getUserByUId(String UId, UserListener<User> userListener);
    void updateUser(String uId, Map<String,Object> updates, UserListener<Void> userListener);
    void getUserByPhoneNumber(String phoneNumber, UserListener<User> userListener);
    void saveMessagingToken(String uId, String token, UserListener<Void> userListener);

    interface UserListener<T> {
        void onSuccess(T user);
        void onError(Exception exception);
    }
}
