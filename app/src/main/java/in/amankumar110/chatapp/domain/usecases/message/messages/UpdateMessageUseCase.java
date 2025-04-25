package in.amankumar110.chatapp.domain.usecases.message.messages;

import android.content.Context;

import dagger.hilt.android.qualifiers.ApplicationContext;
import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.domain.repository.MessageRepository;
import in.amankumar110.chatapp.exceptions.InternetAccessException;
import in.amankumar110.chatapp.models.chat.Message;
import in.amankumar110.chatapp.utils.InternetHelper;
import jakarta.inject.Inject;

public class UpdateMessageUseCase {

    private final Context appContext;
    private final MessageRepository messageRepository;

    @Inject
    public UpdateMessageUseCase(MessageRepository messageRepository, @ApplicationContext Context context) {
        this.messageRepository = messageRepository;
        this.appContext = context;
    }

    public void execute(Message message, String sessionId, MessageRepository.MessageListener<Void> listener) {

        if(!InternetHelper.isInternetAvailable(appContext)){

            String internetErrorMessage = appContext.getString(R.string.internet_not_available);
            listener.onError(new InternetAccessException(internetErrorMessage));
            return;
        }
        messageRepository.updateMessage(message, sessionId,listener);
    }

}
