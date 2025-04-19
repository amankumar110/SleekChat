package in.amankumar110.chatapp.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;
import in.amankumar110.chatapp.R;

public class TokenStorageService {

    private final SharedPreferences sharedPreferences;
    private final Context appContext;

    @Inject
    public TokenStorageService(@ApplicationContext Context appContext,
                               SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.appContext = appContext;
    }

    public void storeToken(String token) {
        sharedPreferences.edit().putString(appContext.getString(R.string.TOKEN_KEY),token).apply();
    }

    public String getToken() {
        return sharedPreferences.getString(appContext.getString(R.string.TOKEN_KEY),null);
    }
}
