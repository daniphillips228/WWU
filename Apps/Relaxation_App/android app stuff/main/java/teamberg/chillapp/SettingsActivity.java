package teamberg.chillapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;

public class SettingsActivity extends PreferenceActivity {

    public static final String KEY_PREF_MUSIC = "pref_music";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean continueMusic = sharedPref.getBoolean(SettingsActivity.KEY_PREF_MUSIC, true);

        if (!continueMusic) {
            MusicManager.pause();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean continueMusic = sharedPref.getBoolean(SettingsActivity.KEY_PREF_MUSIC, true);

        if (continueMusic) {
            MusicManager.start(this, MusicManager.MUSIC_BACKGROUND);
        }
    }



}
