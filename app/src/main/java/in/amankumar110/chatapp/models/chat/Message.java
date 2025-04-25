package in.amankumar110.chatapp.models.chat;

import androidx.annotation.Nullable;

import java.util.List;

public class Message {

    public enum MessageStatus {

        SENT("sent"),
        RECEIVER_ONLINE("receiver_online"),
        SEEN("seen");

        MessageStatus(String status) {
            this.status = status;
        }

        private String status;

        public String getStatus() {
            return status;
        }

        public static boolean shouldUpdateStatus(String current, String incoming) {
            if (current == null) return true;

            List<String> order = List.of(
                    Message.MessageStatus.SENT.getStatus(),
                    Message.MessageStatus.RECEIVER_ONLINE.getStatus(),
                    Message.MessageStatus.SEEN.getStatus()
            );

            return order.indexOf(incoming) > order.indexOf(current);
        }

    }

    private String senderUId, receiverUId, message, id;
    private Long sentAt;

    private String messageStatus;

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


    @Override
    public String toString() {
        return "Message{" +
                "messageStatus='" + messageStatus + '\'' +
                ", sentAt=" + sentAt +
                ", id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", receiverUId='" + receiverUId + '\'' +
                ", senderUId='" + senderUId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return id.equals(message.id);  // assuming `id` is non-null
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

}
