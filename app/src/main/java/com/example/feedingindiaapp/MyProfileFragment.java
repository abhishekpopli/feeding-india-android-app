package com.example.feedingindiaapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.MODE_PRIVATE;

public class MyProfileFragment extends Fragment {

    // User details
    private String userName;
    private String userEmail;
    private int userID;
    private String userPhoneNo1;
    private String fullUserType;
    private String userImageUrl;

    // Views
    private TextView userNameView;
    private TextView userEmailView;
    private TextView userIDView;
    private TextView userPhoneNo1View;
    private TextView fullUserTypeView;
    private Button editButton;
    private Button signOutButton;
    private CircleImageView userImageView;

    private SharedPreferences sharedPreferences;



    public MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.my_profile, container, false);

        // Getting all views
        userNameView = (TextView) view.findViewById(R.id.profile_user_name);
        userEmailView = (TextView) view.findViewById(R.id.profile_user_email);
        userIDView = (TextView) view.findViewById(R.id.profile_user_id);
        userPhoneNo1View = (TextView) view.findViewById(R.id.profile_user_ph_1);
        fullUserTypeView = (TextView) view.findViewById(R.id.profile_user_full_type);
        editButton = (Button) view.findViewById(R.id.profile_edit_btn);
        signOutButton = (Button) view.findViewById(R.id.profile_sign_out_btn);
        userImageView = (CircleImageView) view.findViewById(R.id.profile_user_image);

        return view;

    }


    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // Get all user data
        getUserData();


        userNameView.setText(userName);
        userEmailView.setText(userEmail);
        userIDView.setText(String.valueOf(userID));
        userPhoneNo1View.setText(userPhoneNo1);
        fullUserTypeView.setText(fullUserType);

        userImageUrl = sharedPreferences.getString("user_profile_pic_url", "http://www.msudenver.edu/media/sampleassets/profile-placeholder.png");
        if (userImageUrl.equals("null")) {
            userImageUrl = "http://www.msudenver.edu/media/sampleassets/profile-placeholder.png";
        }
        Glide.with(this).load(userImageUrl).into(userImageView);


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyProfileFragment.this.getContext(), "You clicked on edit button", Toast.LENGTH_SHORT).show();
            }
        });


        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmSignout();
            }
        });

    }


    /**
     * This method is used to retrieve user data from shared prefernces
     */
    private void getUserData() {

        // Getting data from shared preferences
        sharedPreferences = getActivity().getSharedPreferences("app_data", MODE_PRIVATE);

        userName = sharedPreferences.getString("user_name", "Name not set");
        userEmail = sharedPreferences.getString("emailid", "Email not set");
        userID = sharedPreferences.getInt("user_id", 0);
        userPhoneNo1 = sharedPreferences.getString("phoneno", "Phone no not set");
//        userImageUrl = sharedPreferences.getString("user_profile_pic_url",null);

        String userTypeFromStorage = sharedPreferences.getString("user_type", null);
        String donorTypeFromStorage = sharedPreferences.getString("donor_type", null);

        if (userTypeFromStorage != null && userTypeFromStorage.equals("donor")) {

            if (donorTypeFromStorage != null && donorTypeFromStorage.equals("individual")) {

                fullUserType = "Donor - Individual";

            } else if (donorTypeFromStorage != null && donorTypeFromStorage.equals("non-individual")) {

                fullUserType = "Donor - Non Individual";

            } else {
                fullUserType = "Donor";
            }

        } else if (userTypeFromStorage != null && userTypeFromStorage.equals("volunteer")) {

            fullUserType = "Volunteer";

        } else {

            fullUserType = "User type not set";

        }

    }


    /**
     * This method shows a dialog box to confirm sign out
     */
    private void confirmSignout() {

        AlertDialog.Builder signOutDialog = new AlertDialog.Builder(MyProfileFragment.this.getContext());
        signOutDialog.setMessage("Do you want to sign out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signOut();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        signOutDialog.show();
    }

    /**
     * This method signs the user out
     */
    void signOut() {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("is_logged_in", false);
        editor.putInt("user_id", 0);
        editor.putString("user_name", null);
        editor.putString("user_password_hash", null);
        editor.putString("user_type", null);
        editor.putString("donor_type", null);
        editor.putString("phoneno", null);
        editor.putString("emailid", null);
        editor.putString("user_profile_pic_url", null);

        editor.apply();

        Intent intent = new Intent(MyProfileFragment.this.getContext(), LoginActivity.class);
        startActivity(intent);

        MyProfileFragment.this.getActivity().finish();
    }

}
