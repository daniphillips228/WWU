package teamberg.chillapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private Button mBreatheButton;
    private Button mGameButton;
    private ImageButton mSettingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Start up the menu
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mSettingsButton = (ImageButton)findViewById(R.id.setting_button);
        mSettingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        mGameButton = (Button)findViewById(R.id.game_button);
        mGameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, GameActivity.class);
                startActivity(i);
            }
        });

        mBreatheButton = (Button)findViewById(R.id.breathe_button);
        mBreatheButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, BreatheActivity.class);
                startActivity(i);
            }
        });
    }//end onCreate

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MusicManager.release();
    }
}
