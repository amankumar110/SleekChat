package in.amankumar110.chatapp.data.remote;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.amankumar110.chatapp.models.chat.Message;

public class RealtimeMessageService {

    private final FirebaseDatabase realtimeDatabase;
    private final String COLLECTION_NAME = "realtime_messages";
    private final String SESSION_COLLECTION_ID = "messages";

    @Inject
    public RealtimeMessageService() {
        realtimeDatabase = FirebaseDatabase.getInstance();
    }

    public void sendMessage(Message message, String sessionId, OnCompleteListener<Void> onCompleteListener) {

        realtimeDatabase
                .getReference(COLLECTION_NAME)
                .child(sessionId)
                .child(SESSION_COLLECTION_ID)
                .child(message.getId())
                .setValue(message)
                .addOnCompleteListener(onCompleteListener);
    }

    public void getMessages(String sessionId, ChildEventListener childEventListener) {
        realtimeDatabase
                .getReference(COLLECTION_NAME)
                .child(sessionId)
                .child(SESSION_COLLECTION_ID)
                .addChildEventListener(childEventListener);
    }


    public void deleteAllMessages(String sessionId, OnCompleteListener<Void> onCompleteListener) {
        realtimeDatabase
                .getReference(COLLECTION_NAME)
                .child(sessionId)
                .child(SESSION_COLLECTION_ID)
                .removeValue()
                .addOnCompleteListener(onCompleteListener);
    }

}

