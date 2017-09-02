package com.example.feedingindiaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences sharedPreferences ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        sharedPreferences = getSharedPreferences("app_data", MODE_PRIVATE);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hview = navigationView.getHeaderView(0);

        CircleImageView tphoto = (CircleImageView) hview.findViewById(R.id.detail_donor_image);
        TextView tname = (TextView) hview.findViewById(R.id.header_name);
        TextView temail = (TextView) hview.findViewById(R.id.header_email);
        tname.setText(sharedPreferences.getString("user_name",null));
        temail.setText(sharedPreferences.getString("emailid",null));

        String userProfilePicUrl = sharedPreferences.getString("user_profile_pic_url", "http://www.msudenver.edu/media/sampleassets/profile-placeholder.png");
        if (userProfilePicUrl.equals("null")) {
            userProfilePicUrl = "http://www.msudenver.edu/media/sampleassets/profile-placeholder.png";
        }

        Glide.with(this).load(userProfilePicUrl).into(tphoto);

        navigationView.setNavigationItemSelectedListener(this);

        // Set the initial Fragment
        PendingDonationsFragment pendingDonationsFragment = new PendingDonationsFragment();
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.content_main_to_replace, pendingDonationsFragment, pendingDonationsFragment.getTag())
                .commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pending_donations) {

            PendingDonationsFragment pendingDonationsFragment = new PendingDonationsFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.content_main_to_replace, pendingDonationsFragment, pendingDonationsFragment.getTag())
                    .commit();

        } else if (id == R.id.nav_processed_donations) {

            ProcessedDonationsFragment processedDonationsFragment = new ProcessedDonationsFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.content_main_to_replace, processedDonationsFragment, processedDonationsFragment.getTag())
                    .commit();

        } else if (id == R.id.nav_my_donations) {

            MyDonationsFragment myDonationsFragment = new MyDonationsFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.content_main_to_replace, myDonationsFragment, myDonationsFragment.getTag())
                    .commit();


        } else if (id == R.id.nav_my_profile) {
            MyProfileFragment myprofile = new MyProfileFragment();
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.content_main_to_replace, myprofile, myprofile.getTag())
                    .commit();

        } else if (id == R.id.log_out) {

            //Change shared preferences

            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt("user_id", 0);
            editor.putString("user_name", null);
            editor.putString("user_password_hash", null);
            editor.putString("user_type", null);
            editor.putBoolean("is_logged_in", false);
            editor.putString("phoneno",null);
            editor.putString("emailid",null);

            editor.apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
