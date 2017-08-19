package com.example.feedingindiaapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DonationDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    // Server URL
    private static final String DONATION_DETAIL_URL = "https://feedingindiaapp.000webhostapp.com/getdata/donation_detail.php";
    // Adapter to show image slider for donation images
    DonationDetailsImageAdapter donationDetailsImageAdapter;
    // Variables to control authentication
    private Boolean isVolunteer = true;
    private Boolean isAuthenticatedDonor = false;
    // Variables to control program flow and te receive data from intent
    private Long donationId;
    private Boolean isPicked;
    private Boolean hasPickupGPS;
    // Object in which all fetched details are stored
    private Donation donation;

    // These variables are used to store additional data from request that aren't in Donation class
    private Boolean isIndividual;
    private String volunteerName;
    private String volunteerPhotoUrl;
    private String requestTime;
    private String pickupTime;
    private String pickupLocation = "";
    private String[] imageUrls = new String[2];

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
    private Button openMapsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_detail);

//         Puts the back button in AppBar
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        openMapsBtn = (Button) findViewById(R.id.detail_open_maps_btn);


        // Setting onClick listener on FAB
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DonationDetailActivity.this, "If you're a volunteer, use this btn to accept donation for pickup", Toast.LENGTH_SHORT).show();

            }
        });

        //Receive donation_id from calling activity
        donationId = getIntent().getLongExtra("donation_id", 1L);

        // Initially hide all views
        hideViews();

        //Fetch and display data
        connectToServer();
    }


    private void hideViews() {

        // Check required to make them visible
        confirmBtn.setVisibility(View.INVISIBLE);
        mapContainer.setVisibility(View.GONE);

        // No check required to make visible
        volunteerContainer.setVisibility(View.GONE);
        pickupContainer.setVisibility(View.GONE);
        otherDetailsContainer.setVisibility(View.GONE);
        scrollView.setVisibility(View.INVISIBLE);
    }

    private void connectToServer() {

        //Build URL
        HttpUrl.Builder urlBuilder = HttpUrl.parse(DONATION_DETAIL_URL).newBuilder();
        urlBuilder.addQueryParameter("donation_id", String.valueOf(donationId));
        String url = urlBuilder.build().toString();


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Using Asynchronous network call
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                // Need to put everything to display/change in UI inside runOnUiThread
                // Condition when we cannot connect to the internet
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(DonationDetailActivity.this, "Cannot connect to server", Toast.LENGTH_SHORT).show();
                    }
                });


            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (!response.isSuccessful()) {

                    // Condition when response code sent by the server says error
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(DonationDetailActivity.this, "Didn't get correct response from server", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {

                    // If load data from server is successful then display data
                    if (loadData(response)) {

                        // Display data when everything is correct
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                displayData();
                            }
                        });

                    }
                }

            }
        });

    }


    private Boolean loadData(Response response) {

        try {

            final String responseData = response.body().string();

            JSONArray array = new JSONArray(responseData);
            JSONObject object = array.getJSONObject(0);

            if (object.getString("is_picked").equals("1")) {
                isPicked = true;
            } else {
                isPicked = false;
            }

            if (object.getString("has_pickup_gps").equals("1")) {
                hasPickupGPS = true;
            } else {
                hasPickupGPS = false;
            }

            //If donation has been picked up, we also retrieve volunteer details
            if (isPicked) {

                if (hasPickupGPS) {

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
                            object.getString("donor_name"),
                            Short.parseShort(object.getString("has_pickup_gps")),
                            Double.parseDouble(object.getString("pickup_gps_latitude")),
                            Double.parseDouble(object.getString("pickup_gps_longitude"))
                    );

                } else {

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
                            object.getString("donor_name"),
                            Short.parseShort(object.getString("has_pickup_gps"))
                    );

                }

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

                if (hasPickupGPS) {

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
                            object.getString("donor_name"),
                            Short.parseShort(object.getString("has_pickup_gps")),
                            Double.parseDouble(object.getString("pickup_gps_latitude")),
                            Double.parseDouble(object.getString("pickup_gps_longitude"))
                    );
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
                            object.getString("donor_name"),
                            Short.parseShort(object.getString("has_pickup_gps"))
                    );

                }


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

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(DonationDetailActivity.this, "Cannot parse data from server correctly", Toast.LENGTH_SHORT).show();
                }
            });

            return false;
        }

        return true;
    }


    private void displayData() {

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

        // Need to authorize before making them visible
        if (isVolunteer == true || isAuthenticatedDonor == true) {

            pickupLocationView.setText(pickupLocation);

            if (hasPickupGPS == true) {

                mapContainer.setVisibility(View.VISIBLE);

                SupportMapFragment fm = (SupportMapFragment)
                        getSupportFragmentManager().findFragmentById(R.id.detail_map);

                // Getting GoogleMap object from the fragment
                fm.getMapAsync(this);

                openMapsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String geoURI = "geo:0,0?q=" + donation.getPickupGPSLatitude() + "," + donation.getPickupGPSLongitude() + "(Pickup Location)";

                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri
                                .parse(geoURI));

                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });
            }
        }

        if (isVolunteer == true) {
            confirmBtn.setVisibility(View.VISIBLE);
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


    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this);


        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions().position(new LatLng(donation.getPickupGPSLatitude(), donation.getPickupGPSLongitude())).title("Pickup Location"));

        CameraPosition cameraPosition = CameraPosition.builder().target(new LatLng(donation.getPickupGPSLatitude(), donation.getPickupGPSLongitude())).zoom(16).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}
