package teamberg.chillapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by epvoon on 2/28/2017.
 */

public class SettingsFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_PREF_MUSIC = "pref_music";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Grab the context
        Context context = getActivity();
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
