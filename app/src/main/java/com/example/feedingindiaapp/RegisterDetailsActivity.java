package com.example.feedingindiaapp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
    //Request codes
    final private int MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE = 1;
    // Data members for image to be uploaded
    private final int LOAD_IMAGE_CAMERA = 0, CROP_IMAGE = 1, LOAD_IMAGE_GALLERY = 2;
    //Associated flags
    private boolean canWriteExternalStorage = false;
    private String cloudinaryPictureName;
    private File pic;
    private File croppedPic;
    private Uri picUri;
    private Uri croppedPicUri;

    // User data varibles
    // All user classification details
    private String userType; // for getting data from calling activity only
    private boolean isIndividual;
    private boolean isDonor;
    // Other user details
    private int userId;
    private String userEmail;
    private String userPassword;
    private String userName;
    private String userPhone1;
    private String userPhone2;
    private String userCity;
    private String userProfilePicUrl = "http://www.msudenver.edu/media/sampleassets/profile-placeholder.png"; // Default placeholder image

    //Flags
    private boolean clickedRadioButton;
    private boolean addedImage;

    // Views
    private ImageView userPicture;
    private Button changePictureButton;
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

        // Getting all views
        userPicture = (ImageView) findViewById(R.id.user_picture);
        changePictureButton = (Button) findViewById(R.id.change_picture_button);
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

        changePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPicture();
            }
        });
    }


    /**
     * This method runs data validation, and sends network request if data is valid
     */
    private void submitDetails() {

        userName = userNameField.getText().toString();
        userPhone1 = userPhone1Field.getText().toString();
        userPhone2 = userPhone2Field.getText().toString();
        userCity = userCityField.getText().toString();

        // Check if required fields are set, and if yes then send request
        if (validateFields()) {

            loadingLayout.setVisibility(View.VISIBLE);

            if (addedImage) {
                // sendUpdationRequest is called in this Aync task's post execute
                new uploadToCloudinary().execute();
            } else {
                sendUpdationRequest();
            }

        } else {
            Toast.makeText(RegisterDetailsActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * This method initialises the picture files and shows dialog to select from where picture is taken
     */
    private void getPicture() {

        // Check permission, and request permission
        checkExternalStorageWritePermission();

        if (canWriteExternalStorage) {

            final CharSequence[] options = {"Take picture", "Choose from picture from gallery"};

            // TODO: Change folder where picture/file is created
            pic = new File(Environment.getExternalStorageDirectory(),
                    "Donation" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            picUri = Uri.fromFile(pic);

            croppedPic = new File(Environment.getExternalStorageDirectory(),
                    "DonationCropped" + String.valueOf(System.currentTimeMillis()) + ".jpg");
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

        } else {
            Toast.makeText(this, "You haven't granted permission to store images on external storage", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * This methods sends the camera intent
     */
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
        cameraIntent.putExtra("return-data", true);

        startActivityForResult(cameraIntent, LOAD_IMAGE_CAMERA);
    }

    /**
     * This method sends the gallery intent
     */
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(Intent.createChooser(galleryIntent, "Select Image from gallery"), LOAD_IMAGE_GALLERY);
    }

    /**
     * This method sends sends crop image intent
     */
    private void cropImage() {
        try {
            Intent intent = new Intent("com.android.camera.action.CROP");

            intent.setDataAndType(picUri, "image/*");

            intent.putExtra("crop", "true");
            intent.putExtra("outputX", 250);
            intent.putExtra("outputY", 250);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scaleUpIfNeeded", true);
            intent.putExtra("return-data", true);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, croppedPicUri);

            startActivityForResult(intent, CROP_IMAGE);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Please install an app to crop images", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == LOAD_IMAGE_CAMERA && resultCode == RESULT_OK) {

            cropImage();

        } else if (requestCode == LOAD_IMAGE_GALLERY) {

            if (data != null) {
                picUri = data.getData();
                cropImage();
            }

        } else if (requestCode == CROP_IMAGE) {

            if (data != null) {
                Bundle bundle = data.getExtras();
                Bitmap photo = bundle.getParcelable("data");
                userPicture.setImageBitmap(photo);

                try {
                    FileOutputStream fos = new FileOutputStream(croppedPic);
                    photo.compress(Bitmap.CompressFormat.JPEG, 90, fos);
                    fos.close();
                    addedImage = true;
                } catch (FileNotFoundException e) {
                    System.out.println("File not found");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("Error accessing file");
                    e.printStackTrace();
                } catch (Exception e) {
                    System.out.println("Some error occured");
                    e.printStackTrace();
                }
            }

        }
    }


    /**
     * Method to handle radio button click to select donor type
     * Radio buttons are not visible to volunteer, but just adding extra validation
     *
     * @param view is the button out of radio group that is clicked
     */
    public void onRadioClick(View view) {
        if (isDonor) {

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
    }

    /**
     * This method runs validation on data that is entered
     * @return true id all data is correct, otherwise false
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

            // Check them is user is volunteer
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
     * This method sends the database updation request
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

    /**
     * This method handled response sent by server
     * @param response
     */
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

                            if (isDonor) {

                                if (isIndividual) {
                                    editor.putString("donor_type", "individual");
                                } else {
                                    editor.putString("donor_type", "non-individual");
                                }

                            } else {
                                editor.putString("donor_type", null);
                            }

                            editor.putBoolean("is_logged_in", true);
                            editor.putString("user_profile_pic_url", userProfilePicUrl);
                            editor.putString("phoneno", userPhone1);
                            editor.putString("emailid", userEmail);
                            editor.apply();

                            // Route to Main Activity
                            Intent intent = new Intent(RegisterDetailsActivity.this, MainActivity.class);
                            startActivity(intent);
                            // Ends activity, user cannt return to it by pressing back
                            finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        } catch (JSONException | IOException | NullPointerException e) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    loadingLayout.setVisibility(View.INVISIBLE);
                }
            });

            e.printStackTrace();
        }

    }

    /**
     * Method to check permission and to ask for it if not granted yet
     */
    private void checkExternalStorageWritePermission() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Toast.makeText(this, "Please grant storage permission to store clicked images", Toast.LENGTH_LONG).show();

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

            } else {

                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE);

            }

        } else {
            canWriteExternalStorage = true;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    canWriteExternalStorage = true;
                    getPicture();

                } else {

                    canWriteExternalStorage = false;
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }

    }

    /**
     * This Async task upload image onto cloudinary, and then sends database updation request
     */
    private class uploadToCloudinary extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            Map config = new HashMap();
            config.put("cloud_name", "feedingindiaapp");
            config.put("api_key", "721272957494713");
            config.put("api_secret", "4Mr4HHRpMZ0aKABIuNIsDI5AZvw");

            Cloudinary cloudinary = new Cloudinary(config);

            try {

                cloudinary.uploader().upload(croppedPic.getAbsolutePath(), ObjectUtils.asMap("public_id", cloudinaryPictureName));
                String[] htmlPicTag = cloudinary.url().imageTag(cloudinaryPictureName + ".jpg").split("'");

                // Extracts image url from
                userProfilePicUrl = htmlPicTag[1];

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            sendUpdationRequest();
        }
    }
}