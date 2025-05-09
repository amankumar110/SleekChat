package in.amankumar110.chatapp.utils;

public class EventWrapper<T> {
    private final T content;
    private boolean hasBeenHandled = false;

    public EventWrapper(T content) {
        this.content = content;
    }

    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return content;
        }
    }

    public T peekContent() {
        return content;
    }
}
