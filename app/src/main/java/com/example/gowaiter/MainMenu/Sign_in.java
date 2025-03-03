package com.example.gowaiter.MainMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.HashMap;
import android.util.Log;

import com.example.gowaiter.Admin.Admin_Account;
import com.example.gowaiter.BaristaBarman.Barista_Barman_Account;
import com.example.gowaiter.ChefCook.Chef_Cook_Account;
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
    // Mapping role strings (in Greek) to corresponding activity classes.
    private Map<String, Class<?>> roleActivityMap;
    private boolean is_password_visible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Follow system-wide dark mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        roleActivityMap = new HashMap<>();
        roleActivityMap.put("Admin", Admin_Account.class);
        roleActivityMap.put("Διαχειριστής", Admin_Account.class);
        roleActivityMap.put("Waiter-Waitress", Waiter_Account.class);
        roleActivityMap.put("Σερβιτόρος-Σερβιτόρα", Waiter_Account.class);
        roleActivityMap.put("Barista-Barman-Barwoman", Barista_Barman_Account.class);
        roleActivityMap.put("Μπαριστα-Μπαρμαν-Μπαργουμαν", Barista_Barman_Account.class);
        roleActivityMap.put("Cook-Chef", Chef_Cook_Account.class);
        roleActivityMap.put("Μάγειρας-Μαγείρισσα-Αρχιμάγειρας", Chef_Cook_Account.class);

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

        // 1. Check if email and password are filled
        if (emailText.isEmpty() || passwordText.isEmpty()) {
            Toast.makeText(Sign_in.this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Sign in with Firebase Authentication
        mAuth.signInWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userEmail = firebaseUser.getEmail();

                            // 3. Search the entire DB tree for the user record
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    boolean userFound = false;
                                    String userRole = null;

                                    // Loop through each enterprise node
                                    for (DataSnapshot enterpriseSnapshot : snapshot.getChildren()) {
                                        // Loop through each role node
                                        for (DataSnapshot roleSnapshot : enterpriseSnapshot.getChildren()) {
                                            // Loop through each username node
                                            for (DataSnapshot userSnapshot : roleSnapshot.getChildren()) {
                                                String emailFromDb = userSnapshot.child("email").getValue(String.class);
                                                if (emailFromDb != null && emailFromDb.equals(userEmail)) {
                                                    // Found the user record
                                                    userFound = true;
                                                    userRole = userSnapshot.child("role").getValue(String.class);
                                                    break;
                                                }
                                            }
                                            if (userFound) break;
                                        }
                                        if (userFound) break;
                                    }

                                    // 4. Validate the role key and start the correct activity
                                    if (userFound && userRole != null && roleActivityMap.containsKey(userRole)) {
                                        Class<?> targetActivity = roleActivityMap.get(userRole);
                                        startActivity(new Intent(Sign_in.this, targetActivity));
                                        finish();
                                    } else {
                                        // Sign in failed – likely wrong credentials
                                        Toast.makeText(Sign_in.this, getString(R.string.error_wrong_credentials), Toast.LENGTH_SHORT).show();
                                        Log.d("Sign_in", "User role not recognized");                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Sign in failed – likely wrong credentials
                                    Toast.makeText(Sign_in.this, getString(R.string.error_wrong_credentials), Toast.LENGTH_SHORT).show();
                                    Log.e("Sign_in", "Database error: " + error.getMessage());                                }
                            });
                        }
                    } else {
                        // Sign in failed – likely wrong credentials
                        Toast.makeText(Sign_in.this, getString(R.string.error_wrong_credentials), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
