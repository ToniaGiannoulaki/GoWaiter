package com.example.gowaiter.ChefCook;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

import com.example.gowaiter.R;

public class Chef_Cook_Statistics extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_cook_statistics);

        ImageButton backButton = findViewById(R.id.back_button_chef_cook_statistics);
        backButton.setOnClickListener(v -> onBackPressed());
    }
}