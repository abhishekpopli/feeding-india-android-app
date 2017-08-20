package com.example.feedingindiaapp;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager();

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager() {

        LoginPageAdapter loginPageAdapter = new LoginPageAdapter(getSupportFragmentManager());
        loginPageAdapter.addFragment(new DonorLoginFragment(), "Donor");
        loginPageAdapter.addFragment(new VolunteerLoginFragment(), "Volunteer");

        viewPager.setAdapter(loginPageAdapter);

    }
}