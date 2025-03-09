package com.example.gowaiter.MainMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.gowaiter.Loading.Loading_Screen;
import com.example.gowaiter.R;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import java.util.*;

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

        // Toggle password visibility
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

    /**
     * Helper method that loops over all Admin children to find a staffSettings node
     * containing a record with matching email and expectedRole. If found, it calls onSuccess.run().
     */
    private void checkStaffSettingsAndCreateUser(String enterpriseText, String emailText, String expectedRole, Runnable onSuccess) {
        mDatabase.child(enterpriseText)
                .child("Admin")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot adminSnap) {
                        boolean staffSettingsFound = false;
                        for (DataSnapshot adminChild : adminSnap.getChildren()) {
                            if (adminChild.hasChild("staffSettings")) {
                                DataSnapshot staffSnap = adminChild.child("staffSettings");
                                boolean foundUserInStaff = false;
                                for (DataSnapshot ds : staffSnap.getChildren()) {
                                    String staffEmail = ds.child("email").getValue(String.class);
                                    String staffRole = ds.child("role").getValue(String.class);
                                    if (staffEmail != null && staffEmail.equalsIgnoreCase(emailText)
                                            && staffRole != null && staffRole.equalsIgnoreCase(expectedRole)) {
                                        foundUserInStaff = true;
                                        break;
                                    }
                                }
                                if (foundUserInStaff) {
                                    onSuccess.run();
                                } else {
                                    Toast.makeText(Sign_up.this,
                                            getString(R.string.user_role_not_added),
                                            Toast.LENGTH_LONG).show();
                                    loadingScreen.hide();
                                }
                                staffSettingsFound = true;
                                break;
                            }
                        }
                        if (!staffSettingsFound) {
                            Toast.makeText(Sign_up.this,
                                    getString(R.string.user_role_not_added),
                                    Toast.LENGTH_LONG).show();
                            loadingScreen.hide();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Sign_up.this,
                                "DB Error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
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

        // ======================
        // Mapping dictionary: Greek -> EXACT English keys
        // ======================
        Map<String, String> roleMapping = new HashMap<>();
        roleMapping.put("Διαχειριστής", "Admin");
        roleMapping.put("Σερβιτόρος/Σερβιτόρα", "Waiter");
        roleMapping.put("Μπαριστα/Μπαρμαν/Μπαργουμαν", "Barista-Barman-Barwoman");
        roleMapping.put("Μάγειρας/Μαγείρισσα/Αρχιμάγειρας", "Cook-Chef");

        // Convert the selected role to the normalized key (e.g. "Waiter", "Barista", etc.)
        final String normalizedRole = roleMapping.containsKey(selectedRole)
                ? roleMapping.get(selectedRole)
                : selectedRole;

        // Show loading screen
        loadingScreen.show(this);

        if (normalizedRole.equalsIgnoreCase("waiter")) {
            // ---- WAITER: Check if enterprise exists first.
            mDatabase.child(enterpriseText)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                loadingScreen.hide();
                                Toast.makeText(Sign_up.this,
                                        getString(R.string.enterprise_not_exist, enterpriseText),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                // Use helper method to check staffSettings for "Waiter"
                                checkStaffSettingsAndCreateUser(enterpriseText, emailText, "Waiter", () -> {
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

                                                        // notifications
                                                        Map<String, Object> notificationsMap = new HashMap<>();
                                                        notificationsMap.put("inProgress", 0);
                                                        notificationsMap.put("accepted", 0);
                                                        notificationsMap.put("rejected", 0);
                                                        notificationsMap.put("completedOrders", 0);
                                                        waiterNodeMap.put("notifications", notificationsMap);

                                                        // statistics
                                                        Map<String, Object> statisticsMap = new HashMap<>();
                                                        statisticsMap.put("totalOrders", 0);
                                                        waiterNodeMap.put("statistics", statisticsMap);

                                                        // payments
                                                        Map<String, Object> paymentsMap = new HashMap<>();
                                                        paymentsMap.put("paidTables", 0);
                                                        paymentsMap.put("unpaidTables", 0);
                                                        waiterNodeMap.put("payments", paymentsMap);

                                                        // supplies
                                                        Map<String, Object> suppliesMap = new HashMap<>();
                                                        suppliesMap.put("supplyRequest", new HashMap<>());
                                                        waiterNodeMap.put("supplies", suppliesMap);

                                                        // Write to DB -> /enterpriseText/Waiter/usernameText
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
                                                    String errorMsg = (task.getException() != null)
                                                            ? task.getException().getMessage()
                                                            : "Unknown error";
                                                    Toast.makeText(Sign_up.this,
                                                            "Authentication Error: " + errorMsg,
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            loadingScreen.hide();
                            Toast.makeText(Sign_up.this,
                                    "DB Error: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else if (normalizedRole.equalsIgnoreCase("barista")) {
            // ---- BARISTA: Check if enterprise exists first.
            mDatabase.child(enterpriseText)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                loadingScreen.hide();
                                Toast.makeText(Sign_up.this,
                                        getString(R.string.enterprise_not_exist, enterpriseText),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                // Check staffSettings for "Barista"
                                checkStaffSettingsAndCreateUser(enterpriseText, emailText, "Barista-Barman-Barwoman", () -> {
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

                                                        // orders
                                                        Map<String, Object> ordersMap = new HashMap<>();
                                                        ordersMap.put("inProgress", 0);
                                                        ordersMap.put("accepted", 0);
                                                        ordersMap.put("rejected", 0);
                                                        ordersMap.put("completedOrders", 0);
                                                        baristaNodeMap.put("orders", ordersMap);

                                                        // recipes
                                                        baristaNodeMap.put("recipes", new HashMap<>());

                                                        // statistics
                                                        Map<String, Object> statisticsMap = new HashMap<>();
                                                        statisticsMap.put("totalOrders", 0);
                                                        baristaNodeMap.put("statistics", statisticsMap);

                                                        // trainingAndSupport
                                                        baristaNodeMap.put("trainingAndSupport", new HashMap<>());

                                                        // supplies
                                                        Map<String, Object> suppliesMap = new HashMap<>();
                                                        suppliesMap.put("requestedSupplies", new HashMap<>());
                                                        baristaNodeMap.put("supplies", suppliesMap);

                                                        // Write to DB -> /enterpriseText/Barista/usernameText
                                                        mDatabase.child(enterpriseText)
                                                                .child("Barista")
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
                                                    String errorMsg = (task.getException() != null)
                                                            ? task.getException().getMessage()
                                                            : "Unknown error";
                                                    Toast.makeText(Sign_up.this,
                                                            "Authentication Error: " + errorMsg,
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            loadingScreen.hide();
                            Toast.makeText(Sign_up.this,
                                    "DB Error: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else if (normalizedRole.equalsIgnoreCase("cook-chef")) {
            // ---- COOK-CHEF: Check if enterprise exists first.
            mDatabase.child(enterpriseText)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()) {
                                loadingScreen.hide();
                                Toast.makeText(Sign_up.this,
                                        getString(R.string.enterprise_not_exist, enterpriseText),
                                        Toast.LENGTH_LONG).show();
                            } else {
                                // Check staffSettings for "Cook-Chef"
                                checkStaffSettingsAndCreateUser(enterpriseText, emailText, "Cook-Chef", () -> {
                                    mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                                            .addOnCompleteListener(task -> {
                                                loadingScreen.hide();
                                                if (task.isSuccessful()) {
                                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                                    if (firebaseUser != null) {
                                                        // Build the Cook/Chef structure
                                                        Map<String, Object> chefNodeMap = new HashMap<>();

                                                        // accountSettings
                                                        Map<String, Object> accountSettingsMap = new HashMap<>();
                                                        accountSettingsMap.put("chefUsername", usernameText);
                                                        accountSettingsMap.put("chefEmail", emailText);
                                                        chefNodeMap.put("accountSettings", accountSettingsMap);

                                                        // orders
                                                        Map<String, Object> ordersMap = new HashMap<>();
                                                        ordersMap.put("inProgress", 0);
                                                        ordersMap.put("accepted", 0);
                                                        ordersMap.put("rejected", 0);
                                                        ordersMap.put("completedOrders", 0);
                                                        chefNodeMap.put("orders", ordersMap);

                                                        // recipes
                                                        chefNodeMap.put("recipes", new HashMap<>());

                                                        // statistics
                                                        Map<String, Object> statisticsMap = new HashMap<>();
                                                        statisticsMap.put("totalOrders", 0);
                                                        chefNodeMap.put("statistics", statisticsMap);

                                                        // trainingAndSupport
                                                        chefNodeMap.put("trainingAndSupport", new HashMap<>());

                                                        // supplies
                                                        Map<String, Object> suppliesMap = new HashMap<>();
                                                        suppliesMap.put("requestedSupplies", new HashMap<>());
                                                        chefNodeMap.put("supplies", suppliesMap);

                                                        // Write to DB -> /enterpriseText/Cook-Chef/usernameText
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
                                                    String errorMsg = (task.getException() != null)
                                                            ? task.getException().getMessage()
                                                            : "Unknown error";
                                                    Toast.makeText(Sign_up.this,
                                                            "Authentication Error: " + errorMsg,
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
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
                                // Build the accountSettings mapping
                                Map<String, Object> accountSettingsMap = new HashMap<>();
                                accountSettingsMap.put("adminUsername", usernameText);
                                accountSettingsMap.put("adminEmail", emailText);

                                // Build the user node mapping and add subnodes
                                Map<String, Object> userNodeMap = new HashMap<>();
                                userNodeMap.put("accountSettings", accountSettingsMap);

                                if (normalizedRole.equalsIgnoreCase("admin")) {
                                    // enterpriseSettings node
                                    Map<String, Object> enterpriseSettingsMap = new HashMap<>();
                                    enterpriseSettingsMap.put("enterpriseName", enterpriseText);

                                    // tables
                                    Map<String, Object> tablesMap = new HashMap<>();
                                    tablesMap.put("indoorTables", 0);
                                    tablesMap.put("outdoorTables", 0);
                                    enterpriseSettingsMap.put("tables", tablesMap);

                                    // menu with a dummy category
                                    Map<String, Object> menuMap = new HashMap<>();
                                    Map<String, Object> categoriesMap = new HashMap<>();
                                    categoriesMap.put("dummyCategory", "initialValue");
                                    menuMap.put("categories", categoriesMap);
                                    enterpriseSettingsMap.put("menu", menuMap);
                                    userNodeMap.put("enterpriseSettings", enterpriseSettingsMap);

                                    // statistics
                                    Map<String, Object> statisticsMap = new HashMap<>();
                                    statisticsMap.put("totalOrders", 0);
                                    userNodeMap.put("statistics", statisticsMap);

                                    // other empty nodes for Admin
                                    userNodeMap.put("staffSettings", new HashMap<>());
                                    userNodeMap.put("supplies", new HashMap<>());
                                    userNodeMap.put("payments", new HashMap<>());
                                }

                                // Write to DB at: /enterpriseText/normalizedRole/usernameText
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
                            } catch (FirebaseAuthWeakPasswordException e) {
                                errorMessage = getString(R.string.error_weak_password);
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                errorMessage = getString(R.string.error_invalid_email);
                            } catch (FirebaseAuthUserCollisionException e) {
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
