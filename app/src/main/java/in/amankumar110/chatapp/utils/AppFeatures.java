package in.amankumar110.chatapp.utils;

import java.util.Arrays;
import java.util.List;

import in.amankumar110.chatapp.R;

public enum AppFeatures {

    COMMUNICATION(
            R.drawable.ic_feature_communication,
            R.string.feature_communication_description,
            R.string.feature_communication_tagline),

    DARK_MODE(
            R.drawable.ic_feature_dark_mode,
            R.string.feature_dark_mode_description,
            R.string.feature_dark_mode_tagline
    );

    private int imageResource;
    private int featureDescriptionStringResource;
    private int featureTaglineStringResource;

    AppFeatures(int imageResource, int featureDescriptionStringResource, int featureTaglineStringResource) {
        this.imageResource = imageResource;
        this.featureDescriptionStringResource = featureDescriptionStringResource;
        this.featureTaglineStringResource = featureTaglineStringResource;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public int getFeatureDescriptionStringResource() {
        return featureDescriptionStringResource;
    }

    public void setFeatureDescriptionStringResource(int featureDescriptionStringResource) {
        this.featureDescriptionStringResource = featureDescriptionStringResource;
    }

    public int getFeatureTaglineStringResource() {
        return featureTaglineStringResource;
    }

    public void setFeatureTaglineStringResource(int featureTaglineStringResource) {
        this.featureTaglineStringResource = featureTaglineStringResource;
    }

    public static List<AppFeatures> getFeaturesList() {
        return Arrays.asList(AppFeatures.values());
    }
}
