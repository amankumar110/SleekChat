package in.amankumar110.chatapp.domain.common;

public abstract class Result<T> {


    private Result() {} // Prevent instantiation

    public static final class Success<T> extends Result<T> {
        private final T data;

        public Success(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }

    public static final class Error<T> extends Result<T> {
        private final Exception error;

        public Error(Exception error) {
            this.error = error;
        }

        public Exception getError() {
            return error;
        }
    }
}
