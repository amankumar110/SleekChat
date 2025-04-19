package in.amankumar110.chatapp.models.chat;

import java.io.Serializable;

    public class ChatSession implements Serializable {

        private String sessionId;
        private String receiverNumber;
        private String senderNumber;
        private String lastMessage;
        private long lastMessageTime;
        private String receiverUid;
        private String senderUid;

    public ChatSession(String sessionId, String receiverNumber, String lastMessage, String senderNumber, long lastMessageTime, String receiverUid, String senderUid) {
        this.sessionId = sessionId;
        this.receiverNumber = receiverNumber;
        this.lastMessage = lastMessage;
        this.senderNumber = senderNumber;
        this.lastMessageTime = lastMessageTime;
        this.receiverUid = receiverUid;
        this.senderUid = senderUid;
    }

    public ChatSession() {
        // Default Constructor for Serial/De-Serialization
    }

    public String getReceiverNumber() {
        return receiverNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = receiverNumber;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public long getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(long lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }
}
