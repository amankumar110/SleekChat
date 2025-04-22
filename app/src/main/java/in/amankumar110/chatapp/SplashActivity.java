package in.amankumar110.chatapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;
import in.amankumar110.chatapp.utils.AnimationUtil;
import in.amankumar110.chatapp.utils.UiHelper;
import in.amankumar110.chatapp.viewmodels.token.RemoteTokenViewModel;
import jakarta.inject.Inject;

@AndroidEntryPoint
public class SplashActivity extends AppCompatActivity {

    public static final String ARG_IS_LOGGED_IN = "is_Logged_in";
    private boolean isLoginComplete = false;

    private RemoteTokenViewModel remoteTokenViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // ✅ This should be at the top!

        remoteTokenViewModel = new ViewModelProvider(this).get(RemoteTokenViewModel.class);

        // Setup Android 12+ splash screen
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> !isLoginComplete); // Inverted condition

        // Init ViewModel
        remoteTokenViewModel = new ViewModelProvider(this).get(RemoteTokenViewModel.class);

        // Start login logic
        requestLogin();

        // Setup UI
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void requestLogin() {
        remoteTokenViewModel.getRemoteToken();

        // ✅ Observe token and decide navigation
        remoteTokenViewModel.token.observe(this, token -> {

            if (isLoginComplete) return;

            if (remoteTokenViewModel.getErrorMessage() != null) {
                isLoginComplete = true;
                UiHelper.showMessage(this, R.string.login_error_message);
                finishAffinity();
                return;
            }

            isLoginComplete = true;
            if (Boolean.TRUE.equals(remoteTokenViewModel.shouldSignIn.getValue())) {
                navigateToLoginScreen();
            } else {
                navigateToMainScreen();
            }

        });
    }

    private void navigateToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ARG_IS_LOGGED_IN,true);
        startActivity(intent);
        finish();
    }

    private void navigateToLoginScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ARG_IS_LOGGED_IN,false);
        startActivity(intent);
        finish();
    }

}