package in.amankumar110.chatapp.domain.usecases.chat;

import javax.inject.Inject;

import in.amankumar110.chatapp.domain.common.Result;
import in.amankumar110.chatapp.domain.repository.ChatSessionRepository;
import in.amankumar110.chatapp.domain.repository.UserRepository;
import in.amankumar110.chatapp.exceptions.ChatSessionException;
import in.amankumar110.chatapp.models.chat.ChatSession;
import in.amankumar110.chatapp.models.user.User;

public class CreateSessionUseCase {

    private final UserRepository userRepository;
    private final ChatSessionRepository chatSessionRepository;

    @Inject
    public CreateSessionUseCase(UserRepository userRepository, ChatSessionRepository chatSessionRepository) {
        this.userRepository = userRepository;
        this.chatSessionRepository = chatSessionRepository;
    }

    public void execute(String senderPhoneNumber, String receiverPhoneNumber, ChatSessionRepository.ChatSessionListener<Void> listener) {

        userRepository.getUserByPhoneNumber(senderPhoneNumber, new UserRepository.UserListener<User>() {
            @Override
            public void onSuccess(User sender) {
                fetchReceiverAndCreateSession(sender, receiverPhoneNumber, listener);
            }

            @Override
            public void onError(Exception exception) {
                listener.onError(new ChatSessionException("Session Could Not Be Created: Sender Uid - " + exception.getMessage()));
            }
        });
    }

    private void fetchReceiverAndCreateSession(User sender, String receiverPhoneNumber, ChatSessionRepository.ChatSessionListener<Void> listener) {
        userRepository.getUserByPhoneNumber(receiverPhoneNumber, new UserRepository.UserListener<User>() {
            @Override
            public void onSuccess(User receiver) {
                createChatSession(sender, receiver, listener);
            }

            @Override
            public void onError(Exception exception) {
                listener.onError(new ChatSessionException("Session Could Not Be Created: Receiver Uid - " + exception.getMessage()));
            }
        });
    }

    private void createChatSession(User sender, User receiver, ChatSessionRepository.ChatSessionListener<Void> listener) {
        String sessionId = chatSessionRepository.generateSessionId(sender.getUid(), receiver.getUid());

        ChatSession senderSession = new ChatSession(
                sessionId,
                receiver.getPhoneNumber(),
                "",  // lastMessage is empty by default
                sender.getPhoneNumber(),
                System.currentTimeMillis(),
                receiver.getUid(),
                sender.getUid()
        );

        ChatSession receiverSession = new ChatSession(
                sessionId,
                sender.getPhoneNumber(),
                "",  // lastMessage is empty by default
                receiver.getPhoneNumber(),
                System.currentTimeMillis(),
                sender.getUid(),
                receiver.getUid()
        );

        chatSessionRepository.createChatSession(senderSession, receiverSession, listener);
    }



}


