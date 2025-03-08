package com.example.gowaiter.MainMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.HashMap;
import android.util.Log;

import com.example.gowaiter.Admin.Admin_Account;
import com.example.gowaiter.BaristaBarman.Barista_Barman_Account;
import com.example.gowaiter.ChefCook.Chef_Cook_Account;
import com.example.gowaiter.Loading.Loading_Screen;
import com.example.gowaiter.R;
import com.example.gowaiter.Waiter.Waiter_Account;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class Sign_in extends AppCompatActivity {
    private EditText email, password;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Button sign_up, sign_in;
    private Map<String, Class<?>> roleActivityMap;
    private boolean is_password_visible = false;
    private Loading_Screen loadingScreen = new Loading_Screen();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Follow system-wide dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        roleActivityMap = new HashMap<>();
        roleActivityMap.put("Admin", Admin_Account.class);
        roleActivityMap.put("Waiter", Waiter_Account.class);
        roleActivityMap.put("Barista-Barman-Barwoman", Barista_Barman_Account.class);
        roleActivityMap.put("Cook-Chef", Chef_Cook_Account.class);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Find views (make sure these IDs match your layout)
        sign_up = findViewById(R.id.button_sign_up);
        sign_in = findViewById(R.id.button_sign_in);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);

        // Navigate to registration screen
        sign_up.setOnClickListener(v -> startActivity(new Intent(Sign_in.this, Sign_up.class)));

        // Sign in button action
        sign_in.setOnClickListener(v -> signInUser());

        // Setup onTouchListener on the password EditText to toggle visibility
        password.setOnTouchListener((v, event) -> {
            final int DRAWABLE_LEFT = 0;  // index for drawableStart in LTR layouts
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // Ensure the left drawable exists
                if (password.getCompoundDrawables()[DRAWABLE_LEFT] != null) {
                    // Get the width of the drawable
                    int drawableWidth = password.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width();
                    // event.getX() gives the X coordinate relative to the EditText
                    if (event.getX() <= (drawableWidth + password.getPaddingStart())) {
                        // Toggle password visibility
                        if (!is_password_visible) {
                            // Show password
                            password.setTransformationMethod(null);
                            // Change the drawable to open lock; note: the parameters are for left, top, right, bottom
                            password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_open, 0, 0, 0);
                            is_password_visible = true;
                        } else {
                            // Hide password
                            password.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                            password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_icon_purple, 0, 0, 0);
                            is_password_visible = false;
                        }
                        // Move cursor to the end of the text
                        password.setSelection(password.getText().length());
                        return true; // event handled
                    }
                }
            }
            return false;
        });
    }
    private void signInUser() {
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();

        if (emailText.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(Sign_in.this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        loadingScreen.show(this);

        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userEmail = firebaseUser.getEmail();

                            // Search the entire DB for a matching user in accountSettings
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    loadingScreen.hide();
                                    boolean userFound = false;
                                    String userRole = null;

                                    // Loop over each enterprise
                                    for (DataSnapshot enterpriseSnapshot : snapshot.getChildren()) {
                                        // Loop over each role node under the enterprise
                                        for (DataSnapshot roleSnapshot : enterpriseSnapshot.getChildren()) {
                                            String roleKey = roleSnapshot.getKey();  // e.g., "admin", "Waiter", "Barista-Barman-Barwoman", "Cook-Chef"
                                            // Loop over each user under the role node
                                            for (DataSnapshot userSnapshot : roleSnapshot.getChildren()) {
                                                // Retrieve the accountSettings node as a Map
                                                Map<String, Object> accountSettings = (Map<String, Object>) userSnapshot.child("accountSettings").getValue();
                                                if (accountSettings == null) continue;
                                                String emailFromDb = null;
                                                // Determine the key to use based on the roleKey
                                                if (roleKey.equalsIgnoreCase("admin")) {
                                                    emailFromDb = (String) accountSettings.get("adminEmail");
                                                } else if (roleKey.equalsIgnoreCase("Waiter") || roleKey.equalsIgnoreCase("waiter")) {
                                                    emailFromDb = (String) accountSettings.get("waiterEmail");
                                                } else if (roleKey.equalsIgnoreCase("Barista-Barman-Barwoman")) {
                                                    emailFromDb = (String) accountSettings.get("baristaEmail");
                                                } else if (roleKey.equalsIgnoreCase("Cook-Chef")) {
                                                    emailFromDb = (String) accountSettings.get("chefEmail");
                                                }

                                                if (emailFromDb != null && emailFromDb.equalsIgnoreCase(userEmail)) {
                                                    userFound = true;
                                                    userRole = roleKey;  // We infer role from the parent key
                                                    break;
                                                }
                                            }
                                            if (userFound) break;
                                        }
                                        if (userFound) break;
                                    }

                                    if (userFound && userRole != null && roleActivityMap.containsKey(userRole)) {
                                        Class<?> targetActivity = roleActivityMap.get(userRole);
                                        startActivity(new Intent(Sign_in.this, targetActivity));
                                        finish();
                                    } else {
                                        Toast.makeText(Sign_in.this, getString(R.string.error_wrong_credentials), Toast.LENGTH_SHORT).show();
                                        Log.d("Sign_in", "User not found or role not recognized");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    loadingScreen.hide();
                                    Toast.makeText(Sign_in.this, getString(R.string.error_wrong_credentials), Toast.LENGTH_SHORT).show();
                                    Log.e("Sign_in", "Database error: " + error.getMessage());
                                }
                            });
                        } else {
                            loadingScreen.hide();
                        }
                    } else {
                        loadingScreen.hide();
                        Toast.makeText(Sign_in.this, getString(R.string.error_wrong_credentials), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}