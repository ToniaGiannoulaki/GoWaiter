package com.example.gowaiter.ChefCook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

import com.example.gowaiter.R;

public class Chef_Cook_Account_Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_cook_account_settings);

        ImageButton backButton = findViewById(R.id.back_button_account_settings);
        backButton.setOnClickListener(v -> onBackPressed());
    }
}