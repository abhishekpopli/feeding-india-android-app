package com.example.feedingindiaapp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PendingDonationsFragment extends Fragment {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<Donation> listItems = new ArrayList<>();

    private FloatingActionButton addDonationBtn;


    public PendingDonationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donations_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recylerView);
        addDonationBtn = (FloatingActionButton) view.findViewById(R.id.add_fab_btn);

        return view;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set layout manager to recyler view
        recyclerView.setLayoutManager(new LinearLayoutManager(PendingDonationsFragment.this.getContext()));


        listItems.add(new Donation("03:00 PM", "Rohini"));
        listItems.add(new Donation("04:00 PM", "Sarojini Nagar"));
        listItems.add(new Donation("05:30 PM", "Greater Kailash"));
        listItems.add(new Donation("01:21 PM", "Greenpark"));

        // Create a new adapter
        adapter = new DonationAdapter(listItems, PendingDonationsFragment.this.getContext());
        // Assign adapter to recycler view
        recyclerView.setAdapter(adapter);


        // Set onclick listener on FAB
        addDonationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PendingDonationsFragment.this.getContext(), "You clicked on add fab", Toast.LENGTH_SHORT).show();
            }
        });


    }

}
