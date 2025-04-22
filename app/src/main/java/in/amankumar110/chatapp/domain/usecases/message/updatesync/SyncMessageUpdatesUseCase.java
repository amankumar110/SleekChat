package in.amankumar110.chatapp.domain.usecases.message.updatesync;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;

import java.util.List;

import in.amankumar110.chatapp.domain.repository.MessageRepository;
import in.amankumar110.chatapp.domain.repository.RealtimeMessageRepository;
import in.amankumar110.chatapp.domain.repository.RealtimeMessageUpdateRepository;
import in.amankumar110.chatapp.models.chat.Message;
import jakarta.inject.Inject;

public class SyncMessageUpdatesUseCase {

    private final MessageRepository messageRepository;
    private final RealtimeMessageUpdateRepository realtimeMessageUpdateRepository;

    @Inject
    public SyncMessageUpdatesUseCase(MessageRepository messageRepository,  RealtimeMessageUpdateRepository realtimeMessageUpdateRepository) {
        this.messageRepository = messageRepository;
        this.realtimeMessageUpdateRepository = realtimeMessageUpdateRepository;
    }

    public void execute(List<Message> updatedMessages, List<Message> deletedMessages, String sessionId, RealtimeMessageUpdateRepository.RealtimeMessageUpdateListener<Void> listener){

        Log.v("ChatWorker","I was least called");
        Log.v("ChatWorker:Updated",updatedMessages.toString());
        Log.v("ChatWorker:Deleted",deletedMessages.toString());


        updateUpdatedMessagesToDatabase(updatedMessages,sessionId,listener);
        updateDeletedMessagesToDatabase(deletedMessages,sessionId,listener);
    }

    private void updateDeletedMessagesToDatabase(List<Message> messageList,String sessionId, RealtimeMessageUpdateRepository.RealtimeMessageUpdateListener<Void> listener) {

        if(messageList==null || messageList.isEmpty()) {
            listener.onSuccess(null);
            return;
        }

        messageRepository.deleteMessages(messageList, sessionId, new MessageRepository.MessageListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                removeDeletedMessagesFromRealtimeDatabase(sessionId,listener);
            }

            @Override
            public void onError(Exception exception) {
                listener.onError(exception);
            }
        });
    }

    private void updateUpdatedMessagesToDatabase(List<Message> messageList,String sessionId, RealtimeMessageUpdateRepository.RealtimeMessageUpdateListener<Void> listener) {

        if(messageList==null || messageList.isEmpty()) {
            listener.onSuccess(null);
            return;
        }

        messageRepository.updateMessages(messageList, sessionId, new MessageRepository.MessageListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                removeUpdatedMessagesFromRealtimeDatabase(sessionId,listener);
            }

            @Override
            public void onError(Exception exception) {
                listener.onError(exception);
            }
        });
    }

    private void removeDeletedMessagesFromRealtimeDatabase(String sessionId, RealtimeMessageUpdateRepository.RealtimeMessageUpdateListener<Void> listener){
        realtimeMessageUpdateRepository.removeDeletedMessages(sessionId,listener);
    }

    private void removeUpdatedMessagesFromRealtimeDatabase(String sessionId, RealtimeMessageUpdateRepository.RealtimeMessageUpdateListener<Void> listener){

        realtimeMessageUpdateRepository.removeUpdatedMessages(sessionId,listener);
    }
}
