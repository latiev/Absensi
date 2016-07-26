package id.latiev.absensi.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;

import id.latiev.absensi.R;

public class SplashScreen extends AppCompatActivity {

    // set sharepreference
    private SharedPreferences preferences;
    public static String PREF_USER = "userPreference";
    public static String KEY_ID = "id";
    public static String KEY_USERNAME = "username";
    public static String KEY_FULLNAME = "fullname";

    // set variabel
    private static int SPLASH_TIME_OUT = 3000;

    // set display
    private TextView textViewAdaNggak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        textViewAdaNggak = (TextView) findViewById(R.id.tv_ass_ada_nggak);

        File file = new File("/data/data/id.latiev.absensi/shared_prefs/" + PREF_USER + ".xml");
        if (file.exists()){
            textViewAdaNggak.setText("ADA");
            preferences = getSharedPreferences(PREF_USER, MODE_PRIVATE);
            String username = preferences.getString(KEY_USERNAME, "");
            String id = preferences.getString(KEY_ID, "");
            String fullname = preferences.getString(KEY_FULLNAME, "");

            if (username.equalsIgnoreCase("") || id.equalsIgnoreCase("") || fullname.equalsIgnoreCase("")){
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
            } else {
                startActivity(new Intent(SplashScreen.this, MainActivity.class));
            }

        } else {
            textViewAdaNggak.setText("Tidak ADA");
            preferences = getSharedPreferences(PREF_USER, MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(KEY_USERNAME, "");
            editor.putString(KEY_ID, "");
            editor.putString(KEY_FULLNAME, "");
            editor.commit();
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
        }
    }

    private void timerRun(){
        Thread timerThread = new Thread(){
            public void run(){
                try {
                    sleep(SPLASH_TIME_OUT);
                } catch (InterruptedException e){
                    e.printStackTrace();
                } finally {
                    Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };
        timerThread.start();
    }

    private void cekPreference(){
        File fileUserPref = new File("/data/data/id.latiev.absensi/shared_prefs/" + PREF_USER + ".xml");
        File fileAbsenPref = new File("/data/data/id.latiev.absensi/shared_prefs/" + MainActivity.PREF_ABSENSI + ".xml");
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
