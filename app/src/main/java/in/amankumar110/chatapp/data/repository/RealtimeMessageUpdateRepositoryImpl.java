package in.amankumar110.chatapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import in.amankumar110.chatapp.data.remote.RealtimeMessageUpdateService;
import in.amankumar110.chatapp.domain.repository.RealtimeMessageUpdateRepository;
import in.amankumar110.chatapp.models.chat.Message;
import jakarta.inject.Inject;

public class RealtimeMessageUpdateRepositoryImpl implements RealtimeMessageUpdateRepository {

    private RealtimeMessageUpdateService realtimeMessageUpdateService;

    @Inject
    public RealtimeMessageUpdateRepositoryImpl(RealtimeMessageUpdateService realtimeMessageUpdateService) {
        this.realtimeMessageUpdateService = realtimeMessageUpdateService;
    }

    @Override
    public void addToUpdatedMessages(Message message, String sessionId, RealtimeMessageUpdateListener<Void> listener) {

        realtimeMessageUpdateService.addToUpdatedMessages(message, sessionId, task -> {
            if (task.isSuccessful()) {

                Log.v("MessageFromRepo","It was updated i know");
                listener.onSuccess(task.getResult());
            } else {
                listener.onError(task.getException());
            }
        });
    }

    @Override
    public void addToDeletedMessages(Message message, String sessionId, RealtimeMessageUpdateListener<Void> listener) {

        realtimeMessageUpdateService.addToDeletedMessages(message, sessionId, task -> {
            if (task.isSuccessful()) {
                listener.onSuccess(task.getResult());
            } else {
                listener.onError(task.getException());
            }
        });

    }

    @Override
    public void onNewDeletedMessage(String sessionId, RealtimeMessageUpdateListener<Message> listener) {

        realtimeMessageUpdateService.onNewDeletedMessage(sessionId, new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Message message = snapshot.getValue(Message.class);
                listener.onSuccess(message);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(error.toException());
            }
        });
    }

    @Override
    public void onNewUpdatedMessage(String sessionId, RealtimeMessageUpdateListener<Message> listener) {

        realtimeMessageUpdateService.onNewUpdatedMessage(sessionId, new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Message message = snapshot.getValue(Message.class);
                listener.onSuccess(message);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Message message = snapshot.getValue(Message.class);
                listener.onSuccess(message);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onError(error.toException());
            }
        });
    }

    @Override
    public void removeUpdatedMessages(String sessionId, RealtimeMessageUpdateListener<Void> listener) {

        realtimeMessageUpdateService.removeUpdatedMessages(sessionId, task -> {

            if(task.isSuccessful())
                listener.onSuccess(null);
            else
                listener.onError(task.getException());
        });
    }

    @Override
    public void removeDeletedMessages(String sessionId, RealtimeMessageUpdateListener<Void> listener) {

        realtimeMessageUpdateService.removeDeletedMessages(sessionId, task -> {

            if(task.isSuccessful())
                listener.onSuccess(null);
            else
                listener.onError(task.getException());
        });
    }
}
