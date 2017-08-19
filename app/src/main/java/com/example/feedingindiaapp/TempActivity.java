package com.example.feedingindiaapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class TempActivity extends AppCompatActivity {

    private Button gotoLogin;
    private Button gotoMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        gotoLogin = (Button) findViewById(R.id.goto_login);
        gotoMainActivity = (Button) findViewById(R.id.goto_main);

        gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TempActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        gotoMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TempActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
