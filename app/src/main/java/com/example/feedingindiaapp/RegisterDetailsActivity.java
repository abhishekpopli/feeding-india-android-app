package com.example.feedingindiaapp;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
    private String cloudinaryPictureName;

    // Data members for image to be uploaded
    private int LOAD_IMAGE_CAMERA = 0, CROP_IMAGE = 1, LOAD_IMAGE_GALLERY = 2;
    private File pic;
    private File croppedPic;
    private Uri picUri;
    private Uri croppedPicUri;
    private Boolean addedImage;

    // User data varibales
    private int userId;
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
    private String userProfilePicUrl = "http://www.msudenver.edu/media/sampleassets/profile-placeholder.png";

    // Views
    private ImageView uploadPic;
    private TextInputEditText userNameField;
    private TextInputEditText userPhone1Field;
    private TextInputEditText userPhone2Field;
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
        userId = getIntent().getIntExtra("user_id", 0);

        cloudinaryPictureName = userType + "_" + userId;

        uploadPic = (ImageView) findViewById(R.id.upload_yourpic);
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
                submitDetails();
            }
        });

        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });
    }

    private void submitDetails() {
        userName = userNameField.getText().toString();
        userPhone1 = userPhone1Field.getText().toString();
        userPhone2 = userPhone2Field.getText().toString();
        userCity = userCityField.getText().toString();

        // Check if required fields are set, and if yes then send request
        if (validateFields()) {

            loadingLayout.setVisibility(View.VISIBLE);

            if (addedImage) {
//                System.out.println("Path: " + pic.getAbsolutePath());
                new uploadToCloudinary().execute();
            } else {
                sendUpdationRequest();
            }
        } else {
            Toast.makeText(RegisterDetailsActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }
    }

    private void getPicture() {
        final CharSequence[] options = {"Take picture", "Choose from picture from gallery"};

        pic = new File(Environment.getExternalStorageDirectory(),
                "FeedingIndia" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        picUri = Uri.fromFile(pic);

        croppedPic = new File(Environment.getExternalStorageDirectory(),
                "FeedingIndiaCropped" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        croppedPicUri = Uri.fromFile(pic);

        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterDetailsActivity.this);
        builder.setTitle("Select Picture using...");
        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals(options[0])) {
                    openCamera();
                } else if (options[item].equals(options[1])) {
                    openGallery();
                }
            }
        });

        builder.show();
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        pic = new File(Environment.getExternalStorageDirectory(),
//                "tmp_"+String.valueOf(System.currentTimeMillis()) + ".jpg");
//        picUri = Uri.fromFile(pic);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
//        cameraIntent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());
        cameraIntent.putExtra("return-data", true);

        startActivityForResult(cameraIntent, LOAD_IMAGE_CAMERA);
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(galleryIntent, "Select Image from gallery"), LOAD_IMAGE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LOAD_IMAGE_CAMERA && resultCode == RESULT_OK) {
            addedImage = true;
            cropImage();
        } else if (requestCode == LOAD_IMAGE_GALLERY) {
            addedImage = true;

            if (data != null) {
                picUri = data.getData();
                cropImage();
            }
        } else if (requestCode == CROP_IMAGE) {
            if (data != null) {
                Bundle bundle = data.getExtras();
                Bitmap photo = bundle.getParcelable("data");
                uploadPic.setImageBitmap(photo);

                try {
                    FileOutputStream fos = new FileOutputStream(croppedPic);
                    photo.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.close();

                } catch (FileNotFoundException e) {
                    System.out.println("File not found");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("Error accessing file");
                    e.printStackTrace();
                }
            }
        }
    }

    private void cropImage() {
        try {

//            croppedPic = new File(Environment.getExternalStorageDirectory(),
//                    "tmp_crop_"+String.valueOf(System.currentTimeMillis()) + ".jpg");
//            croppedPicUri = Uri.fromFile(pic);

            Intent intent = new Intent("com.android.camera.action.CROP");

            intent.setDataAndType(picUri, "image/*");

            intent.putExtra("crop", "true");
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scaleUpIfNeeded", true);
            intent.putExtra("return-data", true);
//            intent.putExtra("outputFormat",Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, croppedPicUri);

            startActivityForResult(intent, CROP_IMAGE);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Please install an app to crop images", Toast.LENGTH_SHORT).show();
        }
    }

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

        if (addedImage) {
            formBuilder.add("profile_pic_url", userProfilePicUrl);
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

            final JSONObject object = new JSONObject(responseData);
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

                        try {
                            final int user_id = object.getInt("id");
                            final String user_name = object.getString("name");
                            final String password_hash = object.getString("password_hash");

                            //Also store in shared preferences
                            SharedPreferences sharedPreferences = getSharedPreferences("app_data", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putInt("user_id", user_id);
                            editor.putString("user_name", user_name);
                            editor.putString("user_password_hash", password_hash);
                            editor.putString("user_type", userType);
                            editor.putBoolean("is_logged_in", true);
                            editor.putString("phoneno", userPhone1);
                            editor.putString("emailid", userEmail);
                            editor.apply();

                            // Route to Main Activity
                            Intent intent = new Intent(RegisterDetailsActivity.this, MainActivity.class);
                            startActivity(intent);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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

    private class uploadToCloudinary extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
//            Toast.makeText(RegisterDetailsActivity.this, "*Path is: " + pic.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            Map config = new HashMap();
            config.put("cloud_name", "feedingindiaapp");
            config.put("api_key", "721272957494713");
            config.put("api_secret", "4Mr4HHRpMZ0aKABIuNIsDI5AZvw");

            Cloudinary cloudinary = new Cloudinary(config);

            try {

//                Toast.makeText(RegisterDetailsActivity.this, "**Path is: " + pic.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                cloudinary.uploader().upload(croppedPic.getAbsolutePath(), ObjectUtils.asMap("public_id", cloudinaryPictureName));

                String[] htmlPicTag = cloudinary.url().imageTag(cloudinaryPictureName + ".jpg").split("'");
                userProfilePicUrl = htmlPicTag[1];
//                System.out.println("URL on cloudinary: " + userProfilePicUrl);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            Toast.makeText(RegisterDetailsActivity.this, "Image successfully uploaded and url is " + userProfilePicUrl, Toast.LENGTH_SHORT).show();
            sendUpdationRequest();
        }
    }
}