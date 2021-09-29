package bitcoin.collector.collection.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import bitcoin.collector.collection.models.user.User;
import com.google.gson.Gson;

public class AppPreference {

    private static SharedPreferences mPrefs;
    private static SharedPreferences.Editor mPrefsEditor;

    public static boolean isUserLoggedIn(Context ctx) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getBoolean("is_logged_in", false);
    }

    public static void setUserLoggedIn(Context ctx, Boolean value) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putBoolean("is_logged_in", value);
        mPrefsEditor.apply();
    }

    public static void setUserDetails(Context context, User user) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        mPrefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        mPrefsEditor.putString("userData", json);
        mPrefsEditor.apply();
    }

    public static User getUserDetails(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = mPrefs.getString("userData", "");
        return gson.fromJson(json, User.class);
    }

    public static void setFirebaseToken(Context ctx, String value) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.putString("firebaseToken", value);
        mPrefsEditor.apply();
    }

    public static String getFirebaseToken(Context ctx) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        return mPrefs.getString("firebaseToken", "");
    }


    public static void clearAllPreferences(Context ctx) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        mPrefsEditor = mPrefs.edit();
        mPrefsEditor.clear();
        mPrefsEditor.apply();
    }
}
