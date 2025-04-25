package in.amankumar110.chatapp.domain.usecases.chatsession;

import jakarta.inject.Inject;

public class GenerateSessionIdUseCase {

    @Inject
    public GenerateSessionIdUseCase() {
    }

    public String execute(String senderUid, String receiverUid) {
        return senderUid.compareTo(receiverUid) < 0
                ? senderUid + "_" + receiverUid
                : receiverUid + "_" + senderUid;
    }

}
