package com.example.gowaiter.BaristaBarman;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gowaiter.Loading.Loading_Screen;
import com.example.gowaiter.MainMenu.Sign_in;
import com.example.gowaiter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class Barista_Barman_Account extends AppCompatActivity {

    CardView account_settings, orders, recipes, statistics, training_and_support, supplies;
    TextView logout, username;
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
        logout = findViewById(R.id.textView_log_out);
        username = findViewById(R.id.textView_username); // Make sure this ID exists in your layout

        account_settings.setOnClickListener(v -> startActivity(new Intent(Barista_Barman_Account.this, Barista_Barman_Account_Settings.class)));
        orders.setOnClickListener(v -> startActivity(new Intent(Barista_Barman_Account.this, Barista_Barman_Orders.class)));
        recipes.setOnClickListener(v -> startActivity(new Intent(Barista_Barman_Account.this, Barista_Barman_Recipes.class)));
        statistics.setOnClickListener(v -> startActivity(new Intent(Barista_Barman_Account.this, Barista_Barman_Statistics.class)));
        training_and_support.setOnClickListener(v -> startActivity(new Intent(Barista_Barman_Account.this, Barista_Barman_Training_and_Support.class)));
        supplies.setOnClickListener(v -> startActivity(new Intent(Barista_Barman_Account.this, Barista_Barman_Supplies.class)));

        logout.setOnClickListener(v -> showLogoutConfirmation());

        // Handle device back button
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showLogoutConfirmation();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        // Fetch and display the username dynamically
        fetchUsernameDynamically();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(Barista_Barman_Account.this)
                .setTitle(getString(R.string.logout_title))
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
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

    private void fetchUsernameDynamically() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;
        String userEmail = currentUser.getEmail();
        if (userEmail == null) return;

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        // Loop only in the "Barista-Barman-Barwoman" nodes of all enterprises
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Loop over each enterprise
                for (DataSnapshot enterpriseSnap : snapshot.getChildren()) {
                    DataSnapshot baristaSnap = enterpriseSnap.child("Barista-Barman-Barwoman");
                    if (baristaSnap.exists()) {
                        // Loop over each user in Barista-Barman-Barwoman
                        for (DataSnapshot userSnap : baristaSnap.getChildren()) {
                            DataSnapshot accountSettingsSnap = userSnap.child("accountSettings");
                            String emailInDb = accountSettingsSnap.child("baristaEmail").getValue(String.class);
                            if (emailInDb != null && emailInDb.equalsIgnoreCase(userEmail)) {
                                String usernameValue = accountSettingsSnap.child("baristaUsername").getValue(String.class);
                                if (usernameValue != null) {
                                    username.setText(usernameValue);
                                }
                                return; // User found, exit method
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Optionally handle errors here
            }
        });
    }
}
