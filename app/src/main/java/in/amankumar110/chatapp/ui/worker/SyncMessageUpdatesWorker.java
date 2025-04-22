package in.amankumar110.chatapp.ui.worker;

import android.content.Context;
import android.util.Log;

import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import in.amankumar110.chatapp.data.remote.MessageService;
import in.amankumar110.chatapp.data.remote.RealtimeMessageUpdateService;
import in.amankumar110.chatapp.data.repository.MessageRepositoryImpl;
import in.amankumar110.chatapp.data.repository.RealtimeMessageUpdateRepositoryImpl;
import in.amankumar110.chatapp.domain.repository.MessageRepository;
import in.amankumar110.chatapp.domain.repository.RealtimeMessageUpdateRepository;
import in.amankumar110.chatapp.domain.usecases.message.updatesync.SyncMessageUpdatesUseCase;
import in.amankumar110.chatapp.models.chat.Message;
import jakarta.inject.Inject;

public class SyncMessageUpdatesWorker extends ListenableWorker {
    public static final String KEY_UPDATED_MESSAGES_JSON = "key_updated_messages_json";
    public static final String KEY_DELETED_MESSAGES_JSON = "key_deleted_messages_json";
    public static final String KEY_SESSION_ID = "key_session_id";

    private final MessageService messageService = new MessageService();
    private final RealtimeMessageUpdateService realtimeMessageUpdateService = new RealtimeMessageUpdateService();
    private final RealtimeMessageUpdateRepository realtimeMessageUpdateRepository = new RealtimeMessageUpdateRepositoryImpl(realtimeMessageUpdateService);
    private final MessageRepository messageRepository = new MessageRepositoryImpl(messageService);
    private final SyncMessageUpdatesUseCase syncMessageUpdatesUseCase = new SyncMessageUpdatesUseCase(messageRepository,realtimeMessageUpdateRepository);

    @Inject
    public SyncMessageUpdatesWorker(@NonNull Context context,
                              @NonNull WorkerParameters params) {
        super(context, params);
    }

    @Override
    public ListenableFuture<Result> startWork() {

        SettableFuture<Result> future = SettableFuture.create();

        String updatedMessagesJson = getInputData().getString(KEY_UPDATED_MESSAGES_JSON);
        String deletedMessagesJson = getInputData().getString(KEY_DELETED_MESSAGES_JSON);
        String sessionId = getInputData().getString(KEY_SESSION_ID);

        if (updatedMessagesJson == null || deletedMessagesJson == null || sessionId == null) {
            future.set(Result.failure());
            return future;
        }

        List<Message> updatedMessages;
        List<Message> deletedMessages;

        try {
            updatedMessages= new Gson().fromJson(updatedMessagesJson, new TypeToken<List<Message>>() {}.getType());
            deletedMessages = new Gson().fromJson(deletedMessagesJson, new TypeToken<List<Message>>() {}.getType());
        } catch (JsonSyntaxException e) {
            future.set(Result.failure());
            return future;
        }


        syncMessageUpdatesUseCase.execute(updatedMessages, deletedMessages, sessionId, new RealtimeMessageUpdateRepository.RealtimeMessageUpdateListener<Void>() {
            @Override
            public void onSuccess(Void data) {
                future.set(Result.success());
                Log.v("Worker","Succesful syncing of messages");
            }

            @Override
            public void onError(Exception exception) {
                future.set(Result.retry());
                Log.v("Worker","Failed syncing of messages");
            }
        });

        return future;
    }
}
