package in.amankumar110.chatapp.models.chat;

import androidx.annotation.Nullable;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id.equals(message.id);  // assuming `id` is non-null
    }

    public boolean getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(boolean seen) {
        isSeen = seen;
    }
}
