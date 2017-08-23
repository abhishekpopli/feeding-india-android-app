package com.example.feedingindiaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class RegisterDetailsActivity extends AppCompatActivity {

    private static final String USER_REGISTER_URL = "https://feedingindiaapp.000webhostapp.com/getauth/register_details.php";

    private String userEmail;
    private String userPassword;
    private String userType;
    private boolean isIndividual;
    private boolean isDonor;
    private boolean clickedRadioButton;

    private String userName;
    private String userPhone1;
    private String userPhone2;
    private String userProfilePicURL;
    private String userCity;

    private TextInputEditText userNameField;
    private TextInputEditText userPhone1Field;
    private TextInputEditText userPhone2Field;
    private TextInputEditText userProfilePicURLField;
    private TextInputLayout userCityFieldContainer;
    private TextInputEditText userCityField;
    private LinearLayout donorTypeFieldContainer;
    private Button registerDetailsSubmitButton;
    private RelativeLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_details);

        // Receive data from calling activity
        userEmail = getIntent().getStringExtra("email");
        userPassword = getIntent().getStringExtra("password");
        userType = getIntent().getStringExtra("user_type");

        userNameField = (TextInputEditText) findViewById(R.id.register_name);
        userPhone1Field = (TextInputEditText) findViewById(R.id.register_phone_1);
        userPhone2Field = (TextInputEditText) findViewById(R.id.register_phone_2);
        userProfilePicURLField = (TextInputEditText) findViewById(R.id.register_profile_pic);
        userCityFieldContainer = (TextInputLayout) findViewById(R.id.register_city_container);
        userCityField = (TextInputEditText) findViewById(R.id.register_city);
        donorTypeFieldContainer = (LinearLayout) findViewById(R.id.register_donor_type_container);
        registerDetailsSubmitButton = (Button) findViewById(R.id.register_details_form_submit_btn);
        loadingLayout = (RelativeLayout) findViewById(R.id.loading_layout);


        // Set user type flag
        if (userType.equals("donor")) {
            isDonor = true;
        } else if (userType.equals("volunteer")) {
            isDonor = false;
        }

        // display views that aren't required for the particular donor type
        if (isDonor) {
            userCityFieldContainer.setVisibility(View.GONE);
        } else {
            donorTypeFieldContainer.setVisibility(View.GONE);
        }


        registerDetailsSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userName = userNameField.getText().toString();
                userPhone1 = userPhone1Field.getText().toString();
                userPhone2 = userPhone2Field.getText().toString();
                userProfilePicURL = userProfilePicURLField.getText().toString();
                userCity = userCityField.getText().toString();


                // Check if required fields are set, and if yes then send request
                if (validateFields()) {
                    loadingLayout.setVisibility(View.VISIBLE);
                    sendUpdationRequest();
                } else {
                    Toast.makeText(RegisterDetailsActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    // Handle click on radio buttons, and change donor_type variable
    public void onRadioClick(View view) {
        clickedRadioButton = true;

        boolean checked = ((RadioButton) view).isChecked();

        if (checked) {
            switch (view.getId()) {
                case R.id.individual_radio_btn:
                    isIndividual = true;
                    break;
                case R.id.non_individual_radio_btn:
                    isIndividual = false;
                    break;
            }
        }
    }

    /**
     * This method does form validation
     *
     * @return true is all fields are valid, returns false otherwise
     */
    private boolean validateFields() {
        boolean isValid = true;

        if (userName.isEmpty()) {
            userNameField.setError("Enter name");
            isValid = false;
        } else {
            userNameField.setError(null);
        }

        if (userPhone1.isEmpty()) {
            userPhone1Field.setError("Enter phone no.");
            isValid = false;
        } else {
            userPhone1Field.setError(null);
        }


        if (isDonor) {

            if (!clickedRadioButton) {
                isValid = false;
            }

        } else {

            if (userCity.isEmpty()) {
                userCityField.setError("Enter city");
                isValid = false;
            } else {
                userCityField.setError(null);
            }

        }

        return isValid;
    }

    /**
     * This method sends network request, to update corresponding record
     */
    private void sendUpdationRequest() {

        OkHttpClient client = new OkHttpClient();

        // Add all the common fields between diff. user_types initially
        FormBody.Builder formBuilder = new FormBody.Builder()
                .add("email", userEmail)
                .add("password", userPassword)
                .add("name", userName)
                .add("phone_no_1", userPhone1);


        // Conditionally adding attributes to form
        if (userPhone2 != null) {
            formBuilder.add("phone_no_2", userPhone2);
        }

        if (userProfilePicURL != null) {
            formBuilder.add("profile_pic_url", userProfilePicURL);
        }

        if (isDonor) {

            formBuilder.add("user_type", "donor");

            if (isIndividual) {
                formBuilder.add("donor_type", "individual");
            } else {
                formBuilder.add("donor_type", "non_individual");
            }


        } else {

            formBuilder.add("user_type", "volunteer");
            formBuilder.add("city", userCity);
        }


        // Finally build the request body from form body
        RequestBody formBody = formBuilder.build();


        Request request = new Request.Builder()
                .url(USER_REGISTER_URL)
                .post(formBody)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        loadingLayout.setVisibility(View.INVISIBLE);
                        Toast.makeText(RegisterDetailsActivity.this, "Cannot connect to server", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RegisterDetailsActivity.this, "Didn't get correct response from server", Toast.LENGTH_SHORT).show();
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

                    Toast.makeText(RegisterDetailsActivity.this, responseMessage, Toast.LENGTH_SHORT).show();

                    if (responseCode == 0) {
                        // When there is an error

                        // Do nothing, error Toast message is previously displayed
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
                        Intent intent = new Intent(RegisterDetailsActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
        } catch (JSONException | IOException e) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingLayout.setVisibility(View.INVISIBLE);
                }
            });

            e.printStackTrace();
        }

    }
}
