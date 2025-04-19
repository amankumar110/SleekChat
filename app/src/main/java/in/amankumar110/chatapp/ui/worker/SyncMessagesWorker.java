
package in.amankumar110.chatapp.ui.worker;

import android.content.Context;
import android.util.Log;

import androidx.hilt.work.HiltWorker;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.concurrent.Executor;

import in.amankumar110.chatapp.data.remote.MessageService;
import in.amankumar110.chatapp.data.remote.RealtimeMessageService;
import in.amankumar110.chatapp.data.repository.MessageRepositoryImpl;
import in.amankumar110.chatapp.data.repository.RealtimeMessageRepositoryImpl;
import in.amankumar110.chatapp.domain.repository.MessageRepository;
import in.amankumar110.chatapp.domain.repository.RealtimeMessageRepository;
import in.amankumar110.chatapp.domain.usecases.message.SyncMessagesUseCase;
import in.amankumar110.chatapp.models.chat.Message;
import in.amankumar110.chatapp.module.AppComponent;
import in.amankumar110.chatapp.module.AppModule;
import jakarta.inject.Inject;

public class SyncMessagesWorker extends ListenableWorker {

    public static final String KEY_MESSAGES_JSON = "key_messages_json";
    public static final String KEY_SESSION_ID = "key_session_id";
    private final MessageService messageService = new MessageService();
    private final RealtimeMessageService realtimeMessageService = new RealtimeMessageService();
    private final MessageRepository messageRepository = new MessageRepositoryImpl(messageService);
    private final RealtimeMessageRepository realtimeMessageRepository = new RealtimeMessageRepositoryImpl(realtimeMessageService);
    private final SyncMessagesUseCase syncMessagesUseCase = new SyncMessagesUseCase(messageRepository, realtimeMessageRepository);


    @Inject
    public SyncMessagesWorker(@NonNull Context context,
                              @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public ListenableFuture<Result> startWork() {


        SettableFuture<Result> future = SettableFuture.create();

        String messagesJson = getInputData().getString(KEY_MESSAGES_JSON);
        String sessionId = getInputData().getString(KEY_SESSION_ID);

        if (messagesJson == null || sessionId == null) {
            future.set(Result.failure());
            return future;
        }

        List<Message> messages;

        try {
            messages = new Gson().fromJson(messagesJson, new TypeToken<List<Message>>() {}.getType());
        } catch (JsonSyntaxException e) {
            future.set(Result.failure());
            return future;
        }

        syncMessagesUseCase.execute(messages, sessionId, new MessageRepository.MessageListener<>() {
            @Override
            public void onSuccess(Void result) {
                Log.v("Worker","Successfully Done The Sync");
                future.set(Result.success());
            }

            @Override
            public void onError(Exception exception) {
                Log.v("Worker","Successfully Failed The Sync");
                future.set(Result.retry());
            }
        });

        return future;
    }

}
