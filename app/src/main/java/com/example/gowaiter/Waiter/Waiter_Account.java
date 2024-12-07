package com.example.gowaiter.Waiter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

import com.example.gowaiter.R;

public class Waiter_Account extends AppCompatActivity {

    CardView takeOrder, statistics, notifications, account_settings, payments, supplies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_account);

        takeOrder = findViewById(R.id.card_view_orders_waiter);
        statistics = findViewById(R.id.card_view_statistics_waiter);
        notifications = findViewById(R.id.card_view_order_notifications);
        account_settings = findViewById(R.id.card_view_account_settings_waiter);
        payments = findViewById(R.id.card_view_payments_waiter);
        supplies = findViewById(R.id.card_view_supplies_waiter);

        takeOrder.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Take_Order.class)));
        statistics.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Statistics.class)));
        notifications.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Notifications.class)));
        account_settings.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Account_Settings.class)));
        payments.setOnClickListener(v -> {startActivity(new Intent(Waiter_Account.this, Waiter_Payments.class));});
        supplies.setOnClickListener(v -> {startActivity(new Intent(Waiter_Account.this, Waiter_Supplies.class));});
    }
}