package com.example.gowaiter.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

import com.example.gowaiter.R;

public class Admin_Account extends AppCompatActivity {

    CardView account_settings, staff_settings, enterprise_settings, statistics, payments, supplies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_account);

        account_settings = findViewById(R.id.card_view_account_settings_admin);
        staff_settings = findViewById(R.id.card_view_staff_settings);
        enterprise_settings = findViewById(R.id.card_view_enterprise_settings);
        statistics = findViewById(R.id.card_view_statistics);
        payments = findViewById(R.id.card_view_payments);
        supplies = findViewById(R.id.card_view_supplies);

        account_settings.setOnClickListener(v -> startActivity(new Intent(Admin_Account.this, Admin_Account_Settings.class)));
        staff_settings.setOnClickListener(v -> startActivity(new Intent(Admin_Account.this, Admin_Staff_Settings.class)));
        enterprise_settings.setOnClickListener(v -> startActivity(new Intent(Admin_Account.this, Admin_Enterprise_Settings.class)));
        statistics.setOnClickListener(v -> startActivity(new Intent(Admin_Account.this, Admin_Statistics.class)));
        payments.setOnClickListener(v -> startActivity(new Intent(Admin_Account.this, Admin_Payments.class)));
        supplies.setOnClickListener(v -> startActivity(new Intent(Admin_Account.this, Admin_Supplies.class)));
    }
}