package com.example.feedingindiaapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;


public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;
    ImageView img;
    private sessionmanager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                session = new sessionmanager(getApplicationContext());

                if (session.isLoggedIn()) {
                    // User is already logged in. Take him to main activity
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
