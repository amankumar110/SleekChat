package in.amankumar110.chatapp.domain.usecases.message.messages;

import android.content.Context;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.domain.repository.MessageRepository;
import in.amankumar110.chatapp.exceptions.InternetAccessException;
import in.amankumar110.chatapp.models.chat.Message;
import in.amankumar110.chatapp.utils.InternetHelper;

public class SendMessageUseCase {


    private final MessageRepository messageRepository;
    private final Context appContext;

    @Inject
    public SendMessageUseCase(MessageRepository messageRepository, @ApplicationContext Context context) {
        this.messageRepository = messageRepository;
        this.appContext = context;
    }

    public void execute(Message message, String sessionId, MessageRepository.MessageListener<Void> listener) {

        if(!isInternetAvailable()) {
            String internetErrorMessage = appContext.getString(R.string.internet_not_available);
            listener.onError(new InternetAccessException(internetErrorMessage));
            return;
        }
        messageRepository.addMessage(message,sessionId,listener);
    }

    public boolean isInternetAvailable() {
        return InternetHelper.isInternetAvailable(appContext);
    }

}
