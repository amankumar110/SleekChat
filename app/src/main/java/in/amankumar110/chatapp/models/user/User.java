package in.amankumar110.chatapp.models.user;

public class User {
    private String uid;
    private String phoneNumber;
    private long lastSeen;
    private boolean isOnline;
    private String messagingToken;

    public User() {}

    public User(String uid, String phoneNumber, long lastSeen, boolean isOnline) {
        this.uid = uid;
        this.phoneNumber = phoneNumber;
        this.lastSeen = lastSeen;
        this.isOnline = isOnline;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isUserEmpty() {
        return (uid == null && phoneNumber == null && lastSeen == 0);
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean online) {
        isOnline = online;
    }

    public String getMessagingToken() {
        return messagingToken;
    }

    public void setMessagingToken(String messagingToken) {
        this.messagingToken = messagingToken;
    }
}