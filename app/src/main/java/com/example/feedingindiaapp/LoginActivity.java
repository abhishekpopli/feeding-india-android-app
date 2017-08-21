package com.example.feedingindiaapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private String userEmail;
    private String userPassword;

    private Button formSubmitBtn;
    private EditText userEmailField;
    private EditText userPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        formSubmitBtn = (Button) findViewById(R.id.login_form_submit_btn);
        userEmailField = (EditText) findViewById(R.id.login_form_email);
        userPasswordField = (EditText) findViewById(R.id.login_form_password);

        formSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userEmail = userEmailField.getText().toString();
                userPassword = userPasswordField.getText().toString();

            }
        });

    }
}