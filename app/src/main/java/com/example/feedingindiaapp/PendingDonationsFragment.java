package com.example.feedingindiaapp;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class PendingDonationsFragment extends Fragment {

    private static final String DONATION_LIST_URL = "https://feedingindiaapp.000webhostapp.com/getdata/donations_list.php";

    private HttpUrl.Builder urlBuilder;
    private OkHttpClient client;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;

    private ArrayList<Donation> listItemsPending = new ArrayList<>();

    // Views
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
                Intent i = new Intent(PendingDonationsFragment.this.getContext(), AddFood.class);
                startActivity(i);
            }
        });


        // Set layout manager to recyler view
        final LinearLayoutManager layoutManager = new LinearLayoutManager(PendingDonationsFragment.this.getContext());
        recyclerView.setLayoutManager(layoutManager);


        // Initially fetch data from server (no = limit specified in PHP file)
        connectToServer(0);


        // Create a new adapter
        adapter = new DonationAdapter(listItemsPending, PendingDonationsFragment.this.getContext());

        // Assign adapter to recycler view
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

                // Load more data when last item in arraylist is displayed on screen
                if (layoutManager.findLastCompletelyVisibleItemPosition() == listItemsPending.size() - 1) {
                    connectToServer(listItemsPending.get(listItemsPending.size() - 1).getDonationId());
                }

            }

        });


    }


    private void connectToServer(final long donation_id) {

        //Build URL
        urlBuilder = HttpUrl.parse(DONATION_LIST_URL).newBuilder();
        urlBuilder.addQueryParameter("donation_id", String.valueOf(donation_id));
        String url = urlBuilder.build().toString();

        client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        //Using Asynchronous network call
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                // Need to put everything to display/change in UI inside runOnUiThread
                // Condition when we cannot connect to the internet
                if (PendingDonationsFragment.this.getActivity() != null) {

                    PendingDonationsFragment.this.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(PendingDonationsFragment.this.getContext(), "Cannot connect to server", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (!response.isSuccessful()) {

                    // Condition when response code sent by the server says error
                    if (PendingDonationsFragment.this.getActivity() != null) {

                        PendingDonationsFragment.this.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(PendingDonationsFragment.this.getContext(), "Didn't get correct response from server", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }


                } else {

                    // If load data from server is successful then display data
                    if (loadData(response)) {

                        // Display data when everything is correct
                        if (PendingDonationsFragment.this.getActivity() != null) {

                            PendingDonationsFragment.this.getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (donation_id == 0) {
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }

                                    // To tell adapter to supply added data items
                                    adapter.notifyDataSetChanged();

                                }
                            });

                        }


                    }
                }

            }
        });

    }

    private Boolean loadData(Response response) {

        try {

            final String responseData = response.body().string();

            JSONArray array = new JSONArray(responseData);

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

        } catch (IOException | JSONException | NullPointerException e) {

            e.printStackTrace();

            if (PendingDonationsFragment.this.getActivity() != null) {

                PendingDonationsFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(PendingDonationsFragment.this.getContext(), "Cannot parse data from server correctly", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            return false;

        }

        return true;
    }

}
