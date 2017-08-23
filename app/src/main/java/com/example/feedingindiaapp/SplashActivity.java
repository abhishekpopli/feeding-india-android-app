package com.example.feedingindiaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            /**
             * This function is run after SPLASH_TIME_OUT(in millisecs)
             */
            @Override
            public void run() {

                SharedPreferences sharedPreferences = getSharedPreferences("app_data", MODE_PRIVATE);
                Boolean isLoggedIn = sharedPreferences.getBoolean("is_logged_in", false);

                if(isLoggedIn) {

                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {

                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();

                }

            }
        }, SPLASH_TIME_OUT);
    }
}
