package com.example.gowaiter.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

import com.example.gowaiter.R;

public class Admin_Account extends AppCompatActivity {

    CardView account_settings, staff_settings, enterprise_settings, messages, payments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_account);

        account_settings = findViewById(R.id.card_view_account_settings_admin);
        staff_settings = findViewById(R.id.card_view_staff_settings);
        enterprise_settings = findViewById(R.id.card_view_enterprise_settings);
        messages = findViewById(R.id.card_view_messages);
        payments = findViewById(R.id.card_view_payments);

        account_settings.setOnClickListener(v -> startActivity(new Intent(Admin_Account.this, Admin_Account_Settings.class)));
        staff_settings.setOnClickListener(v -> startActivity(new Intent(Admin_Account.this, Admin_Staff_Settings.class)));
        enterprise_settings.setOnClickListener(v -> startActivity(new Intent(Admin_Account.this, Admin_Enterprise_Settings.class)));
        messages.setOnClickListener(v -> startActivity(new Intent(Admin_Account.this, Admin_Messages.class)));
        payments.setOnClickListener(v -> startActivity(new Intent(Admin_Account.this, Admin_Payments.class)));
    }
}