package teamberg.chillapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_WAIT = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load saved preferences
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        // Start up music manager and check preferences
        Boolean playMusic = sharedPref.getBoolean(SettingsActivity.KEY_PREF_MUSIC, true);
        if (playMusic) {
            MusicManager.start(this, MusicManager.MUSIC_BACKGROUND);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_WAIT);
    }
}
