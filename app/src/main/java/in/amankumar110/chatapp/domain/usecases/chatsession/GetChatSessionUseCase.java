package in.amankumar110.chatapp.domain.usecases.chatsession;

import in.amankumar110.chatapp.domain.repository.ChatSessionRepository;
import in.amankumar110.chatapp.models.chat.ChatSession;
import jakarta.inject.Inject;

public class GetChatSessionUseCase {

    private final ChatSessionRepository chatSessionRepository;

    @Inject
    public GetChatSessionUseCase(ChatSessionRepository chatSessionRepository) {
        this.chatSessionRepository = chatSessionRepository;
    }

    public void execute(String uid, String sessionId, ChatSessionRepository.ChatSessionListener<ChatSession> listener) {
        chatSessionRepository.getChatSession(uid, sessionId, listener);
    }
}
