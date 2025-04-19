package in.amankumar110.chatapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ThemeManager {

    private static ThemeManager instance;
    private final SharedPreferences sharedPreferences;
    private final MutableLiveData<Boolean> themeLiveData;
    private static final String PREF_NAME = "chat_app+2";
    private static final String THEME_PREF_KEY = "theme_choice";

    private ThemeManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean(THEME_PREF_KEY, false);
        themeLiveData = new MutableLiveData<>(isDarkMode);
    }

    public static synchronized ThemeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ThemeManager(context.getApplicationContext());
        }
        return instance;
    }

    public LiveData<Boolean> getThemeChoiceLiveData() {
        return themeLiveData;
    }

    public boolean isDarkMode() {
        return Boolean.TRUE.equals(themeLiveData.getValue());
    }

    public void setDarkMode(boolean isDarkMode) {
        themeLiveData.setValue(isDarkMode);
    }
}
