package in.amankumar110.chatapp.data.repository;

import android.content.Context;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.data.local.TokenStorageService;
import in.amankumar110.chatapp.domain.common.Result;
import in.amankumar110.chatapp.domain.repository.TokenStorageRepository;
import in.amankumar110.chatapp.exceptions.TokenFailureException;

public class TokenStorageRepositoryImpl implements TokenStorageRepository {

    private final TokenStorageService tokenStorageService;
    private final Context appcontext;
    @Inject
    public TokenStorageRepositoryImpl(@ApplicationContext Context context,
                                      TokenStorageService tokenStorageService) {

        this.tokenStorageService = tokenStorageService;
        this.appcontext = context;
    }

    @Override
    public Result<String> getStoredToken() {
        String token = tokenStorageService.getToken();

        if(token == null)
            return new Result.Error<>(new TokenFailureException(appcontext.getString(R.string.token_not_found_exception)));

        return new Result.Success<>(token);
    }

    @Override
    public Result<Void> storeToken(String token) {
        tokenStorageService.storeToken(token);
        return new Result.Success<>(null);
    }
}
