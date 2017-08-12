package com.example.feedingindiaapp;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

        final Donation donation = listItems.get(position);
        holder.listItemSubheading.setText(donation.getPickupArea());
        holder.listItemTime.setText(donation.getPickupTime());

        holder.listItemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You clicked" + donation.getPickupTime(), Toast.LENGTH_SHORT).show();
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
        public TextView listItemSubheading;
        public TextView listItemTime;
        public LinearLayout listItemLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            // View holder creates link to element in the view
            listItemSubheading = (TextView) itemView.findViewById(R.id.list_item_donation_area);
            listItemTime = (TextView) itemView.findViewById(R.id.list_item_time);
            listItemLayout = (LinearLayout) itemView.findViewById(R.id.list_item_layout);
        }
    }


}

