package com.example.gowaiter.ChefCook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

import com.example.gowaiter.R;

public class Chef_Cook_Training_and_Support extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_cook_training_and_support);

        ImageButton backButton = findViewById(R.id.back_button_training_and_support_chef);
        backButton.setOnClickListener(v -> onBackPressed());
    }
}