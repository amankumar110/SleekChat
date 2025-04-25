package in.amankumar110.chatapp.data.remote;

import static in.amankumar110.chatapp.BR.chatSession;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import javax.inject.Inject;

import in.amankumar110.chatapp.domain.common.Result;
import in.amankumar110.chatapp.models.chat.ChatSession;

public class ChatSessionService {

    private final FirebaseFirestore database;
    private static final String COLLECTION_ID = "chat_sessions";
    private static final String SESSIONS_COLLECTION_ID = "sessions";

    @Inject
    public ChatSessionService() {
        this.database = FirebaseFirestore.getInstance();
    }

    public void createChatSession(ChatSession senderSession, ChatSession receiverSession, OnCompleteListener<Void> onCompleteListener) {
        String sessionId = senderSession.getSessionId();

        // Path: users/{senderUid}/chat_sessions/{sessionId}
        DocumentReference senderRef = database.collection(COLLECTION_ID)
                .document(senderSession.getSenderUid())
                .collection(SESSIONS_COLLECTION_ID)
                .document(sessionId);

        // Path: users/{receiverUid}/chat_sessions/{sessionId}
        DocumentReference receiverRef = database.collection(COLLECTION_ID)
                .document(receiverSession.getSenderUid())
                .collection(SESSIONS_COLLECTION_ID)
                .document(sessionId);

        WriteBatch batch = database.batch();
        batch.set(senderRef, senderSession);
        batch.set(receiverRef, receiverSession);

        batch.commit().addOnCompleteListener(onCompleteListener);
    }

    public void updateLastMessage(ChatSession chatSession, String lastMessage, OnCompleteListener<Void> onCompleteListener) {
        DocumentReference senderRef = database.collection(COLLECTION_ID)
                .document(chatSession.getSenderUid())
                .collection(SESSIONS_COLLECTION_ID)
                .document(chatSession.getSessionId());

        // Path: users/{receiverUid}/chat_sessions/{sessionId}
        DocumentReference receiverRef = database.collection(COLLECTION_ID)
                .document(chatSession.getReceiverUid())
                .collection(SESSIONS_COLLECTION_ID)
                .document(chatSession.getSessionId());

        WriteBatch batch = database.batch();
        batch.update(senderRef, "lastMessage", lastMessage);
        batch.update(receiverRef, "lastMessage", lastMessage);
        batch.commit().addOnCompleteListener(onCompleteListener);

    }

    public void getChatSession(String uid, String chatSessionId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {

        database.collection(COLLECTION_ID)
                .document(uid)
                .collection(SESSIONS_COLLECTION_ID)
                .document(chatSessionId)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public void getChatSessions(String uid, OnCompleteListener<QuerySnapshot> onCompleteListener) {

        database.collection(COLLECTION_ID)
                .document(uid)
                .collection(SESSIONS_COLLECTION_ID)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }



    public void deleteSession(String uid, OnCompleteListener<Void> onCompleteListener) {

        database.collection(COLLECTION_ID)
                .document(uid)
                .delete()
                .addOnCompleteListener(onCompleteListener);
    }

}
