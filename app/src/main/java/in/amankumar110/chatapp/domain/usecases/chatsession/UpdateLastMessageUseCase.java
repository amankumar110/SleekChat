package in.amankumar110.chatapp.domain.usecases.chatsession;

import in.amankumar110.chatapp.domain.repository.ChatSessionRepository;
import in.amankumar110.chatapp.models.chat.ChatSession;
import jakarta.inject.Inject;

public class UpdateLastMessageUseCase {

    private final ChatSessionRepository chatSessionRepository;
    @Inject
    public UpdateLastMessageUseCase(ChatSessionRepository chatSessionRepository) {
        this.chatSessionRepository = chatSessionRepository;
    }

    public void execute(ChatSession chatSession,String lastMessage, ChatSessionRepository.ChatSessionListener<Void> listener) {
        chatSessionRepository.updateLastMessage(chatSession,lastMessage,listener);
    }
}
