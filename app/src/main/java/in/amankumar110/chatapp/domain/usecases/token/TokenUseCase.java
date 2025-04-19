package in.amankumar110.chatapp.domain.usecases.token;

import javax.inject.Inject;

import in.amankumar110.chatapp.domain.common.Result;
import in.amankumar110.chatapp.domain.repository.TokenStorageRepository;

public class TokenUseCase {

    private final TokenStorageRepository tokenStorageRepository;

    @Inject
    public TokenUseCase(TokenStorageRepository tokenStorageRepository) {
        this.tokenStorageRepository = tokenStorageRepository;
    }

    public Result<Void> storeToken(String token) {
        return tokenStorageRepository.storeToken(token);
    }

    public Result<String> getToken() {
        return tokenStorageRepository.getStoredToken();
    }
}
