
package in.amankumar110.chatapp.domain.usecases.message;

import android.content.Context;

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
import in.amankumar110.chatapp.ui.worker.SyncMessagesWorker;

public class SyncMessagesUseCaseWrapper {

    private final Context appContext;

    @Inject
    public SyncMessagesUseCaseWrapper(@ApplicationContext  Context appContext) {
        this.appContext = appContext;
    }

    public void syncInBackground(List<Message> messages, String sessionId) {
        String json = new Gson().toJson(messages);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        Data data = new Data.Builder()
                .putString(SyncMessagesWorker.KEY_MESSAGES_JSON, json)
                .putString(SyncMessagesWorker.KEY_SESSION_ID, sessionId)
                .build();

        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(SyncMessagesWorker.class)
                .setInputData(data)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, 10, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        WorkManager.getInstance(appContext).enqueue(request);

    }
}
