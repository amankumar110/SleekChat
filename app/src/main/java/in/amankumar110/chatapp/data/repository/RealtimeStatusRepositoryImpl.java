package in.amankumar110.chatapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

import in.amankumar110.chatapp.data.remote.RealtimeStatusService;
import in.amankumar110.chatapp.domain.repository.RealtimeStatusRepository;
import in.amankumar110.chatapp.models.user.User;
import in.amankumar110.chatapp.models.user.UserStatus;
import jakarta.inject.Inject;

public class RealtimeStatusRepositoryImpl implements RealtimeStatusRepository {

    private final RealtimeStatusService realtimeStatusService;

    @Inject
    public RealtimeStatusRepositoryImpl(RealtimeStatusService realtimeStatusService) {
        this.realtimeStatusService = realtimeStatusService;
    }

    @Override
    public void setStatus(String uid, UserStatus userStatus, RealtimeStatusListener<Void> listener) {

        realtimeStatusService.setUserStatus(uid, userStatus, task -> {

            if(task.isSuccessful())
                listener.onSuccess(null);
            else
                listener.onError(task.getException());

        });
    }

    @Override
    public void getStatus(String uid, RealtimeStatusListener<UserStatus> listener) {

        realtimeStatusService.getUserStatus(uid, new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserStatus userStatus = snapshot.getValue(UserStatus.class);
                listener.onSuccess(userStatus);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(error.toException());
            }
        });

    }
}
