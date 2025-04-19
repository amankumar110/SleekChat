package in.amankumar110.chatapp.models.chat;

public class Message {

    private String senderUId, receiverUId, message, id;
    private Long sentAt;

    private boolean isSeen=false,  isSynced = true;

    public Message() {
    }

    public String getSenderUId() {
        return senderUId;
    }

    public void setSenderUId(String senderUId) {
        this.senderUId = senderUId;
    }

    public String getReceiverUId() {
        return receiverUId;
    }

    public void setReceiverUId(String receiverUId) {
        this.receiverUId = receiverUId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getSentAt() {
        return sentAt;
    }

    public void setSentAt(Long sentAt) {
        this.sentAt = sentAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    @Override
    public String toString() {
        return "Message{" +
                "senderUId='" + senderUId + '\'' +
                ", receiverUId='" + receiverUId + '\'' +
                ", message='" + message + '\'' +
                ", id='" + id + '\'' +
                ", sentAt=" + sentAt +
                ", isSeen=" + isSeen +
                '}';
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
