package com.example.feedingindiaapp;


import android.Manifest;
import android.Manifest.permission;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class Getlocation extends FragmentActivity implements LocationListener, OnMapReadyCallback {
    GoogleMap googleMap;
    LatLng myPosition;
    Location location;
    LocationManager locationManager;
    FloatingActionButton button;
    private String latitude_value = "";
    private String longitude_value = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getlocation);
        button = (FloatingActionButton) findViewById(R.id.submit_location);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ActivityCompat.checkSelfPermission(Getlocation.this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Getlocation.this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                double latitude ;
                double longitude;
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if(location!= null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                }
                else {
                    Toast.makeText(Getlocation.this, "Please select your location.", Toast.LENGTH_SHORT).show();
                    return;

                }

                latitude_value = Double.toString(latitude);
                longitude_value = Double.toString(longitude);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("latitude", latitude_value);
                resultIntent.putExtra("longitude", longitude_value);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });

        func();
    }


    public void func() {
        SupportMapFragment fm = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting GoogleMap object from the fragment
        fm.getMapAsync(this);

    }

    @Override
    public void onLocationChanged(Location location) {
//        double latitude = location.getLatitude();
//
//        // Getting longitude of the current location
//        double longitude = location.getLongitude();
//
//
//        latitude_value = Double.toString(latitude);
//        longitude_value = Double.toString(longitude);
        // Creating a LatLng object for the current location
//        LatLng latLng = new LatLng(latitude, longitude);
//
//        myPosition = new LatLng(latitude, longitude);

//        googleMap.addMarker(new MarkerOptions().position(myPosition).title("Start"));


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Enabling MyLocation Layer of Google Map
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        // Getting LocationManager object from System Service LOCATION_SERVICE
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();


        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling;
            return;
        }

        location = locationManager.getLastKnownLocation(provider);


    }

}
