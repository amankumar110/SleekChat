package in.amankumar110.chatapp.module;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.data.local.TokenStorageService;
import in.amankumar110.chatapp.data.remote.ChatSessionService;
import in.amankumar110.chatapp.data.remote.MessageService;
import in.amankumar110.chatapp.data.remote.PhoneAuthService;
import in.amankumar110.chatapp.data.remote.RealtimeMessageService;
import in.amankumar110.chatapp.data.remote.RealtimeMessageUpdateService;
import in.amankumar110.chatapp.data.remote.RealtimeStatusService;
import in.amankumar110.chatapp.data.remote.UserService;
import in.amankumar110.chatapp.data.repository.ChatSessionRepositoryImpl;
import in.amankumar110.chatapp.data.repository.MessageRepositoryImpl;
import in.amankumar110.chatapp.data.repository.RealtimeMessageRepositoryImpl;
import in.amankumar110.chatapp.data.repository.RealtimeMessageUpdateRepositoryImpl;
import in.amankumar110.chatapp.data.repository.RealtimeStatusRepositoryImpl;
import in.amankumar110.chatapp.data.repository.RemoteTokenRepositoryImpl;
import in.amankumar110.chatapp.data.repository.PhoneAuthRepositoryImpl;
import in.amankumar110.chatapp.data.repository.TokenStorageRepositoryImpl;
import in.amankumar110.chatapp.data.repository.UserRepositoryImpl;
import in.amankumar110.chatapp.domain.repository.ChatSessionRepository;
import in.amankumar110.chatapp.domain.repository.MessageRepository;
import in.amankumar110.chatapp.domain.repository.PhoneAuthRepository;
import in.amankumar110.chatapp.domain.repository.RealtimeMessageRepository;
import in.amankumar110.chatapp.domain.repository.RealtimeMessageUpdateRepository;
import in.amankumar110.chatapp.domain.repository.RealtimeStatusRepository;
import in.amankumar110.chatapp.domain.repository.RemoteTokenRepository;
import in.amankumar110.chatapp.domain.repository.TokenStorageRepository;
import in.amankumar110.chatapp.domain.repository.UserRepository;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {


    @Provides
    @Singleton
    public static SharedPreferences provideSharedPreferences(@ApplicationContext Context context) {
        return context.getSharedPreferences(
                context.getString(R.string.APP_PREF_KEY),
                Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    public static PhoneAuthRepository providePhoneAuthRepository(PhoneAuthService phoneAuthService) {
        return new PhoneAuthRepositoryImpl(phoneAuthService);
    }

    @Provides
    @Singleton
    public static TokenStorageRepository provideTokenStorageRepository(@ApplicationContext Context context, TokenStorageService tokenStorageService) {
        return new TokenStorageRepositoryImpl(context,tokenStorageService);
    }

    @Provides
    @Singleton
    public static RemoteTokenRepository provideRemoteTokenRepository(@ApplicationContext Context context) {
        return new RemoteTokenRepositoryImpl(context);
    }

    @Provides
    @Singleton
    public static UserRepository provideUserRepository(@ApplicationContext Context context, UserService userService) {
        return new UserRepositoryImpl(context,userService);
    }

    @Provides
    @Singleton
    public static ChatSessionRepository provideChatSessionRepository(ChatSessionService chatSessionService) {
        return new ChatSessionRepositoryImpl(chatSessionService);
    }

    @Provides
    @Singleton
    public static MessageRepository provideMessageRepository(MessageService messageService) {
        return new MessageRepositoryImpl(messageService);
    }

    @Provides
    @Singleton
    public static RealtimeMessageRepository provideRealtimeMessageRepository(RealtimeMessageService realtimeMessageService) {
        return new RealtimeMessageRepositoryImpl(realtimeMessageService);
    }

    @Provides
    @Singleton
    public static RealtimeStatusRepository provideRealtimeStatusRepository(RealtimeStatusService realtimeStatusService) {
        return new RealtimeStatusRepositoryImpl(realtimeStatusService);
    }

    @Provides
    @Singleton
    public static RealtimeMessageUpdateRepository provideRealtimeMessageUpdateRepository(RealtimeMessageUpdateService realtimeMessageUpdateService) {
        return new RealtimeMessageUpdateRepositoryImpl(realtimeMessageUpdateService);
    }
}
