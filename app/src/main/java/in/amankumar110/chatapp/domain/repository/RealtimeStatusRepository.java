package in.amankumar110.chatapp.domain.repository;

import com.google.android.gms.tasks.OnCompleteListener;

import in.amankumar110.chatapp.models.user.User;
import in.amankumar110.chatapp.models.user.UserStatus;

public interface RealtimeStatusRepository {

    void setStatus(String uId,UserStatus userStatus,RealtimeStatusListener<Void> listener);
    void getStatus(String uId, RealtimeStatusListener<UserStatus> listener);

    interface RealtimeStatusListener<T> {
        void onSuccess(T data);
        void onError(Exception exception);
    }
}
