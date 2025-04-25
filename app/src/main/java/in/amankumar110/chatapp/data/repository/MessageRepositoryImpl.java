package in.amankumar110.chatapp.data.repository;

import static android.util.Log.v;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import in.amankumar110.chatapp.data.remote.MessageService;
import in.amankumar110.chatapp.domain.repository.MessageRepository;
import in.amankumar110.chatapp.exceptions.MessageException;
import in.amankumar110.chatapp.models.chat.ChatSession;
import in.amankumar110.chatapp.models.chat.Message;

public class MessageRepositoryImpl implements MessageRepository {


    private final MessageService messageService;

    @Inject
    public MessageRepositoryImpl(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void getMessages(String sessionId, MessageListener<List<Message>> listener) {

        messageService.getAllMessages(sessionId, task -> {
            if (!task.isSuccessful()) {
                handleError(listener, "Messages Couldn't be Fetched");
                return;
            }

            List<Message> messages = task.getResult().getDocuments().stream()
                    .map(doc -> doc.toObject(Message.class))
                    .collect(Collectors.toList());

            Log.v("MessagesFromRepo",messages.toString());

            listener.onSuccess(messages);
        });
    }

    private void handleError(MessageListener listener, String s) {
        listener.onError(new MessageException(s));
    }

    @Override
    public void addMessage(Message message, String sessionId, MessageListener<Void> listener) {

        messageService.addMessage(message, sessionId, task -> {

            if(task.isSuccessful())
                listener.onSuccess(null);
            else
                handleError(listener, "Message Couldn't be Sent");
        });
    }

    @Override
    public void addMessages(List<Message> messages, String sessionId, MessageListener<Void> listener) {

        messageService.addMessages(messages, sessionId, task -> {

            if(task.isSuccessful())
                listener.onSuccess(null);
            else
                handleError(listener, "Messages Couldn't be Added");
        });
    }

    @Override
    public void markMessageAsSeen(List<Message> messages, ChatSession chatSession, MessageListener<Void> listener) {

        messageService.setMessageAsSeen(messages, chatSession, task -> {

            if(task.isSuccessful())
                listener.onSuccess(null);
            else
                handleError(listener, "Message Couldn't be Marked As Seen");
        });
    }

    @Override
    public void updateMessagesStatus(List<Message> messages, ChatSession chatSession, MessageListener<Void> listener) {

        messageService.updateMessagesStatus(messages,chatSession,task -> {

            if(task.isSuccessful())
                listener.onSuccess(null);
            else
                handleError(listener, "Message Status Couldn't be Updated");
        });
    }

    @Override
    public void updateMessage(Message message, String sessionId, MessageListener<Void> listener) {

        messageService.updateMessage(message, sessionId, task -> {

            if(task.isSuccessful())
                listener.onSuccess(null);
            else
                handleError(listener, "Message Couldn't be Updated");
        });
    }

    @Override
    public void deleteMessages(List<Message> messages, String sessionId, MessageListener<Void> listener) {

        messageService.deleteMessages(messages, sessionId, task -> {

            if(task.isSuccessful())
                listener.onSuccess(null);
            else
                handleError(listener, "Messages Couldn't be Deleted");
        });
    }

    @Override
    public void updateMessages(List<Message> messages, String sessionId, MessageListener<Void> listener) {

        messageService.updateMessages(messages,sessionId,task -> {

            if(task.isSuccessful())
                listener.onSuccess(null);
            else
                handleError(listener, "Messages Couldn't be Updated");
        });
    }

}
