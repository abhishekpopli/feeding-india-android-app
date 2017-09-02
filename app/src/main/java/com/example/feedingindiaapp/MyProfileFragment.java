package com.example.feedingindiaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class MyProfileFragment extends Fragment {

    private static final String DONATION_LIST_URL = "https://feedingindiaapp.000webhostapp.com/getdata/donations_list.php";
    int[] flags;
    private String[] options;
    private TextView textName;
    private TextView textEmail;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.my_profile_fragment, container, false);
        final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("app_data", MODE_PRIVATE);


        options = new String[]{sharedPreferences.getString("user_name", null), sharedPreferences.getString("phoneno", null),
                sharedPreferences.getString("user_type", null), "Log Out"};
        flags = new int[]{R.drawable.name, R.drawable.phone, R.drawable.donor, R.drawable.logout};


        // Getting all views
        textName = (TextView) view.findViewById(R.id.myprofilename);
        textEmail = (TextView) view.findViewById(R.id.myprofileemail);
        CircleImageView userProfileImage = (CircleImageView) view.findViewById(R.id.user_profile_image);

        // Setting data in views
        textName.setText(sharedPreferences.getString("user_name", null));
        textEmail.setText(sharedPreferences.getString("emailid", null));

        String userProfilePicUrl = sharedPreferences.getString("user_profile_pic_url", "http://www.msudenver.edu/media/sampleassets/profile-placeholder.png");
        if (userProfilePicUrl.equals("null")) {
            userProfilePicUrl = "http://www.msudenver.edu/media/sampleassets/profile-placeholder.png";
        }
        Glide.with(this).load(userProfilePicUrl).into(userProfileImage);


        List<HashMap<String, String>> aList = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            HashMap<String, String> hm = new HashMap<>();
            hm.put("cur", options[i]);
            hm.put("flag", Integer.toString(flags[i]));
            aList.add(hm);
        }

        String[] from = {"flag", "cur"};
        int[] to = {R.id.mylist_image, R.id.optionstext};
        ListView list = (ListView) view.findViewById(R.id.options_list);
        SimpleAdapter adapter = new SimpleAdapter(getActivity().getBaseContext(), aList, R.layout.listview_myprofile_style, from, to);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if (position == 3) {

                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putInt("user_id", 0);
                    editor.putString("user_name", null);
                    editor.putString("user_password_hash", null);
                    editor.putString("user_type", null);
                    editor.putBoolean("is_logged_in", false);
                    editor.putString("phoneno", null);
                    editor.putString("emailid", null);
                    editor.apply();

                    Intent intent = new Intent(MyProfileFragment.this.getContext(), LoginActivity.class);
                    startActivity(intent);
                }


            }
        });

        return view;

    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }


}
