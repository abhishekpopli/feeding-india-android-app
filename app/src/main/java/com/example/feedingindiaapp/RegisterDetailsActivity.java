package com.example.feedingindiaapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    private String userCity;
    private ImageView upload_pic;
    private TextInputEditText userNameField;
    private TextInputEditText userPhone1Field;
    private TextInputEditText userPhone2Field;
    private TextInputLayout userCityFieldContainer;
    private TextInputEditText userCityField;
    private LinearLayout donorTypeFieldContainer;
    private Button registerDetailsSubmitButton;
    private RelativeLayout loadingLayout;
    protected int LOAD_IMAGE_CAMERA = 0, CROP_IMAGE = 1, LOAD_IMAGE_GALLARY = 2;
    private Uri picUri;
    File pic;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_details);

        // Receive data from calling activity
        userEmail = getIntent().getStringExtra("email");
        userPassword = getIntent().getStringExtra("password");
        userType = getIntent().getStringExtra("user_type");
        upload_pic = (ImageView) findViewById(R.id.upload_yourpic);
        userNameField = (TextInputEditText) findViewById(R.id.register_name);
        userPhone1Field = (TextInputEditText) findViewById(R.id.register_phone_1);
        userPhone2Field = (TextInputEditText) findViewById(R.id.register_phone_2);
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
                userCity = userCityField.getText().toString();


                // Check if required fields are set, and if yes then send request
                if (validateFields()) {
                    loadingLayout.setVisibility(View.VISIBLE);

                    new uploadcloudinary().execute();
                    sendUpdationRequest();
                } else {
                    Toast.makeText(RegisterDetailsActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });



        upload_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterDetailsActivity.this);
                builder.setTitle("Select Pic Using...");
                builder.setItems(options, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {

                            try {
                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                                pic = new File(Environment.getExternalStorageDirectory(),
                                        "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

                                picUri = Uri.fromFile(pic);

                                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, picUri);

                                cameraIntent.putExtra("return-data", true);
                                startActivityForResult(cameraIntent, LOAD_IMAGE_CAMERA);
                            } catch (ActivityNotFoundException e) {
                                e.printStackTrace();
                            }

                        } else if (options[item].equals("Choose from Gallery")) {
                            Intent intent = new Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), LOAD_IMAGE_GALLARY);
                        }
                    }
                });

                builder.show();

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
    protected void CropImage() {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(picUri, "image/*");

            intent.putExtra("crop", "true");
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 3);
            intent.putExtra("aspectY", 4);
            intent.putExtra("scaleUpIfNeeded", true);
            intent.putExtra("return-data", true);
            pic = new File(Environment.getExternalStorageDirectory(),
                    "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg");

            picUri = Uri.fromFile(pic);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, picUri);

            intent.putExtra("return-data", true);

            startActivityForResult(intent, CROP_IMAGE);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Your device doesn't support the crop action!", Toast.LENGTH_SHORT).show();
        }
    }

    public class uploadcloudinary extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Map config = new HashMap();
            config.put("cloud_name", "feedingindiaapp");
            config.put("api_key", "721272957494713");
            config.put("api_secret", "4Mr4HHRpMZ0aKABIuNIsDI5AZvw");
            Cloudinary cloudinary = new Cloudinary(config);

            try {
                cloudinary.uploader().upload(pic.getAbsolutePath(), ObjectUtils.asMap("public_id","profile_pic_donor_id" +picUri.toString()));
                //cloudinary.uploader().upload(pic.getAbsolutePath(), ObjectUtils.asMap("public_id","user_name" +picUri.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOAD_IMAGE_CAMERA && resultCode == RESULT_OK) {
            CropImage();

        }
        else if (requestCode == LOAD_IMAGE_GALLARY) {
            if (data != null) {

                picUri = data.getData();
                CropImage();
            }
        }
        else if (requestCode == CROP_IMAGE) {
            if (data != null) {
                extras = data.getExtras();

                // get the cropped bitmap
                Bitmap photo = extras.getParcelable("data");
                upload_pic.setImageBitmap(photo);
                upload_pic.setTag("newpic");

            }
        }

    }


}