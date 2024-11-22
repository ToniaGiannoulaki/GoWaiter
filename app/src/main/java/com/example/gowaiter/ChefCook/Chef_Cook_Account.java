package com.example.gowaiter.ChefCook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;

import com.example.gowaiter.MainMenu.Sign_in;
import com.example.gowaiter.R;

public class Chef_Cook_Account extends AppCompatActivity {

    CardView account_settings, statistics, orders, recipes, training_and_support, supplies;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_cook_account);

        orders = findViewById(R.id.card_view_orders_chef_cook);
        account_settings = findViewById(R.id.card_view_account_settings_chef_cook);
        statistics = findViewById(R.id.card_view_statistics_chef_cook);
        training_and_support = findViewById(R.id.card_view_training_support_chef_cook);
        recipes = findViewById(R.id.card_view_food_recipes);
        supplies = findViewById(R.id.card_view_supplies_chef_cook);

        account_settings.setOnClickListener(v -> startActivity(new Intent(Chef_Cook_Account.this, Chef_Cook_Account_Settings.class)));
        statistics.setOnClickListener(v -> startActivity(new Intent(Chef_Cook_Account.this, Chef_Cook_Statistics.class)));
        orders.setOnClickListener(v -> startActivity(new Intent(Chef_Cook_Account.this, Chef_Cook_Orders.class)));
        training_and_support.setOnClickListener(v -> startActivity(new Intent(Chef_Cook_Account.this, Chef_Cook_Training_and_Support.class)));
        recipes.setOnClickListener(v -> startActivity(new Intent(Chef_Cook_Account.this, Chef_Cook_Recipes.class)));
        supplies.setOnClickListener(v -> startActivity(new Intent(Chef_Cook_Account.this, Chef_Cook_Supplies.class)));
    }
}