package in.amankumar110.chatapp.service.messaging;

import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import dagger.hilt.android.EntryPointAccessors;
import in.amankumar110.chatapp.MainActivity;
import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.domain.repository.UserRepository;
import in.amankumar110.chatapp.domain.usecases.user.SaveMessagingTokenUseCase;
import in.amankumar110.chatapp.module.MessagingServiceEntryPoint;
import in.amankumar110.chatapp.utils.MessagingTokenHelper;
import in.amankumar110.chatapp.utils.notifications.AppNotificationManager;
import jakarta.inject.Inject;

public class MessagingService extends FirebaseMessagingService {

    private static final String KEY_MESSAGE_TEXT = "messageText";
    private static final String KEY_SENDER_NUMBER = "senderNumber";

    @Override
    public void onNewToken(@NonNull String token) {

        Log.v("MessagingService", "Refreshed token: " + token);

        MessagingTokenHelper messagingTokenHelper = new MessagingTokenHelper(getApplicationContext());
        messagingTokenHelper.storeTokenIfRequired(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {

        String senderNumber,messageText;

        // Handling Data Payload
        if(!message.getData().isEmpty()) {

            Log.v("FcmApp", "From: " + message.getFrom());
            senderNumber = message.getData().get(KEY_SENDER_NUMBER);
            messageText = message.getData().get(KEY_MESSAGE_TEXT);

            Log.v("messaging","Sender Number: "+senderNumber);
            Log.v("messaging","Message Text: "+messageText);

            sendNotification(senderNumber,messageText);
        }
    }

    private void sendNotification(String title,String body) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String channelId = getString(R.string.default_notification_channel_id);
        AppNotificationManager appNotificationManager = new AppNotificationManager(this,channelId);
        appNotificationManager.showNotification(1,title,body,pendingIntent);
    }



}
