package in.amankumar110.chatapp.data.remote;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.FirebaseDatabase;

import in.amankumar110.chatapp.models.chat.Message;
import jakarta.inject.Inject;

public class RealtimeMessageUpdateService {

    private final FirebaseDatabase database;
    private final String DATABASE_REFERENCE = "patches";
    private final String UPDATED_MESSAGES_REFERENCE = "updated_messages";
    private final String DELETED_MESSAGES_REFERENCE  = "deleted_messages";

    @Inject
    public RealtimeMessageUpdateService() {
        this.database  = FirebaseDatabase.getInstance();
    }

    public void addToUpdatedMessages(Message message, String sessionId, OnCompleteListener<Void> onCompleteListener) {
        database.getReference(DATABASE_REFERENCE)
                .child(UPDATED_MESSAGES_REFERENCE)
                .child(sessionId)
                .child(message.getId())
                .setValue(message)
                .addOnCompleteListener(onCompleteListener);
    }

    public void addToDeletedMessages(Message message, String sessionId, OnCompleteListener<Void> onCompleteListener) {
        database.getReference(DATABASE_REFERENCE)
                .child(DELETED_MESSAGES_REFERENCE)
                .child(sessionId)
                .child(message.getId())
                .setValue(message)
                .addOnCompleteListener(onCompleteListener);
    }

    public void onNewDeletedMessage(String sessionId, ChildEventListener childEventListener) {

        database.getReference(DATABASE_REFERENCE)
                .child(DELETED_MESSAGES_REFERENCE)
                .child(sessionId)
                .addChildEventListener(childEventListener);
    }

    public void onNewUpdatedMessage(String sessionId, ChildEventListener childEventListener) {

        database.getReference(DATABASE_REFERENCE)
                .child(UPDATED_MESSAGES_REFERENCE)
                .child(sessionId)
                .addChildEventListener(childEventListener);
    }

    public void removeDeletedMessages(String sessionId,OnCompleteListener<Void> onCompleteListener) {

        database.getReference(DATABASE_REFERENCE)
                .child(DELETED_MESSAGES_REFERENCE)
                .child(sessionId)
                .removeValue()
                .addOnCompleteListener(onCompleteListener);
    }

    public void removeUpdatedMessages(String sessionId,OnCompleteListener<Void> onCompleteListener) {

        database.getReference(DATABASE_REFERENCE)
                .child(UPDATED_MESSAGES_REFERENCE)
                .child(sessionId)
                .removeValue()
                .addOnCompleteListener(onCompleteListener);
    }

}
