package in.amankumar110.chatapp.domain.repository;

import in.amankumar110.chatapp.domain.common.Result;

public interface TokenStorageRepository {
    Result<String> getStoredToken();
    Result<Void> storeToken(String token);
}
