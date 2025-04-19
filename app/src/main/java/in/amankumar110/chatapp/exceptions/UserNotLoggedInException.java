package in.amankumar110.chatapp.exceptions;

public class UserNotLoggedInException extends RuntimeException{

    public UserNotLoggedInException(String message) {
        super(message);
    }
}
