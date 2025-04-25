package in.amankumar110.chatapp.data.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.amankumar110.chatapp.data.remote.RealtimeMessageService;
import in.amankumar110.chatapp.domain.repository.RealtimeMessageRepository;
import in.amankumar110.chatapp.models.chat.Message;

public class RealtimeMessageRepositoryImpl implements RealtimeMessageRepository {

    private final RealtimeMessageService realtimeMessageService;

    @Inject
    public RealtimeMessageRepositoryImpl(RealtimeMessageService realtimeMessageService) {

        this.realtimeMessageService = realtimeMessageService;
    }

    @Override
    public void addMessage(Message message, String sessionId, RealtimeMessageListener<Void> listener) {

        message.setId(generateUniqueId(message.getSenderUId(),message.getReceiverUId(),message.getSentAt()));

        realtimeMessageService.sendMessage(message, sessionId, task -> {

            Log.v("MessageR",task.isSuccessful()+"");

            if(task.isSuccessful())
                listener.onSuccess(null);
            else
                listener.onError(task.getException());
        });
    }

    @Override
    public void onNewMessage(String sessionId, RealtimeMessageListener<Message>listener) {

        realtimeMessageService.getMessages(sessionId, new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Log.v("MessageR","Child added Successfully");

                if (!snapshot.exists()) {
                    listener.onError(new Exception("Snapshot does not exist"));
                return;
                }

                Message message = snapshot.getValue(Message.class);

                listener.onSuccess(message);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.v("MessageR","Child Changed Successfully");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                Log.v("MessageR","Child Removed Successfully");
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.v("MessageR","Child moved Successfully");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.v("MessageR","Child operation cancelled Successfully");
                Log.e("Realtime", "Listener canceled: " + error.getMessage());
                listener.onError(error.toException());
            }
        });

    }

    @Override
    public void deleteAllMessages(String sessionId, RealtimeMessageListener<Void> listener) {

        realtimeMessageService.deleteAllMessages(sessionId, task -> {

            if(task.isSuccessful())
                listener.onSuccess(null);
            else
                listener.onError(task.getException());
        });
    }

    @Override
    public String generateUniqueId(String sender, String receiver, Long sentAt) {
        // Sort sender and receiver lexicographically to ensure consistent order
        String first = sender.compareTo(receiver) <= 0 ? sender : receiver;
        String second = sender.compareTo(receiver) <= 0 ? receiver : sender;

        return first + "_" + second + "_" + sentAt;
    }


}
