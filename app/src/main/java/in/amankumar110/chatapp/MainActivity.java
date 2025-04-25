package in.amankumar110.chatapp;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;

import dagger.hilt.android.AndroidEntryPoint;
import in.amankumar110.chatapp.utils.UiHelper;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private NavController navController;

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
        navController = navHostFragment.getNavController();

        if (savedInstanceState == null) {
            // Only set the nav graph manually on first creation
            boolean isLoggedIn = getIntent().getBooleanExtra(SplashActivity.ARG_IS_LOGGED_IN, false);
            NavInflater navInflater = navController.getNavInflater();
            NavGraph navGraph = navInflater.inflate(R.navigation.nav_graph);

            if (isLoggedIn) {
                navGraph.setStartDestination(R.id.mainFragment);
            } else {
                navGraph.setStartDestination(R.id.welcomeFragment);
            }

            navController.setGraph(navGraph);
        } else {
            // Restore navigation state
            navController.restoreState(savedInstanceState.getBundle("nav_state"));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save navigation controller state
        if (navController != null) {
            outState.putBundle("nav_state", navController.saveState());
        }
    }
}
