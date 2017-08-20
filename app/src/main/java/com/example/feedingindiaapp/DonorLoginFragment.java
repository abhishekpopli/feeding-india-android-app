package com.example.feedingindiaapp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DonorLoginFragment extends Fragment{

    private TextView greetingText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment_layout,container,false);

        greetingText = (TextView) view.findViewById(R.id.login_greeting);
        greetingText.setText("Welcome donors");

        return view;
    }
}
