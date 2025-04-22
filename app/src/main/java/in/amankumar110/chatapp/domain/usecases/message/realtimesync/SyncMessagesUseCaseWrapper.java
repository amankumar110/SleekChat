
package in.amankumar110.chatapp.domain.usecases.message.realtimesync;

import android.content.Context;
import android.util.Log;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.gson.Gson;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import in.amankumar110.chatapp.models.chat.Message;
import in.amankumar110.chatapp.ui.worker.SyncMessageUpdatesWorker;
import in.amankumar110.chatapp.ui.worker.SyncMessagesWorker;

public class SyncMessagesUseCaseWrapper {

    private final Context appContext;

    @Inject
    public SyncMessagesUseCaseWrapper(@ApplicationContext  Context appContext) {
        this.appContext = appContext;
    }

    public void syncInBackground(List<Message> realtimeMessages,
                                 List<Message> updateMessages,
                                 List<Message> deleteMessages,
                                 String sessionId) {

        String json = new Gson().toJson(realtimeMessages);
        String updatedMessagesJson = new Gson().toJson(updateMessages);
        String deletedMessagesJson = new Gson().toJson(deleteMessages);

        Log.v("SyncWorker:syncMessages",json);
        Log.v("SyncWorker:updatedMessages",updatedMessagesJson);
        Log.v("SyncWorker:deletedMessages",deletedMessagesJson);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();


        // Sync Messages To database Request
        Data messageSyncData = new Data.Builder()
                .putString(SyncMessagesWorker.KEY_MESSAGES_JSON, json)
                .putString(SyncMessagesWorker.KEY_SESSION_ID, sessionId)
                .build();

        OneTimeWorkRequest messageSyncRequest = new OneTimeWorkRequest.Builder(SyncMessagesWorker.class)
                .setInputData(messageSyncData)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 10, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        // update messages request
        Data messageUpdatesSyncData = new Data.Builder()
                .putString(SyncMessageUpdatesWorker.KEY_UPDATED_MESSAGES_JSON, updatedMessagesJson)
                .putString(SyncMessageUpdatesWorker.KEY_DELETED_MESSAGES_JSON, deletedMessagesJson)
                .putString(SyncMessageUpdatesWorker.KEY_SESSION_ID,sessionId)
                .build();

        OneTimeWorkRequest messageUpdatesSyncRequest= new OneTimeWorkRequest.Builder(SyncMessageUpdatesWorker.class)
                .setInputData(messageUpdatesSyncData)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 10, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        WorkManager.getInstance(appContext)
                .beginWith(messageSyncRequest)
                .then(messageUpdatesSyncRequest)
                .enqueue();

    }
}
