package com.example.gowaiter.BaristaBarman;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.gowaiter.R;

public class Barista_Barman_Training_and_Support extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barista_barman_training_and_support);

        ImageButton backButton = findViewById(R.id.back_button_training_and_support_barista);
        backButton.setOnClickListener(v -> onBackPressed());

    }
}