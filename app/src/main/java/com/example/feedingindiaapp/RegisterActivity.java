package com.example.feedingindiaapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String USER_AUTH_URL = "https://feedingindiaapp.000webhostapp.com/getauth/register_login.php";

    private String userEmail;
    private String userPassword;
    private String userType;

    private Button formSubmitBtn;
    private TextInputEditText userEmailField;
    private TextInputEditText userPasswordField;
    private RelativeLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEmailField = (TextInputEditText) findViewById(R.id.register_form_email);
        userPasswordField = (TextInputEditText) findViewById(R.id.register_form_password);
        loadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
        formSubmitBtn = (Button) findViewById(R.id.register_form_submit_btn);

        formSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userEmail = userEmailField.getText().toString();
                userPassword = userPasswordField.getText().toString();

                if (validateFields()) {
                    loadingLayout.setVisibility(View.VISIBLE);
                    sendAuthenticationRequest();
                } else {
                    Toast.makeText(RegisterActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void onRadioClick(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        if (checked) {
            switch (view.getId()) {
                case R.id.donor_radio_btn:
                    userType = "donor";
                    break;
                case R.id.volunteer_radio_btn:
                    userType = "volunteer";
                    break;
            }
        }
    }


    private boolean validateFields() {
        boolean isValid = true;

        if (userEmail.isEmpty()) {
            userEmailField.setError("Enter email address");
            isValid = false;
        } else {
            userEmailField.setError(null);
        }

        if (userPassword.isEmpty()) {
            userPasswordField.setError("Enter password");
            isValid = false;
        } else {
            userPasswordField.setError(null);
        }

        if (userType == null) {
            isValid = false;
        }

        return isValid;
    }


    private void sendAuthenticationRequest() {


        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("email", userEmail)
                .add("password", userPassword)
                .add("user_type", userType)
                .add("action", "register")
                .build();

        Request request = new Request.Builder()
                .url(USER_AUTH_URL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        loadingLayout.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterActivity.this, "Cannot connect to server", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            loadingLayout.setVisibility(View.INVISIBLE);
                            Toast.makeText(RegisterActivity.this, "Didn't get correct response from server", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    handleResponse(response);
                }

            }
        });

    }

    private void handleResponse(final Response response) {

        try {
            final String responseData = response.body().string();

            JSONObject object = new JSONObject(responseData);
            final int responseCode = object.getInt("response_code");
            final String responseMessage = object.getString("message");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    loadingLayout.setVisibility(View.INVISIBLE);

                    Toast.makeText(RegisterActivity.this, responseMessage, Toast.LENGTH_SHORT).show();

                    if (responseCode == 0) {
                        // Do nothing
                    } else if (responseCode == 1) {

                        Intent intent = new Intent(RegisterActivity.this, RegisterDetailsActivity.class);
                        intent.putExtra("email",userEmail);
                        intent.putExtra("password",userPassword);
                        intent.putExtra("user_type",userType);
                        startActivity(intent);
                    }
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }


    }
}