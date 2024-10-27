package com.example.gowaiter.BaristaBarman;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

import com.example.gowaiter.Admin.Admin_Account;
import com.example.gowaiter.Admin.Admin_Account_Settings;
import com.example.gowaiter.R;

public class Barista_Barman_Account extends AppCompatActivity {
    CardView account_settings, orders, recipes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barista_barman_account);

        orders = findViewById(R.id.card_view_orders_barista_barman);
        recipes = findViewById(R.id.card_view_drink_beverage_recipes);
        account_settings = findViewById(R.id.card_view_account_settings_barista_barman);

        orders.setOnClickListener(v -> startActivity(new Intent(Barista_Barman_Account.this, Barista_Barman_Orders.class)));
        recipes.setOnClickListener(v -> startActivity(new Intent(Barista_Barman_Account.this, Barista_Barman_Recipes.class)));
        account_settings.setOnClickListener(v -> startActivity(new Intent(Barista_Barman_Account.this, Barista_Barman_Account_Settings.class)));
    }
}