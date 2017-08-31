package com.example.feedingindiaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class MyDonationsFragment extends Fragment {
    private static final String DONATION_LIST_URL = "https://feedingindiaapp.000webhostapp.com/getdata/mydonations.php";
    JSONObject jsonObject;
    JSONArray jsonArray;
    private RelativeLayout loadingLayout;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private ArrayList<Donation> listOfDonations = new ArrayList<>();
    private String result = "";
    private String donor_id="";
    public MyDonationsFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_my_donations_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recylerViewofmydonations);

        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_data", MODE_PRIVATE);
        donor_id = Integer.toString(sharedPreferences.getInt("user_id",0));
        loadingLayout = (RelativeLayout) view.findViewById(R.id.loading_layout);
        loadingLayout.setVisibility(View.VISIBLE);
        return view;

    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // Set layout manager to recyler view
        final LinearLayoutManager layoutManager = new LinearLayoutManager(MyDonationsFragment.this.getContext());
        recyclerView.setLayoutManager(layoutManager);


        // Create a new adapter
        adapter = new DonationAdapter(listOfDonations, MyDonationsFragment.this.getContext());
        // Assign adapter to recycler view
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {


            }

        });

        Bgtask bg = new Bgtask();
        bg.execute();

    }


 //                           progressBar.setVisibility(View.INVISIBLE);



    private class Bgtask extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {


            String fetch_url =  "https://feedingindiaapp.000webhostapp.com/getdata/mydonations.php";;
            try {


                URL url = new URL(fetch_url);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestMethod("POST");
                http.setDoInput(true);
                http.setDoOutput(true);
                OutputStream os = http.getOutputStream();
                BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                String postdata = URLEncoder.encode("donor_id", "UTF-8") + "=" + URLEncoder.encode(donor_id, "UTF-8");
                bf.write(postdata);
                bf.flush();
                bf.close();
                os.close();

                InputStream is = http.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "iso-8859-1"));
                String line = "";

                while ((line = br.readLine()) != null) {
                    result += line;

                }

                br.close();
                is.close();
                http.disconnect();
                Handler handler =  new Handler(MyDonationsFragment.this.getContext().getMainLooper());
                handler.post( new Runnable(){
                    public void run(){
                        Toast.makeText(MyDonationsFragment.this.getContext(), result, Toast.LENGTH_SHORT).show();
                    }
                });
                return result;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            loadingLayout.setVisibility(View.INVISIBLE);

            showlist();


        }
    }
    ;

    public void showlist() {


        try {
            jsonObject = new JSONObject(result);
            jsonArray = jsonObject.getJSONArray("server");
            int count = 0;


            while (count < jsonArray.length()) {
                JSONObject jo = jsonArray.getJSONObject(count);

                Donation item = new Donation(
                        jsonObject.getLong("donation_id"),
                        jsonObject.getString("pickup_area"),
                        Short.parseShort(jsonObject.getString("is_veg")),
                        Short.parseShort(jsonObject.getString("is_perishable")),
                        Short.parseShort(jsonObject.getString("is_accepted")),
                        Short.parseShort(jsonObject.getString("is_picked")),
                        jsonObject.getString("other_details"),
                        jsonObject.getString("photo_url"),
                        jsonObject.getString("name"),
                        jsonObject.getString("request_datetime"),
                        jsonObject.getString("pickup_datetime")
                );
                listOfDonations.add(item);
                count++;

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
