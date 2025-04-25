package in.amankumar110.chatapp.data.remote;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

import javax.inject.Inject;

import in.amankumar110.chatapp.models.user.User;

public class UserService {

    private final FirebaseFirestore database;
    private static final String COLLECTION_ID = "users";

    @Inject
    public UserService() {
        database = FirebaseFirestore.getInstance();
    }

    public void saveUser(User user, OnCompleteListener<Void> onCompleteListener) {
        database.collection(COLLECTION_ID).document(user.getUid()).set(user)
                .addOnCompleteListener(onCompleteListener);
    }

    public void getUser(String UId, OnCompleteListener<DocumentSnapshot> onCompleteListener) {
        database.collection(COLLECTION_ID).document(UId).get().addOnCompleteListener(onCompleteListener);
    }

    public void updateUser(String UId, Map<String,Object> updates, OnCompleteListener<Void> onCompleteListener) {

        database.collection(COLLECTION_ID).document(UId)
                .update(updates)
                .addOnCompleteListener(onCompleteListener);
    }

    public void getUserByPhoneNumber(String phoneNumber, OnCompleteListener<QuerySnapshot> onCompleteListener) {
        database.collection(COLLECTION_ID)
                .whereEqualTo("phoneNumber",phoneNumber)
                .get()
                .addOnCompleteListener(onCompleteListener);
    }

    public void saveMessagingToken(String uId, String token, OnCompleteListener<Void> onCompleteListener) {
        // Corrected method to update messagingToken
        database.collection(COLLECTION_ID)
                .document(uId)
                .update("messagingToken", token)  // Use update to set the messagingToken field
                .addOnCompleteListener(onCompleteListener);
    }

}
