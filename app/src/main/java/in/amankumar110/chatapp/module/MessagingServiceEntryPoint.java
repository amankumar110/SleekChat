package in.amankumar110.chatapp.module;

import dagger.hilt.EntryPoint;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import in.amankumar110.chatapp.domain.usecases.user.SaveMessagingTokenUseCase;

@EntryPoint
@InstallIn(SingletonComponent.class)
public interface MessagingServiceEntryPoint {
    SaveMessagingTokenUseCase provideSaveMessagingTokenUseCase(); // or UseCase, etc.
}
