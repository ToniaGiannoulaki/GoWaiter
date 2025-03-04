package com.example.gowaiter.BaristaBarman;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.gowaiter.Loading.Loading_Screen;
import com.example.gowaiter.MainMenu.Sign_in;
import com.example.gowaiter.R;
import com.google.firebase.auth.FirebaseAuth;

public class Barista_Barman_Account extends AppCompatActivity {
    CardView account_settings, orders, recipes, statistics, training_and_support, supplies;
    TextView logout;
    private Loading_Screen loadingScreen = new Loading_Screen();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barista_barman_account);

        orders = findViewById(R.id.card_view_orders_barista_barman);
        recipes = findViewById(R.id.card_view_drink_beverage_recipes);
        statistics = findViewById(R.id.card_view_statistics_barista_barman);
        training_and_support = findViewById(R.id.card_view_training_support_barista_barman);
        supplies = findViewById(R.id.card_view_supplies_barista_barman);
        account_settings = findViewById(R.id.card_view_account_settings_barista_barman);
        logout = findViewById(R.id.textView_log_out); // Ensure this matches your layout id

        orders.setOnClickListener(v -> startActivity(new Intent(Barista_Barman_Account.this, Barista_Barman_Orders.class)));
        recipes.setOnClickListener(v -> startActivity(new Intent(Barista_Barman_Account.this, Barista_Barman_Recipes.class)));
        statistics.setOnClickListener(v -> startActivity(new Intent(Barista_Barman_Account.this, Barista_Barman_Statistics.class)));
        training_and_support.setOnClickListener(v -> startActivity(new Intent(Barista_Barman_Account.this, Barista_Barman_Training_and_Support.class)));
        supplies.setOnClickListener(v -> startActivity(new Intent(Barista_Barman_Account.this, Barista_Barman_Supplies.class)));
        account_settings.setOnClickListener(v -> startActivity(new Intent(Barista_Barman_Account.this, Barista_Barman_Account_Settings.class)));

        // Logout functionality for the logout TextView using string resources
        logout.setOnClickListener(v -> showLogoutConfirmation());

        // Add OnBackPressedCallback to handle the device back button press
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showLogoutConfirmation();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    // Method to show the logout confirmation dialog
    private void showLogoutConfirmation() {
        new AlertDialog.Builder(Barista_Barman_Account.this)
                .setTitle(getString(R.string.logout_title))
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    // Show the loading screen before signing out
                    loadingScreen.show(Barista_Barman_Account.this);
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Barista_Barman_Account.this, Sign_in.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}