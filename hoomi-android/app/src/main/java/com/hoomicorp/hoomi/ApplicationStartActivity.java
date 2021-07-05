package com.hoomicorp.hoomi;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ApplicationStartActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_start);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.navigation_controller);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        //init profile image
        ImageView profileImageView = findViewById(R.id.account_bottom_nav_image);
        Glide.with(this).load("https://hoomi-images.s3.eu-central-1.amazonaws.com/events-images/pubg.jpg").into(profileImageView);
    }
}