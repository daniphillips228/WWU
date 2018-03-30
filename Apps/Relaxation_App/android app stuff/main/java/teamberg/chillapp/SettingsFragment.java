package teamberg.chillapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by epvoon on 2/28/2017.
 */

public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String THEME_TREE = "0";
    private static final String THEME_NONE = "1";
    public static final String KEY_PREF_MUSIC = "pref_music";
    public static final String KEY_PREF_THEME = "pref_theme";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Grab the context
        Context context = getContext();
        Activity activity = getActivity();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        // Check which pref is being changed
        if (key.equals(KEY_PREF_MUSIC)) {
            Boolean musicOn = sharedPref.getBoolean(SettingsActivity.KEY_PREF_MUSIC, true);
            // Check the pref value
            if (musicOn) {
                MusicManager.start(context, MusicManager.MUSIC_BACKGROUND);
            } else if (!musicOn) {
                MusicManager.pause();
            }
        } else if (key.equals(KEY_PREF_THEME)) {
            // Restart with new theme
            Intent mainIntent = new Intent(activity, MainActivity.class);
            activity.startActivity(mainIntent);
            activity.finish();

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
}
