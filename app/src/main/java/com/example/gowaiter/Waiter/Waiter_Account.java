package com.example.gowaiter.Waiter;

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

public class Waiter_Account extends AppCompatActivity {

    CardView takeOrder, statistics, notifications, account_settings, payments, supplies;
    TextView logout;
    private Loading_Screen loadingScreen = new Loading_Screen();
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
        logout = findViewById(R.id.textView_log_out);

        takeOrder.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Take_Order.class)));
        statistics.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Statistics.class)));
        notifications.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Notifications.class)));
        account_settings.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Account_Settings.class)));
        payments.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Payments.class)));
        supplies.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Supplies.class)));

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
        new AlertDialog.Builder(Waiter_Account.this)
                .setTitle(getString(R.string.logout_title))
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    // Show the loading screen before signing out
                    loadingScreen.show(Waiter_Account.this);
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Waiter_Account.this, Sign_in.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }
}