package in.amankumar110.chatapp.domain.repository;

public interface RemoteTokenRepository {

    public interface TokenCallback {
        void onSuccess(String token);
        void onError(Exception exception);
    }
    void getRemoteToken(TokenCallback tokenCallback);


}
