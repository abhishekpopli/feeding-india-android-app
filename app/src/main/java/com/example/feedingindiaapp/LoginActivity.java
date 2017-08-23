package com.example.feedingindiaapp;

import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity {

    private static final String USER_AUTH_URL = "https://feedingindiaapp.000webhostapp.com/getauth/register_login.php";

    private String userEmail;
    private String userPassword;
    private String userType;

    private Button newUserBtn;
    private Button formSubmitBtn;
    private TextInputEditText userEmailField;
    private TextInputEditText userPasswordField;
    private RelativeLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        formSubmitBtn = (Button) findViewById(R.id.login_form_submit_btn);
        userEmailField = (TextInputEditText) findViewById(R.id.login_form_email);
        userPasswordField = (TextInputEditText) findViewById(R.id.login_form_password);
        loadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);
        newUserBtn = (Button) findViewById(R.id.login_new_user_btn);


        formSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userEmail = userEmailField.getText().toString();
                userPassword = userPasswordField.getText().toString();

                // Enforces form validation, and sends network request if form is valid
                if (validateFields()) {
                    loadingLayout.setVisibility(View.VISIBLE);
                    sendAuthenticationRequest();
                } else {
                    Toast.makeText(LoginActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        newUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }


    /**
     * Handles click on radio button, and sets user type variable according to it
     *
     * @param view
     */
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


    /**
     * This method enforces for form validation
     * @return true is all required fields are entered, otherwise returns false
     */
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
                .add("action", "login")
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
                        Toast.makeText(LoginActivity.this, "Cannot connect to server", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(LoginActivity.this, "Didn't get correct response from server", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    handleResponse(response);
                }

            }
        });

    }

    /**
     * Handles correct response from server
     * @param response is the JSON string returned by server
     */
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

                    Toast.makeText(LoginActivity.this, responseMessage, Toast.LENGTH_SHORT).show();

                    if (responseCode == 0) {
                        // when is an error, and request isn't successfull

                        // Do nothing
                    } else if (responseCode == 1) {
                        // when request is successful

                        //Also store in shared preferences
                        SharedPreferences sharedPreferences = getSharedPreferences("app_data", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("user_email", userEmail);
                        editor.putString("user_password", userPassword);
                        editor.putString("user_type", userType);
                        editor.putBoolean("is_logged_in", true);

                        editor.apply();

                        // Route to Main Activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }


    }
}