package in.amankumar110.chatapp.domain.usecases.message.messages;

import android.content.Context;

import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.domain.repository.MessageRepository;
import in.amankumar110.chatapp.exceptions.InternetAccessException;
import in.amankumar110.chatapp.models.chat.Message;
import in.amankumar110.chatapp.utils.InternetHelper;

public class GetMessagesUseCase {

    private final MessageRepository messageRepository;
    private final Context appContext;
    @Inject
    public GetMessagesUseCase(MessageRepository messageRepository, @ApplicationContext Context context) {
        this.messageRepository = messageRepository;
        this.appContext = context;
    }

    public void execute(String sessionId, MessageRepository.MessageListener<List<Message>> listener) {

        if(!InternetHelper.isInternetAvailable(appContext)){
            String internetErrorMessage = appContext.getString(R.string.internet_not_available);
            listener.onError(new InternetAccessException(internetErrorMessage));
            return;
        }

        messageRepository.getMessages(sessionId, new MessageRepository.MessageListener<>() {
            @Override
            public void onSuccess(List<Message> result) {

                result.sort(Comparator.comparingLong(Message::getSentAt));
                listener.onSuccess(result);
            }

            @Override
            public void onError(Exception exception) {
                listener.onError(exception);
            }
        });
    }
}
