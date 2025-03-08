package com.example.gowaiter.ChefCook;

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

public class Chef_Cook_Account extends AppCompatActivity {

    CardView account_settings, statistics, orders, recipes, training_and_support, supplies;
    TextView logout, username;  // Added username TextView
    private Loading_Screen loadingScreen = new Loading_Screen();

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
        logout = findViewById(R.id.textView_log_out);
        username = findViewById(R.id.textView_username); // Ensure this ID exists in your layout

        account_settings.setOnClickListener(v -> startActivity(new Intent(Chef_Cook_Account.this, Chef_Cook_Account_Settings.class)));
        statistics.setOnClickListener(v -> startActivity(new Intent(Chef_Cook_Account.this, Chef_Cook_Statistics.class)));
        orders.setOnClickListener(v -> startActivity(new Intent(Chef_Cook_Account.this, Chef_Cook_Orders.class)));
        training_and_support.setOnClickListener(v -> startActivity(new Intent(Chef_Cook_Account.this, Chef_Cook_Training_and_Support.class)));
        recipes.setOnClickListener(v -> startActivity(new Intent(Chef_Cook_Account.this, Chef_Cook_Recipes.class)));
        supplies.setOnClickListener(v -> startActivity(new Intent(Chef_Cook_Account.this, Chef_Cook_Supplies.class)));

        logout.setOnClickListener(v -> showLogoutConfirmation());

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showLogoutConfirmation();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        // Fetch and display the username for the Chef/Cook account
        fetchUsernameDynamically();
    }

    private void showLogoutConfirmation() {
        new AlertDialog.Builder(Chef_Cook_Account.this)
                .setTitle(getString(R.string.logout_title))
                .setMessage(getString(R.string.logout_message))
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    loadingScreen.show(Chef_Cook_Account.this);
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Chef_Cook_Account.this, Sign_in.class);
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

        // Reference to the root of your Realtime Database
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        // We only search in the "Cook-Chef" nodes of all enterprises
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Loop over each enterprise node
                for (DataSnapshot enterpriseSnap : snapshot.getChildren()) {
                    DataSnapshot chefSnap = enterpriseSnap.child("Cook-Chef");
                    if (chefSnap.exists()) {
                        // Loop over each user in the "Cook-Chef" node
                        for (DataSnapshot userSnap : chefSnap.getChildren()) {
                            DataSnapshot accountSettingsSnap = userSnap.child("accountSettings");
                            String emailInDb = accountSettingsSnap.child("chefEmail").getValue(String.class);
                            if (emailInDb != null && emailInDb.equalsIgnoreCase(userEmail)) {
                                String usernameValue = accountSettingsSnap.child("chefUsername").getValue(String.class);
                                if (usernameValue != null) {
                                    username.setText(usernameValue);
                                }
                                return; // Stop after finding the user
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
