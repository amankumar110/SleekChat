package in.amankumar110.chatapp.data.remote;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.C;

import in.amankumar110.chatapp.models.user.User;
import in.amankumar110.chatapp.models.user.UserStatus;
import jakarta.inject.Inject;

public class RealtimeStatusService {
    private final FirebaseDatabase realtimeDatabase;
    private final String COLLECTION_NAME = "online_users";

    @Inject
    public RealtimeStatusService() {
        realtimeDatabase = FirebaseDatabase.getInstance();
    }

    public void setUserStatus(String uid, UserStatus userStatus, OnCompleteListener<Void> onCompleteListener) {

        DatabaseReference ref = realtimeDatabase
                .getReference(COLLECTION_NAME)
                .child(uid);

        // Set user's current status (e.g. ONLINE)
        ref.setValue(userStatus)
                .addOnCompleteListener(onCompleteListener);

        // This value will be automatically written when the app disconnects (force close, crash, swipe away, etc.)
        UserStatus offlineStatus = new UserStatus(UserStatus.Status.OFFLINE, System.currentTimeMillis());
        ref.onDisconnect().setValue(offlineStatus);
    }


    public void getUserStatus(String uid,ValueEventListener valueEventListener) {

        realtimeDatabase
                .getReference(COLLECTION_NAME)
                .child(uid)
                .addValueEventListener(valueEventListener);
    }

}
