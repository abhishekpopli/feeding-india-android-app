package com.example.feedingindiaapp;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class LoginPageAdapter extends FragmentPagerAdapter{

    private List<Fragment> mFragmentList = new ArrayList<>();
    private List<String> mFragementListTitle = new ArrayList<>();

    public LoginPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragementListTitle.get(position);
    }

    public void addFragment(Fragment fragment, String fragmentTitle) {
        mFragmentList.add(fragment);
        mFragementListTitle.add(fragmentTitle);
    }
}
