package in.amankumar110.chatapp.module;

import dagger.Component;
import in.amankumar110.chatapp.ui.worker.SyncMessagesWorker;

@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(SyncMessagesWorker worker);

    // Add a builder for Dagger to generate
    @Component.Builder
    interface Builder {
        AppComponent build();
        Builder appModule(AppModule appModule);
    }
}
