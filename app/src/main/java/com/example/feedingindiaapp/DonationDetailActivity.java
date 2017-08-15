package com.example.feedingindiaapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DonationDetailActivity extends AppCompatActivity {

    private static final String DONATION_DETAIL_URL = "http://bdfb4a13.ngrok.io/feeding-india-app-backend/getdata/donation_detail.php?donation_id=";
    DonationDetailsImageAdapter donationDetailsImageAdapter;
    private Long donationId;
    private Boolean isPicked;
    // These varibales are used to store additional data from request that aren't in Donaltion class
    private Boolean isIndividual;
    private String volunteerName;
    private String volunteerPhotoUrl;
    private String requestTime;
    private String pickupTime;
    private String[] imageUrls = new String[2];
    private Donation donation;
    // Variables for views
    private ProgressBar progressBar;
    private ViewPager viewPager;
    private CircleImageView donorImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_detail);

        // Puts the back button in AppBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.detail_progress_bar);
        donorImage = (CircleImageView) findViewById(R.id.donor_detail_image);

        //Receive donation_id from calling activity
        donationId = getIntent().getLongExtra("donation_id", Long.valueOf(1));

        //Fetched data and initialises donationDetailsAdapter
        loadDonationDataFromServer();
    }

    private void loadDonationDataFromServer() {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(DONATION_DETAIL_URL + String.valueOf(donationId))
                        .build();

                try {

                    Response response = client.newCall(request).execute();

                    JSONArray array = new JSONArray(response.body().string());
                    JSONObject object = array.getJSONObject(0);

                    if (object.getString("is_picked").equals("1")) {
                        isPicked = true;
                    } else {
                        isPicked = false;
                    }

                    //If donation has been picked up, we also retreive volunteer details
                    if (isPicked) {

                        donation = new Donation(
                                object.getLong("donation_id"),
                                object.getString("request_date"),
                                object.getString("pickup_date"),
                                object.getLong("donor_id"),
                                object.getLong("volunteer_id"),
                                object.getString("pickup_photo_url"),
                                object.getString("delivery_photo_url"),
                                object.getString("pickup_city"),
                                object.getString("pickup_area"),
                                object.getString("pickup_street"),
                                object.getString("pickup_house_no"),
                                Short.parseShort(object.getString("is_veg")),
                                Short.parseShort(object.getString("is_perishable")),
                                Short.parseShort(object.getString("is_accepted")),
                                Short.parseShort(object.getString("is_picked")),
                                Short.parseShort(object.getString("is_completed")),
                                object.getString("other_details"),
                                object.getString("donor_photo_url"),
                                object.getString("donor_name")
                        );

                        requestTime = object.getString("request_time");
                        pickupTime = object.getString("pickup_time");

                        if (Short.parseShort(object.getString("donor_type")) == 0) {
                            isIndividual = true;
                        } else {
                            isIndividual = false;
                        }

                        volunteerName = object.getString("volunteer_name");
                        volunteerPhotoUrl = object.getString("volunteer_photo_url");

                    } else {

                        donation = new Donation(
                                object.getLong("donation_id"),
                                object.getString("request_date"),
                                object.getString("pickup_date"),
                                object.getLong("donor_id"),
                                object.getString("pickup_photo_url"),
                                object.getString("delivery_photo_url"),
                                object.getString("pickup_city"),
                                object.getString("pickup_area"),
                                object.getString("pickup_street"),
                                object.getString("pickup_house_no"),
                                object.getString("other_details"),
                                Short.parseShort(object.getString("is_veg")),
                                Short.parseShort(object.getString("is_perishable")),
                                Short.parseShort(object.getString("is_accepted")),
                                Short.parseShort(object.getString("is_picked")),
                                Short.parseShort(object.getString("is_completed")),
                                object.getString("donor_photo_url"),
                                object.getString("donor_name")
                        );

                        requestTime = object.getString("request_time");
                        pickupTime = object.getString("pickup_time");

                        if (Short.parseShort(object.getString("donor_type")) == 0) {
                            isIndividual = true;
                        } else {
                            isIndividual = false;
                        }

                    }

                    imageUrls[0] = donation.getPickupPhotoUrl();
                    imageUrls[1] = donation.getDeliveryPhotoUrl();

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                //Dismisses progress bar after data is fetched
                progressBar.setVisibility(View.INVISIBLE);

                if (imageUrls == null) {
                    donationDetailsImageAdapter = new DonationDetailsImageAdapter(DonationDetailActivity.this);
                } else {
                    donationDetailsImageAdapter = new DonationDetailsImageAdapter(DonationDetailActivity.this, imageUrls);
                }

                viewPager = (ViewPager) findViewById(R.id.donation_detail_view_pager);
                viewPager.setAdapter(donationDetailsImageAdapter);


                //Display all views
                Glide.with(DonationDetailActivity.this).load(donation.getDonorPhotoUrl()).into(donorImage);
            }


        };

        //Execute the Async task
        task.execute();
    }
}
