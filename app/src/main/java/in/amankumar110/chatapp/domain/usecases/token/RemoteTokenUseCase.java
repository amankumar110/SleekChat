package in.amankumar110.chatapp.domain.usecases.token;

import javax.inject.Inject;

import in.amankumar110.chatapp.domain.repository.RemoteTokenRepository;

public class RemoteTokenUseCase {

    private final RemoteTokenRepository remoteTokenRepository;

    @Inject
    public RemoteTokenUseCase(RemoteTokenRepository remoteTokenRepository) {
        this.remoteTokenRepository = remoteTokenRepository;
    }

    public void getRemoteToken(RemoteTokenRepository.TokenCallback tokenCallback) {
        remoteTokenRepository.getRemoteToken(tokenCallback);
    }

}
