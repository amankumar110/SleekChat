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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;
import in.amankumar110.chatapp.utils.AnimationUtil;
import in.amankumar110.chatapp.utils.InternetHelper;
import in.amankumar110.chatapp.utils.UiHelper;
import in.amankumar110.chatapp.viewmodels.token.RemoteTokenViewModel;
import jakarta.inject.Inject;

@AndroidEntryPoint
public class SplashActivity extends AppCompatActivity {

    public static final String ARG_IS_LOGGED_IN = "is_Logged_in";
    private boolean isLoginComplete = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // ✅ This should be at the top!

        // Setup Android 12+ splash screen
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> !isLoginComplete); // Inverted condition

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (!InternetHelper.isInternetAvailable(this)) {
            // No internet – can’t validate session
            exitAppWithInternetWarning();
        } else if (user==null) {
            // User never signed in
            navigateToLoginScreen();
        } else {
            // Try to refresh token to ensure the session is valid
            user.getIdToken(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Token is valid → go to main screen
                    navigateToMainScreen();
                } else {
                    // Token refresh failed → clear session and go to login
                    FirebaseAuth.getInstance().signOut();
                    navigateToLoginScreen();
                }
            });
        }

        // Setup UI
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void exitAppWithInternetWarning() {
        UiHelper.showInternetWarning(this);
        isLoginComplete = true;
        finishAffinity();

    }

    private void navigateToMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ARG_IS_LOGGED_IN,true);
        startActivity(intent);
        isLoginComplete = true;
        finish();
    }

    private void navigateToLoginScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(ARG_IS_LOGGED_IN,false);
        startActivity(intent);
        isLoginComplete = true;
        finish();
    }

}