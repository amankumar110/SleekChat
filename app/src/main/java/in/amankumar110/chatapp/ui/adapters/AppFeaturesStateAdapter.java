package in.amankumar110.chatapp.ui.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import in.amankumar110.chatapp.utils.AppFeatures;
import in.amankumar110.chatapp.ui.welcome.fragments.FeatureFragment;
import in.amankumar110.chatapp.utils.ThemeManager;

public class AppFeaturesStateAdapter extends FragmentStateAdapter {

    private final List<AppFeatures> featuresList;
    private ThemeManager themeManager;

    public AppFeaturesStateAdapter(FragmentActivity activity) {
        super(activity);
        this.featuresList = AppFeatures.getFeaturesList();
        themeManager = ThemeManager.getInstance(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        AppFeatures feature = featuresList.get(position);

        return FeatureFragment.newInstance(
                feature.getImageResource(),
                feature.getFeatureDescriptionStringResource(),
                feature.getFeatureTaglineStringResource()
        );
    }

    @Override
    public int getItemCount() {
        return featuresList.size();
    }

}
