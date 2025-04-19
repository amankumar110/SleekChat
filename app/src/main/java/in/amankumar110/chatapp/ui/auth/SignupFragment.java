package in.amankumar110.chatapp.ui.auth;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;
import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.databinding.FragmentSignupBinding;
import in.amankumar110.chatapp.models.user.User;
import in.amankumar110.chatapp.utils.AnimationUtil;
import in.amankumar110.chatapp.utils.CountryCodeUtil;
import in.amankumar110.chatapp.utils.UiHelper;
import in.amankumar110.chatapp.viewmodels.auth.PhoneAuthViewModel;
import in.amankumar110.chatapp.viewmodels.user.UserViewModel;

@AndroidEntryPoint
public class SignupFragment extends Fragment {

    private FragmentSignupBinding binding;
    private PhoneAuthViewModel phoneAuthViewModel;
    private UserViewModel userViewModel;
    private String contactNumber;
    private NavController navController;
    

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phoneAuthViewModel = new ViewModelProvider(this).get(PhoneAuthViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSignupBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        navController = Navigation.findNavController(view);

        // Initially Hide Views Related to OTP Retrieval Before Sending OTP
        hideOtpViews();

        // Set default country for country code selection view
        binding.countrySelectionView.setDefaultCountry(CountryCodeUtil.getUsCountry());

        // Hide Keyboard Once Filled
        binding.otpView.setOtpCompletionListener(otp ->
                UiHelper.hideKeyboard(requireContext(),binding.otpView.getWindowToken()));

        // Send Verification Code on Send Btn Click
        binding.btnSendOtp.setOnClickListener(v -> sendVerificationCode());

        // Bind Observers
        bindPhoneObservers();
        bindUserObserver();
    }

    private void sendVerificationCode() {
        // Todo: Impl Sending Mechanism

        String code = binding.countrySelectionView.getSelectedCode();
        String number = binding.etPhoneNumber.getText().toString();
        contactNumber = code+number;

        if(number.isEmpty() || !CountryCodeUtil.isPhoneNumberValid(contactNumber)) {
            Toast.makeText(requireContext(), getString(R.string.invalid_contact_number_message), Toast.LENGTH_SHORT).show();
            return;
        }
        phoneAuthViewModel.sendOtp(contactNumber);
    }

    private void verifyCode() {
        // Todo: Impl Verification system
        String verifyCode = Objects.requireNonNull(binding.otpView.getText()).toString();
        phoneAuthViewModel.verifyOtp(verifyCode);
    }


    private void bindPhoneObservers() {

        phoneAuthViewModel.isOtpSent.observe(getViewLifecycleOwner(), isOtpSent -> {

            if(isOtpSent) {

                UiHelper.showMessage(requireContext(),"OTP Sent To: "+contactNumber);
                showOtpViews();
            } else if(!phoneAuthViewModel.errorMessage.getValue().isEmpty())
                UiHelper.showMessage(requireContext(),R.string.otp_not_sent_message);

        });

        phoneAuthViewModel.isVerified.observe(getViewLifecycleOwner(), isVerified -> {

            if(isVerified)
                saveUserToDatabase();
            else if(phoneAuthViewModel.isOtpSent.getValue() && !phoneAuthViewModel.errorMessage.getValue().isEmpty())
                UiHelper.showMessage(requireContext(),R.string.number_not_verified_message);
            else if(!phoneAuthViewModel.errorMessage.getValue().isEmpty())
               UiHelper.showMessage(requireContext(),R.string.unknown_exception_message);

        });

        phoneAuthViewModel.errorMessage.observe(getViewLifecycleOwner(),error ->
                Log.e("PhoneAuthError",error));

    }

    private void showOtpViews() {

        List<View> otpViews = new ArrayList<>();
        otpViews.add(binding.otpView);
        otpViews.add(binding.btnVerifyOtp);
        AnimationUtil.fadeInTogether(otpViews,0.5f,1f);
        binding.btnVerifyOtp.setClickable(true);

        // Verify Otp when clicked
        binding.btnVerifyOtp.setOnClickListener(v -> verifyCode());
    }

    private void hideOtpViews() {
        List<View> otpViews = new ArrayList<>();
        otpViews.add(binding.otpView);
        otpViews.add(binding.btnVerifyOtp);
        AnimationUtil.fadeOutTogether(otpViews,1f,0.5f);

        binding.btnVerifyOtp.setClickable(false);
    }

    private void saveUserToDatabase() {
        String Uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        User user = new User(Uid,contactNumber,System.currentTimeMillis(),true);
        userViewModel.saveUser(user);
    }

    private void bindUserObserver() {

        userViewModel.userSavedToDatabase.observe(getViewLifecycleOwner(), isSaved -> {

            if(isSaved == null)
                return;

            // Remove Observer to avoid duplication of data on database
            userViewModel.userSavedToDatabase.removeObservers(getViewLifecycleOwner());

            if(isSaved) {
                Toast.makeText(requireContext(), "User Saved, Navigate to Main", Toast.LENGTH_SHORT).show();
                navigateToMainScreen();

            } else {
                String message = userViewModel.errorMessage.getValue();
                assert message != null;
                Log.e("ChatAppError",message);
                UiHelper.showMessage(requireContext(),R.string.unknown_exception_message);
            }
        });
    }

    private void navigateToMainScreen() {
        navController.navigate(R.id.action_signupFragment_to_mainFragment);
    }
}