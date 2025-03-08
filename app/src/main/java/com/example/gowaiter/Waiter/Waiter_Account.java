package com.example.gowaiter.Waiter;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gowaiter.Loading.Loading_Screen;
import com.example.gowaiter.MainMenu.Sign_in;
import com.example.gowaiter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class Waiter_Account extends AppCompatActivity {

    CardView takeOrder, statistics, notifications, account_settings, payments, supplies;
    TextView logout, username;  // Added username TextView
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
        username = findViewById(R.id.textView_username); // Ensure this ID exists in your layout

        takeOrder.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Take_Order.class)));
        statistics.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Statistics.class)));
        notifications.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Notifications.class)));
        account_settings.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Account_Settings.class)));
        payments.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Payments.class)));
        supplies.setOnClickListener(v -> startActivity(new Intent(Waiter_Account.this, Waiter_Supplies.class)));

        logout.setOnClickListener(v -> showLogoutConfirmation());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showLogoutConfirmation();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        // Fetch the username from the database for the logged-in Waiter
        fetchUsernameDynamically();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(Waiter_Account.this)
                .setTitle(getString(R.string.logout_title))
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
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

    private void fetchUsernameDynamically() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(Waiter_Account.this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }
        String userEmail = currentUser.getEmail();
        if (userEmail == null) {
            Toast.makeText(Waiter_Account.this, "User email not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reference to the root of your DB
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        // Search through all enterprises for the current Waiter
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                // Loop over each enterprise node
                for (DataSnapshot enterpriseSnap : snapshot.getChildren()) {
                    DataSnapshot waiterSnap = enterpriseSnap.child("Waiter");
                    if (waiterSnap.exists()) {
                        // Loop over each user in the "Waiter" node
                        for (DataSnapshot userSnap : waiterSnap.getChildren()) {
                            if (!userSnap.hasChild("accountSettings")) continue;
                            DataSnapshot accountSettingsSnap = userSnap.child("accountSettings");
                            String emailInDb = accountSettingsSnap.child("waiterEmail").getValue(String.class);
                            if (emailInDb != null && emailInDb.trim().equalsIgnoreCase(userEmail.trim())) {
                                String usernameValue = accountSettingsSnap.child("waiterUsername").getValue(String.class);
                                if (usernameValue != null) {
                                    username.setText(usernameValue);
                                    found = true;
                                    Log.d("Waiter_Account", "Username found: " + usernameValue);
                                }
                                break;
                            }
                        }
                    }
                    if (found) break;
                }
                if (!found) {
                    Toast.makeText(Waiter_Account.this, "Username not found", Toast.LENGTH_SHORT).show();
                    Log.d("Waiter_Account", "User with email " + userEmail + " not found in any Waiter node.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Waiter_Account.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Waiter_Account", "DB error: " + error.getMessage());
            }
        });
    }
}