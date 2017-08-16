package com.example.feedingindiaapp;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class PendingDonationsFragment extends Fragment {

    private static final String DONATION_LIST_URL = "https://feedingindiaapp.000webhostapp.com/getdata/donations_list.php?donation_id=";

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<Donation> listItemsPending = new ArrayList<>();

    private FloatingActionButton addDonationBtn;
    private ProgressBar progressBar;

    public PendingDonationsFragment() {
        // Required empty public constructor
    }


    /**
     * Only run findViewById calls in this method
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_donations_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recylerView);
        addDonationBtn = (FloatingActionButton) view.findViewById(R.id.add_fab_btn);
        progressBar = (ProgressBar) view.findViewById(R.id.list_progress_bar);

        return view;

    }


    /**
     * Run everything in this method except findViewById calls
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set onclick listener on FAB
        addDonationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(PendingDonationsFragment.this.getContext(),AddFood.class);
                startActivity(i);
            }
        });


        // Set layout manager to recyler view
        final LinearLayoutManager layoutManager = new LinearLayoutManager(PendingDonationsFragment.this.getContext());
        recyclerView.setLayoutManager(layoutManager);

        // Initially fetch data from server (no = limit specified in PHP file)
        loadDonationsFromServer(0);


        // Create a new adapter
        adapter = new DonationAdapter(listItemsPending, PendingDonationsFragment.this.getContext());
        // Assign adapter to recycler view
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                // Load more data when last item in arraylist is displayed on screen
                if (layoutManager.findLastCompletelyVisibleItemPosition() == listItemsPending.size() - 1) {
                    loadDonationsFromServer(listItemsPending.get(listItemsPending.size() - 1).getDonationId());
                }

            }

        });


    }

    private void loadDonationsFromServer(final long donation_id) {

        // Perform all network requests in Async tasks
        AsyncTask<Long, Void, Void> task = new AsyncTask<Long, Void, Void>() {

            @Override
            protected Void doInBackground(Long... params) {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(DONATION_LIST_URL + String.valueOf(donation_id))
                        .build();

                try {
                    // Send connection request
                    Response response = client.newCall(request).execute();

                    JSONArray array = new JSONArray(response.body().string());

                    for (int i = 0; i < array.length(); i++) {

                        JSONObject object = array.getJSONObject(i);

                        // All values from JSON objects are Strings, hence need to parse in appropriate type
                        Donation item = new Donation(
                                object.getLong("donation_id"),
                                object.getString("pickup_area"),
                                Short.parseShort(object.getString("is_veg")),
                                Short.parseShort(object.getString("is_perishable")),
                                Short.parseShort(object.getString("is_accepted")),
                                Short.parseShort(object.getString("is_picked")),
                                object.getString("other_details"),
                                object.getString("photo_url"),
                                object.getString("name"),
                                object.getString("request_datetime"),
                                object.getString("pickup_datetime")
                        );

                        listItemsPending.add(item);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    Log.e(PendingDonationsFragment.class.getSimpleName(), "JSON exception occured");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (donation_id == 0) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                // To tell adapter to supply added data items
                adapter.notifyDataSetChanged();
            }
        };

        // Execute the Async task
        task.execute(donation_id);
    }

}
