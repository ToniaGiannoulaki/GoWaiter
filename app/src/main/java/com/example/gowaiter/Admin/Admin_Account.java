package com.example.gowaiter.Admin;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Admin_Account extends AppCompatActivity {

    CardView account_settings, staff_settings, enterprise_settings, statistics, payments, supplies;
    TextView logout, username;
    private Loading_Screen loadingScreen = new Loading_Screen();

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
        username = findViewById(R.id.textView_username);
        logout = findViewById(R.id.textView_log_out);

        account_settings.setOnClickListener(v -> startActivity(new Intent(Admin_Account.this, Admin_Account_Settings.class)));
        staff_settings.setOnClickListener(v -> startActivity(new Intent(Admin_Account.this, Admin_Staff_Settings.class)));
        enterprise_settings.setOnClickListener(v -> startActivity(new Intent(Admin_Account.this, Admin_Enterprise_Settings.class)));
        statistics.setOnClickListener(v -> startActivity(new Intent(Admin_Account.this, Admin_Statistics.class)));
        payments.setOnClickListener(v -> startActivity(new Intent(Admin_Account.this, Admin_Payments.class)));
        supplies.setOnClickListener(v -> startActivity(new Intent(Admin_Account.this, Admin_Supplies.class)));

        // Retrieve username
        fetchUsernameDynamically();

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
        new AlertDialog.Builder(Admin_Account.this)
                .setTitle(getString(R.string.logout_title))
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    // Show the loading screen before signing out
                    loadingScreen.show(Admin_Account.this);
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Admin_Account.this, Sign_in.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton(getString(R.string.no), null)
                .show();
    }

    private void fetchUsernameDynamically() {
        // Get the currently logged in user from FirebaseAuth
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;  // No user logged in

        String userEmail = currentUser.getEmail();
        if (userEmail == null) return;    // User might not have an email

        // Reference to the root of your Realtime Database
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        // Read all data once
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Loop over all enterprises
                for (DataSnapshot enterpriseSnap : dataSnapshot.getChildren()) {
                    // Loop over all roles within each enterprise
                    for (DataSnapshot roleSnap : enterpriseSnap.getChildren()) {
                        // Loop over all username nodes under each role
                        for (DataSnapshot userSnap : roleSnap.getChildren()) {
                            // Retrieve email from accountSettings for Admin
                            DataSnapshot accountSettingsSnap = userSnap.child("accountSettings");
                            String emailInDb = accountSettingsSnap.child("adminEmail").getValue(String.class);
                            if (emailInDb != null && emailInDb.equalsIgnoreCase(userEmail)) {
                                // We found the user in the DB
                                String usernameValue = accountSettingsSnap.child("adminUsername").getValue(String.class);
                                if (usernameValue != null) {
                                    username.setText(usernameValue);
                                }
                                // Stop searching once the user is found
                                return;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors if needed
            }
        });
    }
}
