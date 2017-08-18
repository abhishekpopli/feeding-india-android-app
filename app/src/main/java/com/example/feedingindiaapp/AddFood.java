package com.example.feedingindiaapp;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddFood extends AppCompatActivity implements  View.OnClickListener {

    private TextView latitude;
    private TextView longitude;
    private Spinner city;
    private EditText area;
    private EditText street;
    private EditText houseno;
    private ImageView upload_pic;
    private CheckBox isveg;
    private CheckBox isperishable;
    private EditText other_details;
    private FloatingActionButton submit_button;
    protected int LOAD_IMAGE_CAMERA = 0, CROP_IMAGE = 1, LOAD_IMAGE_GALLARY = 2;
    private Uri picUri;
    Button seemap;
    File pic;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        latitude = (TextView) findViewById(R.id.latitude_map);
        longitude = (TextView) findViewById(R.id.longitude_map);
        latitude.setVisibility(View.GONE);
        longitude.setVisibility(View.GONE);
        seemap =(Button) findViewById(R.id.map_button);
        area = (EditText) findViewById(R.id.area);
        street = (EditText) findViewById(R.id.street);
        houseno = (EditText) findViewById(R.id.houseno);
        city = (Spinner) findViewById(R.id.spinner_city);
        upload_pic = (ImageView) findViewById(R.id.upload_pic);
        isveg = (CheckBox) findViewById(R.id.is_veg);
        isperishable = (CheckBox) findViewById(R.id.is_perishable);
        other_details = (EditText) findViewById(R.id.other_details);


        submit_button = (FloatingActionButton) findViewById(R.id.submit_button);
        submit_button.setOnClickListener(this);
        seemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AddFood.this, Getlocation.class);
                startActivityForResult(i,10);
            }
        });
        upload_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] options = {"Take Photo", "Choose from Gallery"};

                AlertDialog.Builder builder = new AlertDialog.Builder(AddFood.this);
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



    public boolean validate() {
        boolean valid = true;

        String areaname = area.getText().toString();
        String house = houseno.getText().toString();
        String backgroundImageName = String.valueOf(upload_pic.getTag());
        if (areaname.isEmpty() || areaname.length() < 3) {
            area.setError("at least 3 characters");
            valid = false;
        } else {
            area.setError(null);
        }

        if (house.isEmpty() ) {
            houseno.setError("enter your house no.");
            valid = false;
        } else {
            houseno.setError(null);
        }
        if( (backgroundImageName.equals("uploadpic")))
        {
            valid = false;
            Toast.makeText(this, "Please upload atleast one pic of food !", Toast.LENGTH_SHORT).show();
        }


        return valid;
    }

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


                new uploadcloudinary().execute();
            }
        }
        else if (resultCode == Activity.RESULT_OK && data!=null)
        {
            String latitude_value = data.getStringExtra("latitude");
            String longitude_value = data.getStringExtra("longitude");
            latitude.setVisibility(View.VISIBLE);
            longitude.setVisibility(View.VISIBLE);
            latitude.setText(latitude_value);
            longitude.setText(longitude_value);
        }
    }
    public void TransferData()
    {

        Toast.makeText(this, "Happy!!", Toast.LENGTH_SHORT).show();
        Resources resources = this.getResources();
        String[] codes = resources.getStringArray(R.array.city_arrays);
        int pos = city.getSelectedItemPosition();
        String donorid;
        String city = codes[pos];
        String areaname = area.getText().toString();
        String streetname = street.getText().toString();
        String house = houseno.getText().toString();
        String picurl;
        Boolean veg = isveg.isChecked();
        //false if non-veg
        Boolean perishable = isperishable.isChecked();
        //true if persihable
        String otherdetails = other_details.getText().toString();
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

    @Override
    public void onClick(View v) {
        if(!validate())
        {
            return;

        }

        final ProgressDialog progressDialog = new ProgressDialog(AddFood.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Submiiting your donation");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete, adding food to database
                        Toast.makeText(AddFood.this, "Hello", Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                    }
                }, 2000);
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
                cloudinary.uploader().upload(pic.getAbsolutePath(), ObjectUtils.emptyMap());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    };

}



