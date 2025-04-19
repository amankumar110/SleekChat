package in.amankumar110.chatapp.ui.welcome.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import in.amankumar110.chatapp.R;
import in.amankumar110.chatapp.databinding.FragmentFeatureBinding;
import in.amankumar110.chatapp.utils.ThemeManager;

public class FeatureFragment extends Fragment {

    private int featureImageResource;
    private int featureDescriptionStringResource;
    private int featureTaglineStringResource;

    private static final String ARG_FEATURE_IMAGE_RESOURCE_KEY = "feature_image";
    private static final String ARG_FEATURE_DESCRIPTION_STRING_RESOURCE_KEY = "feature_description";
    private static final String ARG_FEATURE_TAGLINE_STRING_RESOURCE_KEY = "feature_tagline";

    private FragmentFeatureBinding binding;

    // ✅ Required public no-arg constructor for Navigation Component
    public FeatureFragment() {
        // Empty constructor
    }

    public static FeatureFragment newInstance(
            @DrawableRes int featureImageResource,
            @StringRes int featureDescriptionStringResource,
            @StringRes int featureTaglineStringResource ) {

        Bundle args = new Bundle();
        args.putInt(ARG_FEATURE_IMAGE_RESOURCE_KEY, featureImageResource);
        args.putInt(ARG_FEATURE_DESCRIPTION_STRING_RESOURCE_KEY, featureDescriptionStringResource);
        args.putInt(ARG_FEATURE_TAGLINE_STRING_RESOURCE_KEY, featureTaglineStringResource);

        FeatureFragment fragment = new FeatureFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            featureImageResource = getArguments().getInt(ARG_FEATURE_IMAGE_RESOURCE_KEY);
            featureDescriptionStringResource = getArguments().getInt(ARG_FEATURE_DESCRIPTION_STRING_RESOURCE_KEY);
            featureTaglineStringResource = getArguments().getInt(ARG_FEATURE_TAGLINE_STRING_RESOURCE_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFeatureBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bindData();
    }

    private void bindData() {
        binding.ivFeatureImage.setImageResource(featureImageResource);
        binding.tvFeatureDescription.setText(featureDescriptionStringResource);
        binding.tvFeatureTagline.setText(featureTaglineStringResource);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
