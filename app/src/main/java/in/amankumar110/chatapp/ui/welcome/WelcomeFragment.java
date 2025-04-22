package in.amankumar110.chatapp.ui.welcome;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import dagger.hilt.android.AndroidEntryPoint;
import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.databinding.FragmentWelcomeBinding;
import in.amankumar110.chatapp.ui.adapters.AppFeaturesStateAdapter;
import in.amankumar110.chatapp.utils.AnimationUtil;
import in.amankumar110.chatapp.utils.UiHelper;
import in.amankumar110.chatapp.viewmodels.token.RemoteTokenViewModel;
import in.amankumar110.chatapp.viewmodels.user.RealtimeStatusViewModel;

@AndroidEntryPoint
public class WelcomeFragment extends Fragment {

    private FragmentWelcomeBinding binding;
    private NavController navController;


    public WelcomeFragment() {

    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      binding = FragmentWelcomeBinding.inflate(inflater,container,false);
      return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        navController = Navigation.findNavController(view);

        binding.viewpagerFeatures.setAdapter(new AppFeaturesStateAdapter(requireActivity()));
        binding.wormDotsIndicator.attachTo(binding.viewpagerFeatures);

        binding.btnSignup.setOnClickListener(v -> navigateToSignupScreen());


    }

    private void navigateToSignupScreen() {
        navController.navigate(R.id.action_welcomeFragment_to_signupFragment);
    }

    private void navigateToMainScreen() {
        navController.navigate(R.id.action_welcomeFragment_to_mainFragment);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}