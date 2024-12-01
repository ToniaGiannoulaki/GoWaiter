package com.example.gowaiter.Waiter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageButton;

import com.example.gowaiter.R;

public class Waiter_Account_Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_account_settings);

        ImageButton backButton = findViewById(R.id.back_button_account_settings);
        backButton.setOnClickListener(v -> onBackPressed());
    }
}