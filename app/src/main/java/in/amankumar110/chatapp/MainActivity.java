package in.amankumar110.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;
import in.amankumar110.chatapp.data.remote.RealtimeStatusService;
import in.amankumar110.chatapp.utils.UiHelper;
import in.amankumar110.chatapp.viewmodels.user.RealtimeStatusViewModel;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        UiHelper.setStatusBarColor(android.R.attr.colorBackground, getTheme(), getWindow());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        boolean isLoggedIn = getIntent().getBooleanExtra(SplashActivity.ARG_IS_LOGGED_IN, false);

        NavInflater navInflater = navController.getNavInflater();
        NavGraph navGraph = navInflater.inflate(R.navigation.nav_graph); // your nav_graph file

        Log.v("LoggedIn", String.valueOf(isLoggedIn));

        if (isLoggedIn) {
            navGraph.setStartDestination(R.id.mainFragment); // change to your main fragment ID
        } else {
            navGraph.setStartDestination(R.id.welcomeFragment); // change to your welcome/login fragment ID
        }

        navController.setGraph(navGraph);
    }
}