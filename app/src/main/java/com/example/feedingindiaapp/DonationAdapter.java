package com.example.feedingindiaapp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    public void onBindViewHolder(final ViewHolder holder, int position) {

        // Get that particular donation
        final Donation donation = listItems.get(position);

        //Populate list view with data from server
        holder.donorName.setText(donation.getDonorName());
        holder.pickupArea.setText(donation.getPickupArea());

        if ((donation.isCompleted() == 1) || (donation.isPicked() == 1)) {
            holder.itemTime.setText(donation.getPickupDateTime());
        } else {
            holder.itemTime.setText(donation.getRequestDateTime());
        }


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
            holder.isPerishable.setText("Perishable");
            holder.isPerishable.setBackgroundResource(R.drawable.orange_tag);
        } else {
            holder.isPerishable.setText("Non-Perishable");
            holder.isPerishable.setBackgroundResource(R.drawable.indigo_tag);
        }

        if (donation.isAccepted() == 1) {

            if (donation.isPicked() == 1) {

                if (donation.isCompleted() == 1) {

                    holder.status.setText("Completed");
                    holder.status.setBackgroundResource(R.drawable.green_tag);

                } else {
                    holder.status.setText("Picked");
                    holder.status.setBackgroundResource(R.drawable.green_tag);
                }

            } else {
                holder.status.setText("Accepted");
                holder.status.setBackgroundResource(R.drawable.blue_tag);
            }
        } else {
            holder.status.setText("Not Accepted");
            holder.status.setBackgroundResource(R.drawable.blue_tag);
        }

        Glide.with(context).load(donation.getDonorPhotoUrl()).into(holder.donorImage);

        //TODO: Add intent to take to donation's activity
        holder.listItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DonationDetailActivity.class);

                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) context, holder.donorImage, "donorImage");
                intent.putExtra("donation_id", donation.getDonationId());

                context.startActivity(intent, optionsCompat.toBundle());
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
        public TextView itemTime;
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
            itemTime = (TextView) itemView.findViewById(R.id.list_item_time);
            foodDetails = (TextView) itemView.findViewById(R.id.list_item_food_detail);
            status = (TextView) itemView.findViewById(R.id.list_item_donation_status);
            listItemLayout = (LinearLayout) itemView.findViewById(R.id.list_item_layout);
            isPerishable = (TextView) itemView.findViewById(R.id.list_item_perishable);
        }
    }


}

