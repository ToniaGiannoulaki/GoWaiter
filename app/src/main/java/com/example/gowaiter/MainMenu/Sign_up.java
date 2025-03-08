package com.example.gowaiter.MainMenu;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gowaiter.Loading.Loading_Screen;
import com.example.gowaiter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sign_up extends AppCompatActivity {
    private Spinner spinner;
    private EditText username, enterprise_name, email, password;
    private Button sing_up;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private boolean is_password_visible = false;
    private Loading_Screen loadingScreen = new Loading_Screen();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        username = findViewById(R.id.editText_username);
        enterprise_name = findViewById(R.id.editText_enterprise);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        sing_up = findViewById(R.id.button_sign_up);
        spinner = findViewById(R.id.spinner_role);

        // Sign up button click
        sing_up.setOnClickListener(view -> registerUser());

        // Toggle password visibility (using drawable on the left)
        password.setOnTouchListener((v, event) -> {
            final int DRAWABLE_LEFT = 0;  // index for drawableStart in LTR layouts
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (password.getCompoundDrawables()[DRAWABLE_LEFT] != null) {
                    int drawableWidth = password.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width();
                    if (event.getX() <= (drawableWidth + password.getPaddingStart())) {
                        if (!is_password_visible) {
                            // Show password
                            password.setTransformationMethod(null);
                            password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_open, 0, 0, 0);
                            is_password_visible = true;
                        } else {
                            // Hide password
                            password.setTransformationMethod(android.text.method.PasswordTransformationMethod.getInstance());
                            password.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_icon_purple, 0, 0, 0);
                            is_password_visible = false;
                        }
                        password.setSelection(password.getText().length());
                        return true;
                    }
                }
            }
            return false;
        });

        // Setup spinner with a hint and list of roles
        List<String> roles = new ArrayList<>();
        roles.add(getString(R.string.select_role_hint));
        roles.addAll(Arrays.asList(getResources().getStringArray(R.array.dropdown_items)));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, roles) {
            @Override
            public boolean isEnabled(int position) {
                // disable the first item (hint)
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                textView.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Optional: do something with selected role
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void registerUser() {
        // Get text from EditTexts
        String usernameText = username.getText().toString().trim();
        String enterpriseText = enterprise_name.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String selectedRole = spinner.getSelectedItem().toString().trim();

        // Validate required fields
        if (usernameText.isEmpty() || enterpriseText.isEmpty() || emailText.isEmpty() ||
                passwordText.isEmpty() || selectedRole.equals(getString(R.string.select_role_hint))) {
            Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        // Reject Greek characters in email
        if (emailText.matches(".*[\\u0370-\\u03FF].*")) {
            Toast.makeText(Sign_up.this, getString(R.string.email_no_greek_chars), Toast.LENGTH_SHORT).show();
            return;
        }

        // Mapping dictionary: Greek -> normalized English keys
        Map<String, String> roleMapping = new HashMap<>();
        roleMapping.put("Διαχειριστής", "Admin");
        roleMapping.put("Σερβιτόρος/Σερβιτόρα", "Waiter-Waitress");
        roleMapping.put("Μπαριστα/Μπαρμαν/Μπαργουμαν", "Barista-Barman-Barwoman");
        roleMapping.put("Μάγειρας/Μαγείρισσα/Αρχιμάγειρας", "Cook-Chef");

        // Convert the selected role to the normalized key
        final String normalizedRole = (roleMapping.containsKey(selectedRole)
                ? roleMapping.get(selectedRole)
                : selectedRole)
                .replace("/", "-");

        // Show loading screen
        loadingScreen.show(this);

        if (normalizedRole.equalsIgnoreCase("waiter-waitress")) {
            // ---- WAITERS: Check if enterprise exists
            mDatabase.child(enterpriseText).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        loadingScreen.hide();
                        Toast.makeText(Sign_up.this,
                                getString(R.string.enterprise_not_exist, enterpriseText),
                                Toast.LENGTH_LONG).show();
                    } else {
                        // Enterprise exists -> proceed with Auth creation
                        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                                .addOnCompleteListener(task -> {
                                    loadingScreen.hide();
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        if (firebaseUser != null) {
                                            // Build the Waiter structure
                                            Map<String, Object> waiterNodeMap = new HashMap<>();

                                            // accountSettings
                                            Map<String, Object> accountSettingsMap = new HashMap<>();
                                            accountSettingsMap.put("waiterUsername", usernameText);
                                            accountSettingsMap.put("waiterEmail", emailText);
                                            waiterNodeMap.put("accountSettings", accountSettingsMap);

                                            // orders -> registeredOrders
                                            Map<String, Object> ordersMap = new HashMap<>();
                                            ordersMap.put("registeredOrders", new HashMap<>());
                                            waiterNodeMap.put("orders", ordersMap);

                                            // notifications -> inProgress, accepted, rejected, completedOrders
                                            Map<String, Object> notificationsMap = new HashMap<>();
                                            notificationsMap.put("inProgress", 0);
                                            notificationsMap.put("accepted", 0);
                                            notificationsMap.put("rejected", 0);
                                            notificationsMap.put("completedOrders", 0);
                                            waiterNodeMap.put("notifications", notificationsMap);

                                            // statistics -> totalOrders
                                            Map<String, Object> statisticsMap = new HashMap<>();
                                            statisticsMap.put("totalOrders", 0);
                                            waiterNodeMap.put("statistics", statisticsMap);

                                            // payments -> paidTables, unpaidTables
                                            Map<String, Object> paymentsMap = new HashMap<>();
                                            paymentsMap.put("paidTables", 0);
                                            paymentsMap.put("unpaidTables", 0);
                                            waiterNodeMap.put("payments", paymentsMap);

                                            // supplies -> supplyRequest
                                            Map<String, Object> suppliesMap = new HashMap<>();
                                            suppliesMap.put("supplyRequest", new HashMap<>());
                                            waiterNodeMap.put("supplies", suppliesMap);

                                            // Write to DB -> /enterpriseName/Waiter/username
                                            mDatabase.child(enterpriseText)
                                                    .child("Waiter")
                                                    .child(usernameText)
                                                    .setValue(waiterNodeMap)
                                                    .addOnCompleteListener(dbTask -> {
                                                        if (dbTask.isSuccessful()) {
                                                            Toast.makeText(Sign_up.this,
                                                                    getString(R.string.registration_successful),
                                                                    Toast.LENGTH_SHORT).show();

                                                            // Clear form
                                                            username.setText("");
                                                            enterprise_name.setText("");
                                                            email.setText("");
                                                            password.setText("");
                                                            spinner.setSelection(0);

                                                        } else {
                                                            Toast.makeText(Sign_up.this,
                                                                    "DB Error: " + dbTask.getException().getMessage(),
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    } else {
                                        // Auth error
                                        String errorMsg = (task.getException() != null)
                                                ? task.getException().getMessage()
                                                : "Unknown error";
                                        Toast.makeText(Sign_up.this,
                                                "Authentication Error: " + errorMsg,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                    loadingScreen.hide();
                    Toast.makeText(Sign_up.this,
                            "DB Error: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });

        } else if (normalizedRole.equalsIgnoreCase("barista-barman-barwoman")) {
            // ---- BARISTA-BARMAN-BARWOMAN: Check if enterprise exists
            mDatabase.child(enterpriseText).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        loadingScreen.hide();
                        Toast.makeText(Sign_up.this,
                                getString(R.string.enterprise_not_exist, enterpriseText),
                                Toast.LENGTH_LONG).show();
                    } else {
                        // Enterprise exists -> proceed with Auth creation
                        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                                .addOnCompleteListener(task -> {
                                    loadingScreen.hide();
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        if (firebaseUser != null) {
                                            // Build the Barista structure
                                            Map<String, Object> baristaNodeMap = new HashMap<>();

                                            // accountSettings
                                            Map<String, Object> accountSettingsMap = new HashMap<>();
                                            accountSettingsMap.put("baristaUsername", usernameText);
                                            accountSettingsMap.put("baristaEmail", emailText);
                                            baristaNodeMap.put("accountSettings", accountSettingsMap);

                                            // orders -> inProgress, accepted, rejected, completedOrders
                                            Map<String, Object> ordersMap = new HashMap<>();
                                            ordersMap.put("inProgress", 0);
                                            ordersMap.put("accepted", 0);
                                            ordersMap.put("rejected", 0);
                                            ordersMap.put("completedOrders", 0);
                                            baristaNodeMap.put("orders", ordersMap);

                                            // recipes
                                            baristaNodeMap.put("recipes", new HashMap<>());

                                            // statistics -> totalOrders
                                            Map<String, Object> statisticsMap = new HashMap<>();
                                            statisticsMap.put("totalOrders", 0);
                                            baristaNodeMap.put("statistics", statisticsMap);

                                            // trainingAndSupport
                                            baristaNodeMap.put("trainingAndSupport", new HashMap<>());

                                            // supplies -> requestedSupplies
                                            Map<String, Object> suppliesMap = new HashMap<>();
                                            suppliesMap.put("requestedSupplies", new HashMap<>());
                                            baristaNodeMap.put("supplies", suppliesMap);

                                            // Write to DB -> /enterpriseName/Barista-Barman-Barwoman/username
                                            mDatabase.child(enterpriseText)
                                                    .child("Barista-Barman-Barwoman")
                                                    .child(usernameText)
                                                    .setValue(baristaNodeMap)
                                                    .addOnCompleteListener(dbTask -> {
                                                        if (dbTask.isSuccessful()) {
                                                            Toast.makeText(Sign_up.this,
                                                                    getString(R.string.registration_successful),
                                                                    Toast.LENGTH_SHORT).show();

                                                            // Clear form
                                                            username.setText("");
                                                            enterprise_name.setText("");
                                                            email.setText("");
                                                            password.setText("");
                                                            spinner.setSelection(0);

                                                        } else {
                                                            Toast.makeText(Sign_up.this,
                                                                    "DB Error: " + dbTask.getException().getMessage(),
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    } else {
                                        // Auth error
                                        String errorMsg = (task.getException() != null)
                                                ? task.getException().getMessage()
                                                : "Unknown error";
                                        Toast.makeText(Sign_up.this,
                                                "Authentication Error: " + errorMsg,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                    loadingScreen.hide();
                    Toast.makeText(Sign_up.this,
                            "DB Error: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });

        } else if (normalizedRole.equalsIgnoreCase("cook-chef")) {
            // ---- COOK-CHEF: Same structure & logic as Barista
            // Check if enterprise exists
            mDatabase.child(enterpriseText).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        loadingScreen.hide();
                        Toast.makeText(Sign_up.this,
                                getString(R.string.enterprise_not_exist, enterpriseText),
                                Toast.LENGTH_LONG).show();
                    } else {
                        // Enterprise exists -> proceed with Auth creation
                        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                                .addOnCompleteListener(task -> {
                                    loadingScreen.hide();
                                    if (task.isSuccessful()) {
                                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                        if (firebaseUser != null) {
                                            // Build the Cook/Chef structure (same as Barista)
                                            Map<String, Object> chefNodeMap = new HashMap<>();

                                            // accountSettings
                                            Map<String, Object> accountSettingsMap = new HashMap<>();
                                            accountSettingsMap.put("chefUsername", usernameText);
                                            accountSettingsMap.put("chefEmail", emailText);
                                            chefNodeMap.put("accountSettings", accountSettingsMap);

                                            // orders -> inProgress, accepted, rejected, completedOrders
                                            Map<String, Object> ordersMap = new HashMap<>();
                                            ordersMap.put("inProgress", 0);
                                            ordersMap.put("accepted", 0);
                                            ordersMap.put("rejected", 0);
                                            ordersMap.put("completedOrders", 0);
                                            chefNodeMap.put("orders", ordersMap);

                                            // recipes
                                            chefNodeMap.put("recipes", new HashMap<>());

                                            // statistics -> totalOrders
                                            Map<String, Object> statisticsMap = new HashMap<>();
                                            statisticsMap.put("totalOrders", 0);
                                            chefNodeMap.put("statistics", statisticsMap);

                                            // trainingAndSupport
                                            chefNodeMap.put("trainingAndSupport", new HashMap<>());

                                            // supplies -> requestedSupplies
                                            Map<String, Object> suppliesMap = new HashMap<>();
                                            suppliesMap.put("requestedSupplies", new HashMap<>());
                                            chefNodeMap.put("supplies", suppliesMap);

                                            // Write to DB -> /enterpriseName/Cook-Chef/username
                                            mDatabase.child(enterpriseText)
                                                    .child("Cook-Chef")
                                                    .child(usernameText)
                                                    .setValue(chefNodeMap)
                                                    .addOnCompleteListener(dbTask -> {
                                                        if (dbTask.isSuccessful()) {
                                                            Toast.makeText(Sign_up.this,
                                                                    getString(R.string.registration_successful),
                                                                    Toast.LENGTH_SHORT).show();

                                                            // Clear form
                                                            username.setText("");
                                                            enterprise_name.setText("");
                                                            email.setText("");
                                                            password.setText("");
                                                            spinner.setSelection(0);

                                                        } else {
                                                            Toast.makeText(Sign_up.this,
                                                                    "DB Error: " + dbTask.getException().getMessage(),
                                                                    Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    } else {
                                        // Auth error
                                        String errorMsg = (task.getException() != null)
                                                ? task.getException().getMessage()
                                                : "Unknown error";
                                        Toast.makeText(Sign_up.this,
                                                "Authentication Error: " + errorMsg,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                    loadingScreen.hide();
                    Toast.makeText(Sign_up.this,
                            "DB Error: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            // ---- OTHER ROLES (Admin, etc.)
            mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            // Clear form fields
                            username.setText("");
                            enterprise_name.setText("");
                            email.setText("");
                            password.setText("");
                            spinner.setSelection(0);

                            if (firebaseUser != null) {
                                // 1) Build the accountSettings mapping
                                Map<String, Object> accountSettingsMap = new HashMap<>();
                                // If it's Admin, we keep the same keys as before:
                                accountSettingsMap.put("adminUsername", usernameText);
                                accountSettingsMap.put("adminEmail", emailText);

                                // 2) Build the user node mapping and add subnodes
                                Map<String, Object> userNodeMap = new HashMap<>();
                                userNodeMap.put("accountSettings", accountSettingsMap);

                                switch (normalizedRole.toLowerCase()) {
                                    case "admin":
                                        // -- enterpriseSettings node
                                        Map<String, Object> enterpriseSettingsMap = new HashMap<>();
                                        enterpriseSettingsMap.put("enterpriseName", enterpriseText);

                                        // -- tables
                                        Map<String, Object> tablesMap = new HashMap<>();
                                        tablesMap.put("indoorTables", 0);
                                        tablesMap.put("outdoorTables", 0);
                                        enterpriseSettingsMap.put("tables", tablesMap);

                                        // -- menu
                                        Map<String, Object> menuMap = new HashMap<>();
                                        // categories sub-node with dummy entry
                                        Map<String, Object> categoriesMap = new HashMap<>();
                                        categoriesMap.put("dummyCategory", "initialValue");
                                        menuMap.put("categories", categoriesMap);

                                        enterpriseSettingsMap.put("menu", menuMap);
                                        userNodeMap.put("enterpriseSettings", enterpriseSettingsMap);

                                        // -- statistics node
                                        Map<String, Object> statisticsMap = new HashMap<>();
                                        statisticsMap.put("totalOrders", 0);
                                        userNodeMap.put("statistics", statisticsMap);

                                        // -- other empty nodes for Admin
                                        userNodeMap.put("staffSettings", new HashMap<>());
                                        userNodeMap.put("supplies", new HashMap<>());
                                        userNodeMap.put("payments", new HashMap<>());
                                        break;

                                    default:
                                        Log.d("DEBUG", "Normalized role: [" + normalizedRole + "]");
                                        // fallback if needed
                                        break;
                                }

                                // 3) Write to DB at: /<enterpriseText>/<normalizedRole>/<usernameText>
                                loadingScreen.hide();
                                mDatabase.child(enterpriseText)
                                        .child(normalizedRole)
                                        .child(usernameText)
                                        .setValue(userNodeMap)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                Toast.makeText(Sign_up.this,
                                                        getString(R.string.registration_successful),
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(Sign_up.this,
                                                        getString(R.string.error_storing_data) + ": " +
                                                                dbTask.getException().getMessage(),
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                loadingScreen.hide();
                            }
                        } else {
                            loadingScreen.hide();
                            String errorMessage;
                            try {
                                throw task.getException();
                            } catch (com.google.firebase.auth.FirebaseAuthWeakPasswordException e) {
                                errorMessage = getString(R.string.error_weak_password);
                            } catch (com.google.firebase.auth.FirebaseAuthInvalidCredentialsException e) {
                                errorMessage = getString(R.string.error_invalid_email);
                            } catch (com.google.firebase.auth.FirebaseAuthUserCollisionException e) {
                                errorMessage = getString(R.string.error_email_already_in_use);
                            } catch (Exception e) {
                                errorMessage = getString(R.string.error_registration_failed) + " " + e.getMessage();
                            }
                            Toast.makeText(Sign_up.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
