package com.example.gowaiter.Waiter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

import com.example.gowaiter.Admin.Admin_Account;
import com.example.gowaiter.Admin.Admin_Account_Settings;
import com.example.gowaiter.R;

public class Waiter_Account extends AppCompatActivity {

    CardView takeOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_account);

        takeOrder = findViewById(R.id.card_view_take_order);

        takeOrder.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Take_Order.class)));
    }
}