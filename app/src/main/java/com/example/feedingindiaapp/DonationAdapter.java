package com.example.feedingindiaapp;


import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

public class DonationAdapter extends RecyclerView.Adapter<DonationAdapter.ViewHolder> {

    private List<Donation> listItems;
    private Context context;

    // Needs to receive context along with all data
    public DonationAdapter(List<Donation> listItems, Context context) {

        this.listItems = listItems;
        this.context = context;
    }

    // Method 1 to implement
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // Creates a view and passes it view holder to wrap around it
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.donation_list_item, parent, false);
        // Return this viewholder wrapped around(view)
        return new ViewHolder(v);
    }

    // Method 2 to implement
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Get that particular donation
        final Donation donation = listItems.get(position);

        //Populate list view with data from server
        holder.donorName.setText(donation.getDonorName());
        holder.pickupArea.setText(donation.getPickupArea());
        holder.pickupTime.setText(donation.getRequestDateTime());


        if (donation.getOtherDetails().equals("null")) {
            holder.foodDetails.setVisibility(View.GONE);
        } else {
            holder.foodDetails.setText(donation.getOtherDetails());
        }

        if (donation.isVeg() == 1) {
            holder.isVeg.setBackgroundResource(R.drawable.green_circle_shape);
        } else {
            holder.isVeg.setBackgroundResource(R.drawable.red_circle_shape);
        }

        if (donation.isPerishable() == 1) {
            holder.listItemCardView.setCardBackgroundColor(Color.parseColor("#fff9c4"));
        } else {
            holder.listItemCardView.setCardBackgroundColor(Color.parseColor("#e0f7fa"));
        }

        if (donation.isAccepted() == 1) {
            holder.status.setText("Accepted");
            holder.status.setBackgroundResource(R.drawable.blue_tag);
        } else {
            holder.status.setText("Not Accepted");
            holder.status.setBackgroundResource(R.drawable.blue_tag);
        }

        Glide.with(context).load(donation.getDonorPhotoUrl()).into(holder.donorImage);

        //TODO: Add intent to take to donation's activity
        holder.listItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method 3 to implement
    @Override
    public int getItemCount() {
        return listItems.size();
    }

    // Contained class, need this to pass this to generic class
    public class ViewHolder extends RecyclerView.ViewHolder {

        // Need to declare these as public, as we refer them in bindHolder function using . notation
        public TextView donorName;
        public ImageView donorImage;
        public TextView pickupArea;
        public View isVeg;
        public TextView isPerishable;
        public TextView pickupTime;
        public TextView foodDetails;
        public TextView status;
        public LinearLayout listItemLayout;
        public CardView listItemCardView;

        public ViewHolder(View itemView) {
            super(itemView);

            // View holder creates links to elements in the view
            donorName = (TextView) itemView.findViewById(R.id.list_item_donor_name);
            donorImage = (ImageView) itemView.findViewById(R.id.list_item_image);
            pickupArea = (TextView) itemView.findViewById(R.id.list_item_donation_area);
            isVeg = itemView.findViewById(R.id.list_item_is_veg);
            pickupTime = (TextView) itemView.findViewById(R.id.list_item_time);
            foodDetails = (TextView) itemView.findViewById(R.id.list_item_food_detail);
            status = (TextView) itemView.findViewById(R.id.list_item_donation_status);
            listItemLayout = (LinearLayout) itemView.findViewById(R.id.list_item_layout);
            listItemCardView = (CardView) itemView.findViewById(R.id.list_item_card_view);
        }
    }


}

