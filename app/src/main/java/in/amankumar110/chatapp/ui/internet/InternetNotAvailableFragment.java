package in.amankumar110.chatapp.ui.internet;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.amankumar110.chatapp.databinding.FragmentInternetNotAvailableBinding;
import in.amankumar110.chatapp.utils.AnimationUtil;
import in.amankumar110.chatapp.utils.NetworkConnectionLiveData;

public class InternetNotAvailableFragment extends DialogFragment {

    private FragmentInternetNotAvailableBinding binding;
    private NetworkConnectionLiveData networkConnectionLiveData;

    // Avoid static reference to prevent memory leaks
    public static InternetNotAvailableFragment newInstance() {
        InternetNotAvailableFragment fragment = new InternetNotAvailableFragment();
        fragment.setCancelable(false);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(MATCH_PARENT, MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentInternetNotAvailableBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
        observeNetwork();
    }

    private void setupUI() {
        AnimationUtil.fadeIn(binding.getRoot(), () -> {});
        binding.vInternetNotAvailableItem.btnCloseApp.setOnClickListener(v -> {
            requireActivity().finishAffinity();
        });
    }

    private void observeNetwork() {
        networkConnectionLiveData = new NetworkConnectionLiveData(requireContext());
        networkConnectionLiveData.observe(getViewLifecycleOwner(), isConnected -> {
            if (isConnected && isAdded()) {
                dismissAllowingStateLoss(); // Safer dismissal
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Proper view binding cleanup
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (binding != null) {
            AnimationUtil.fadeOut(binding.getRoot(), this::dismissAllowingStateLoss);
        }
    }
}
