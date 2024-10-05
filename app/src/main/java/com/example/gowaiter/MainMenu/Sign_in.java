package com.example.gowaiter.MainMenu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.gowaiter.Admin.Admin_Account;
import com.example.gowaiter.R;
import com.example.gowaiter.Waiter.Waiter_Account;
import com.example.gowaiter.Waiter.Waiter_selected_table;

public class Sign_in extends AppCompatActivity {
    ImageButton sign_up, sign_in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        sign_up = findViewById(R.id.imageButton_sign_up);
        sign_in = findViewById(R.id.imageButton_sign_in);

        sign_up.setOnClickListener(v -> startActivity(new Intent(Sign_in.this, Sign_up.class)));
        sign_in.setOnClickListener(v -> startActivity(new Intent(Sign_in.this, Waiter_selected_table.class)));
    }
}