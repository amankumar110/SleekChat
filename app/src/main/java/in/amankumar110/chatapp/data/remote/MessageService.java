package in.amankumar110.chatapp.data.remote;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import in.amankumar110.chatapp.models.chat.ChatSession;
import in.amankumar110.chatapp.models.chat.Message;

public class MessageService {

    private final FirebaseFirestore firestore;
    private final String COLLECTION_ID = "messages";
    private final String MESSAGES_COLLECTION_ID = "messages";

    @Inject
    public MessageService() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public void addMessage(Message message, String sessionId, OnCompleteListener<Void> onCompleteListener) {

        firestore.collection(COLLECTION_ID)
                .document(sessionId)
                .collection(MESSAGES_COLLECTION_ID)
                .document(message.getId())
                .set(message)
                .addOnCompleteListener(onCompleteListener);
    }

    // Bulk adding messages using WriteBatch for atomic operations
    public void addMessages(List<Message> messages, String sessionId, OnCompleteListener<Void> onCompleteListener) {
        WriteBatch batch = firestore.batch();  // Create a batch operation

        // Loop through all messages and prepare them for batch operation
        for (Message message : messages) {
            // Create a Map to hold the message data, map the fields as required
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("id",message.getId());
            messageData.put("senderUId", message.getSenderUId());
            messageData.put("receiverUId", message.getReceiverUId());
            messageData.put("message", message.getMessage()); // Assuming Message has a getMessageContent() method
            messageData.put("sentAt", message.getSentAt());  // You can add a timestamp field
            messageData.put("isSeen", message.getIsSeen());

            // If the Message class has other fields, include them as needed
            // messageData.put("sender", message.getSender());

            // Create a new document reference in the "messages" collection for each message
            DocumentReference newMessageRef = firestore.collection(COLLECTION_ID)
                    .document(sessionId)  // Use sessionId for the document reference
                    .collection(MESSAGES_COLLECTION_ID)
                    .document(message.getId());  // Firestore auto-generates the document ID

            // Add the set operation to the batch
            batch.set(newMessageRef, messageData);
        }

        // Commit the batch
        batch.commit()
                .addOnCompleteListener(onCompleteListener)
                .addOnSuccessListener(aVoid -> {
                    // Handle success
                    System.out.println("Bulk messages added successfully!");
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    e.printStackTrace();
                });
    }

    public void getAllMessages(String sessionId,OnCompleteListener<QuerySnapshot> onCompleteListener) {
        firestore.collection(COLLECTION_ID)
                .document(sessionId)
                .collection(MESSAGES_COLLECTION_ID)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public void setMessageAsSeen(List<Message> messages, ChatSession chatSession, OnCompleteListener<Void> onCompleteListener) {

        WriteBatch batch = firestore.batch();

        for (Message message : messages) {
            DocumentReference docRef = firestore.collection(COLLECTION_ID)
                    .document(chatSession.getSessionId())
                    .collection(MESSAGES_COLLECTION_ID)
                    .document(message.getId()); // Assuming Message has getMessageId()

            batch.update(docRef, "isSeen", true);
        }

        batch.commit().addOnCompleteListener(onCompleteListener);
    }

    public void updateMessage(Message message, String sessionId, OnCompleteListener<Void> onCompleteListener) {

        firestore.collection(COLLECTION_ID)
                .document(sessionId)
                .collection(MESSAGES_COLLECTION_ID)
                .document(message.getId())
                .update("message",message.getMessage())
                .addOnCompleteListener(onCompleteListener);
    }

    public void deleteMessages(List<Message> messages, String sessionId, OnCompleteListener<Void> onCompleteListener) {

        WriteBatch batch = firestore.batch();

        for (Message message : messages) {
            DocumentReference docRef = firestore.collection(COLLECTION_ID)
                    .document(sessionId)
                    .collection(MESSAGES_COLLECTION_ID)
                    .document(message.getId());

            batch.delete(docRef);
        }

        batch.commit().addOnCompleteListener(onCompleteListener);
    }

    public void updateMessages(List<Message> messages, String sessionId, OnCompleteListener<Void> onCompleteListener) {

        WriteBatch batch = firestore.batch();

        for (Message message : messages) {
            DocumentReference docRef = firestore.collection(COLLECTION_ID)
                    .document(sessionId)
                    .collection(MESSAGES_COLLECTION_ID)
                    .document(message.getId());

            batch.update(docRef,"message",message.getMessage());
        }

        batch.commit().addOnCompleteListener(onCompleteListener);
    }
}
