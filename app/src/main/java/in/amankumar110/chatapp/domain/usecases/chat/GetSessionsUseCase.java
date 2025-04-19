package in.amankumar110.chatapp.domain.usecases.chat;

import java.util.List;

import javax.inject.Inject;

import in.amankumar110.chatapp.domain.repository.ChatSessionRepository;
import in.amankumar110.chatapp.models.chat.ChatSession;

public class GetSessionsUseCase {

    private final ChatSessionRepository chatSessionRepository;

    @Inject
    public GetSessionsUseCase(ChatSessionRepository chatSessionRepository) {
        this.chatSessionRepository = chatSessionRepository;
    }

    public void execute(String uid, ChatSessionRepository.ChatSessionListener<List<ChatSession>> listener) {
        chatSessionRepository.getChatSessions(uid,listener);
    }

}
