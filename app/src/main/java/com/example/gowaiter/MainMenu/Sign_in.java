package com.example.gowaiter.MainMenu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.gowaiter.Admin.Admin_Account;
import com.example.gowaiter.BaristaBarman.Barista_Barman_Account;
import com.example.gowaiter.BaristaBarman.Barista_Barman_Orders;
import com.example.gowaiter.ChefCook.Chef_Cook_Account;
import com.example.gowaiter.R;
import com.example.gowaiter.Waiter.Waiter_Account;
import com.example.gowaiter.Waiter.Waiter_selected_table_payment;
import com.example.gowaiter.Waiter.Waiter_selected_table_take_order;

public class Sign_in extends AppCompatActivity {
    Button sign_up, sign_in;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        sign_up = findViewById(R.id.button_sign_up);
        sign_in = findViewById(R.id.button_sign_in);

        sign_up.setOnClickListener(v -> startActivity(new Intent(Sign_in.this, Sign_up.class)));
        sign_in.setOnClickListener(v -> startActivity(new Intent(Sign_in.this, Chef_Cook_Account.class)));
    }
}