package com.example.feedingindiaapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

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

    private static final String DONATION_DETAIL_URL = "https://feedingindiaapp.000webhostapp.com/getdata/donation_detail.php?donation_id=";
    DonationDetailsImageAdapter donationDetailsImageAdapter;
    private Long donationId;
    private Boolean isPicked;
    // These varibales are used to store additional data from request that aren't in Donaltion class
    private Boolean isIndividual;
    private String volunteerName;
    private String volunteerPhotoUrl;
    private String requestTime;
    private String pickupTime;
    private String pickupLocation = "";
    private String[] imageUrls = new String[2];
    private Donation donation;

    // Variables for views
    private FloatingActionButton confirmBtn;
    private ProgressBar progressBar;
    private ViewPager viewPager;
    private CircleImageView donorImage;
    private TextView donorName;
    private TextView donorType;
    private LinearLayout volunteerContainer;
    private CircleImageView volunteerImage;
    private TextView volunteerNameView;
    private TextView volunteerId;
    private TextView donationID;
    private TextView donationStatus;
    private TextView shelfLife;
    private TextView isVegText;
    private View isVegIndicator;
    private TextView requestTimeView;
    private TextView requestDate;
    private TextView pickupTimeView;
    private TextView pickupDate;
    private LinearLayout pickupContainer;
    private TextView otherDetails;
    private ScrollView scrollView;
    private LinearLayout otherDetailsContainer;
    private LinearLayout mapContainer;
    private TextView pickupLocationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_detail);

        // Puts the back button in AppBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = (ProgressBar) findViewById(R.id.detail_progress_bar);
        donorImage = (CircleImageView) findViewById(R.id.detail_donor_image);
        confirmBtn = (FloatingActionButton) findViewById(R.id.confirm_fab_btn);
        viewPager = (ViewPager) findViewById(R.id.donation_detail_view_pager);
        donorName = (TextView) findViewById(R.id.detail_donor_name);
        donorType = (TextView) findViewById(R.id.detail_donor_type);
        volunteerContainer = (LinearLayout) findViewById(R.id.detail_volunteer_container);
        volunteerImage = (CircleImageView) findViewById(R.id.detail_volunteer_image);
        volunteerNameView = (TextView) findViewById(R.id.detail_volunteer_name);
        volunteerId = (TextView) findViewById(R.id.detail_volunteer_id);
        donationID = (TextView) findViewById(R.id.detail_donation_id);
        donationStatus = (TextView) findViewById(R.id.detail_donation_status);
        shelfLife = (TextView) findViewById(R.id.detail_shelf_life);
        isVegText = (TextView) findViewById(R.id.detail_is_veg_text);
        isVegIndicator = findViewById(R.id.detail_is_veg_indicator);
        requestTimeView = (TextView) findViewById(R.id.detail_request_time);
        requestDate = (TextView) findViewById(R.id.detail_request_date);
        pickupContainer = (LinearLayout) findViewById(R.id.detail_pickup_container);
        pickupDate = (TextView) findViewById(R.id.detail_pickup_date);
        pickupTimeView = (TextView) findViewById(R.id.detail_pickup_time);
        otherDetails = (TextView) findViewById(R.id.detail_other_details);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        otherDetailsContainer = (LinearLayout) findViewById(R.id.other_details_container);
        mapContainer = (LinearLayout) findViewById(R.id.detail_map_container);
        pickupLocationView = (TextView) findViewById(R.id.detail_location);


        //Receive donation_id from calling activity
        donationId = getIntent().getLongExtra("donation_id", Long.valueOf(1));

        //Fetched data and initialises donationDetailsAdapter
        loadDonationDataFromServer();
    }

    private void loadDonationDataFromServer() {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                confirmBtn.setVisibility(View.INVISIBLE);
                volunteerContainer.setVisibility(View.GONE);
                pickupContainer.setVisibility(View.GONE);
                otherDetailsContainer.setVisibility(View.GONE);
                mapContainer.setVisibility(View.GONE);
                scrollView.setVisibility(View.INVISIBLE);

            }

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

                afterDataLoad();
            }


        };

        //Execute the Async task
        task.execute();
    }

    private void afterDataLoad() {

        // For displaying images in slider
        if (imageUrls == null) {
            donationDetailsImageAdapter = new DonationDetailsImageAdapter(DonationDetailActivity.this);
        } else {
            donationDetailsImageAdapter = new DonationDetailsImageAdapter(DonationDetailActivity.this, imageUrls);
        }
        viewPager.setAdapter(donationDetailsImageAdapter);


        ////////////////// Populating different views ///////////////////////

        /////// Setting views for both picked up and non picked up donations//////


        donorName.setText(donation.getDonorName());
        donationID.setText("#" + donation.getDonationId());
        requestTimeView.setText(requestTime);
        requestDate.setText(donation.getRequestDateTime());


        // Conditional setting of views
        if (!donation.getOtherDetails().equals("null")) {
            otherDetailsContainer.setVisibility(View.VISIBLE);
            otherDetails.setText(donation.getOtherDetails());
        }

        if (!donation.getPickupHouseNo().equals("null")) {
            pickupLocation += donation.getPickupHouseNo();
        }
        if (!donation.getPickupStreet().equals("null")) {
            pickupLocation += ", ";
            pickupLocation += donation.getPickupStreet();
        }
        if (!donation.getPickupArea().equals("null")) {
            pickupLocation += ", ";
            pickupLocation += donation.getPickupArea();
        }
        if (!donation.getPickupCity().equals("null")) {
            pickupLocation += ", ";
            pickupLocation += donation.getPickupCity();
        }

        pickupLocationView.setText(pickupLocation);


        if (isIndividual) {
            donorType.setText("Individual");
        } else {
            donorType.setText("Restaurant");
        }


        if (donation.isAccepted() == 1) {

            if (donation.isPicked() == 1) {

                if (donation.isCompleted() == 1) {

                    donationStatus.setText("Completed");
                    donationStatus.setBackgroundResource(R.drawable.green_tag);

                } else {
                    donationStatus.setText("Picked");
                    donationStatus.setBackgroundResource(R.drawable.green_tag);
                }

            } else {
                donationStatus.setText("Accepted");
                donationStatus.setBackgroundResource(R.drawable.blue_tag);
            }
        } else {
            donationStatus.setText("Not Accepted");
            donationStatus.setBackgroundResource(R.drawable.blue_tag);
        }


        if (donation.isPerishable() == 1) {
            shelfLife.setText("Perishable");
            shelfLife.setBackgroundResource(R.drawable.orange_tag);
        } else {
            shelfLife.setText("Non-Perishable");
            shelfLife.setBackgroundResource(R.drawable.indigo_tag);
        }


        if (donation.isVeg() == 1) {
            isVegIndicator.setBackgroundResource(R.drawable.green_circle_shape);
            isVegText.setText("Veg");
        } else {
            isVegIndicator.setBackgroundResource(R.drawable.red_circle_shape);
            isVegText.setText("Non-Veg");
        }


        if (donation.getDonorPhotoUrl() != null) {
            Glide.with(DonationDetailActivity.this).load(donation.getDonorPhotoUrl()).into(donorImage);
        }


        /////// Setting views for picked up donations//////

        if (isPicked) {
            // Making volunteer section visible and setting the views
            volunteerContainer.setVisibility(View.VISIBLE);

            if (volunteerPhotoUrl != null) {
                Glide.with(DonationDetailActivity.this).load(volunteerPhotoUrl).into(volunteerImage);
            }

            if (volunteerName != null) {
                volunteerNameView.setText(volunteerName);
            }

            // Making the pickup section visible and setting the views
            pickupContainer.setVisibility(View.VISIBLE);

            if (pickupTime != null) {
                pickupTimeView.setText(pickupTime);
            }

            if (donation.getPickupDateTime() != null) {
                pickupDate.setText(donation.getPickupDateTime());
            }

        }


        // After all views have been set, finally hiding the progress bar and making the scroll view vsisible
        scrollView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
