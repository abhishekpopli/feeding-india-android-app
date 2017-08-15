package com.example.feedingindiaapp;


import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class DonationDetailsImageAdapter extends PagerAdapter {

    private Context context;
    private String[] imageUrls = {"", ""};

    public DonationDetailsImageAdapter(Context context) {
        this.context = context;
    }

    public DonationDetailsImageAdapter(Context context, String[] imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }


    @Override
    public int getCount() {
        return 2;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.donation_detail_images, container, false);

        ImageView imageView = (ImageView) itemView.findViewById(R.id.view_pager_image_view);

        //TODO: Need to display placeholder image if the corresponding url is not set and sends null
        // Display placeholder image if network image isn't available
        Glide.with(context).load(imageUrls[position]).into(imageView);
        container.addView(itemView);

        return itemView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
