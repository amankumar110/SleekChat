package in.amankumar110.chatapp.models.user;

public class UserStatus {

    public enum Status {
        ONLINE("online"),
        OFFLINE("offline"),
        IN_CHAT("in_chat");

        Status(String status) {
            this.name = status;
        }

        private final String name;

        public String getName() {
            return name;
        }
    }

    private String status;
    private long lastSeen;

    public UserStatus(Status status, long lastSeen) {
        this.status = status.getName();
        this.lastSeen = lastSeen;
    }

    public UserStatus() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(long lastSeen) {
        this.lastSeen = lastSeen;
    }
}
