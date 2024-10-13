package com.example.gowaiter.Waiter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

import com.example.gowaiter.Admin.Admin_Account;
import com.example.gowaiter.Admin.Admin_Account_Settings;
import com.example.gowaiter.R;

public class Waiter_Account extends AppCompatActivity {

    CardView takeOrder, changeOrder, messages, account_settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_account);

        takeOrder = findViewById(R.id.card_view_take_order);
        changeOrder = findViewById(R.id.card_view_change_order);
        messages = findViewById(R.id.card_view_messages_waiter);
        account_settings = findViewById(R.id.card_view_account_settings_waiter);

        takeOrder.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Take_Order.class)));
        changeOrder.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Change_Order.class)));
        messages.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Messages.class)));
        account_settings.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Account_Settings.class)));
    }
}