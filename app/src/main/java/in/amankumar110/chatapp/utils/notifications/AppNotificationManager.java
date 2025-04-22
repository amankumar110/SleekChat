package in.amankumar110.chatapp.utils.notifications;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;

import androidx.activity.result.ActivityResultLauncher;


import android.app.Notification;
import android.app.NotificationChannel;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import in.amankumar110.chatapp.R;

public class AppNotificationManager {

    private static String CHANNEL_ID = "default_channel_id";
    private static final String CHANNEL_NAME = "Default Channel";
    private final Context context;
    private final NotificationManager notificationManager;

    public AppNotificationManager(Context context) {

        this.context = context.getApplicationContext();
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(); // Create channel on initialization
    }

    public AppNotificationManager(Context context, String channelId) {

        CHANNEL_ID = channelId;
        this.context = context.getApplicationContext();
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel(); // Create channel on initialization
    }

    public void requestNotificationPermissionIfRequired(ActivityResultLauncher<String> permissionLauncher) {

        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    public void showNotification(int id, String title, String message) {
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build();

        if (notificationManager != null) {
            notificationManager.notify(id, notification);
        }
    }


    public void showNotification(int id, String title, String message, PendingIntent pendingIntent) {
        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        if (notificationManager != null) {
            notificationManager.notify(id, notification);
        }
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager != null) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for general notifications");
            notificationManager.createNotificationChannel(channel);
        }
    }
}
