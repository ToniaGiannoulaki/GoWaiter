package com.example.gowaiter.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.gowaiter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Admin_Staff_Settings extends AppCompatActivity {

    private ImageButton btnBack;
    private Spinner spinner_add;
    private EditText addemail;
    private Button buttonAddStaff, buttonDeleteStaff;
    // For removing staff
    private Spinner spinner_delete;
    // Firebase DB reference
    private DatabaseReference mDatabase;
    // We'll load the actual enterpriseName & adminUsername from the DB (not hardcoded)
    private String enterpriseName = null;
    private String adminUsername = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_staff_settings);

        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(view -> onBackPressed());
        }

        spinner_add = findViewById(R.id.spinner_select_role_add);
        addemail = findViewById(R.id.editText_enter_employees_email);
        buttonAddStaff = findViewById(R.id.button_add_employee);

        // (Optional) For deletion
        spinner_delete = findViewById(R.id.spinner_select_role_delete);
        buttonDeleteStaff = findViewById(R.id.button_delete_employee);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Load Admin paths from DB (enterpriseName & adminUsername)
        loadAdminPaths();

        // Setup spinners
        setupSpinners();

        // Set button listeners
        buttonAddStaff.setOnClickListener(v -> addStaffMember());
        if (buttonDeleteStaff != null) {
            buttonDeleteStaff.setOnClickListener(v -> removeStaffMember());
        }
    }

    private void loadAdminPaths() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user logged in.", Toast.LENGTH_SHORT).show();
            return;
        }
        String userEmail = currentUser.getEmail();
        if (userEmail == null) {
            Toast.makeText(this, "User email not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot enterpriseSnap : snapshot.getChildren()) {
                    DataSnapshot adminSnap = enterpriseSnap.child("Admin");
                    if (adminSnap.exists()) {
                        for (DataSnapshot adminUserSnap : adminSnap.getChildren()) {
                            DataSnapshot accountSettingsSnap = adminUserSnap.child("accountSettings");
                            if (accountSettingsSnap.exists()) {
                                String emailInDb = accountSettingsSnap.child("adminEmail").getValue(String.class);
                                if (emailInDb != null && emailInDb.equalsIgnoreCase(userEmail)) {
                                    enterpriseName = enterpriseSnap.getKey();    // e.g., "Bad Dog"
                                    adminUsername = adminUserSnap.getKey();       // e.g., "Tonia"
                                    found = true;
                                    break;
                                }
                            }
                        }
                        if (found) break;
                    }
                }
                if (!found) {
                    Toast.makeText(Admin_Staff_Settings.this,
                            "Could not find matching Admin for your email in DB.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Log.d("Admin_Staff_Settings", "Admin info loaded: enterprise=" +
                            enterpriseName + ", admin=" + adminUsername);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Admin_Staff_Settings.this,
                        "DB Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupSpinners() {
        List<String> roles = new ArrayList<>();
        roles.add("Select role"); // hint
        roles.addAll(Arrays.asList(getResources().getStringArray(R.array.dropdown_items)));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, roles) {
            @Override
            public boolean isEnabled(int position) {
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
        spinner_add.setAdapter(adapter);
        if (spinner_delete != null) {
            spinner_delete.setAdapter(adapter);
        }
    }

    // Updated addStaffMember: Now checks the whole enterprise for duplicate email.
    private void addStaffMember() {
        String staffEmail = addemail.getText().toString().trim();
        String selectedRole = spinner_add.getSelectedItem().toString().trim();

        if (staffEmail.isEmpty() || "Select role".equals(selectedRole)) {
            Toast.makeText(this, getString(R.string.enter_email_select_role), Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(staffEmail).matches()) {
            Toast.makeText(this, getString(R.string.error_invalid_email), Toast.LENGTH_SHORT).show();
            return;
        }
        // Check for Greek letters in email
        if (staffEmail.matches(".*[\\u0370-\\u03FF].*")) {
            Toast.makeText(this, getString(R.string.email_no_greek_chars), Toast.LENGTH_SHORT).show();
            return;
        }

        // Mapping dictionary: Convert Greek role to English.
        Map<String, String> roleMapping = new HashMap<>();
        roleMapping.put("Διαχειριστής", "Admin");
        roleMapping.put("Σερβιτόρος/Σερβιτόρα", "Waiter");
        roleMapping.put("Μπαριστα/Μπαρμαν/Μπαργουμαν", "Barista-Barman-Barwoman");
        roleMapping.put("Μάγειρας/Μαγείρισσα/Αρχιμάγειρας", "Cook-Chef");

        String normalizedRole = roleMapping.containsKey(selectedRole) ? roleMapping.get(selectedRole) : selectedRole;

        // Check if Admin info is loaded
        if (enterpriseName == null || adminUsername == null) {
            Toast.makeText(this, "Admin info not loaded yet. Please wait or re-check DB structure.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check the entire enterprise for duplicate email in any Admin's staffSettings.
        DatabaseReference enterpriseAdminRef = mDatabase.child(enterpriseName).child("Admin");
        enterpriseAdminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean duplicateFound = false;
                for (DataSnapshot adminSnap : snapshot.getChildren()) {
                    DataSnapshot staffSettingsSnap = adminSnap.child("staffSettings");
                    if (staffSettingsSnap.exists()) {
                        for (DataSnapshot staffSnap : staffSettingsSnap.getChildren()) {
                            String existingEmail = staffSnap.child("email").getValue(String.class);
                            if (existingEmail != null && existingEmail.trim().equalsIgnoreCase(staffEmail)) {
                                duplicateFound = true;
                                break;
                            }
                        }
                    }
                    if (duplicateFound) break;
                }
                if (duplicateFound) {
                    Toast.makeText(Admin_Staff_Settings.this, getString(R.string.error_email_already_in_use), Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed to add the staff member under the current Admin's staffSettings.
                    Map<String, Object> staffMap = new HashMap<>();
                    staffMap.put("email", staffEmail);
                    staffMap.put("role", normalizedRole);

                    DatabaseReference adminRef = mDatabase.child(enterpriseName)
                            .child("Admin")
                            .child(adminUsername)
                            .child("staffSettings");
                    String newStaffKey = adminRef.push().getKey();
                    adminRef.child(newStaffKey)
                            .setValue(staffMap)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Admin_Staff_Settings.this, "Staff member added!", Toast.LENGTH_SHORT).show();
                                    addemail.setText("");
                                    spinner_add.setSelection(0);
                                } else {
                                    Toast.makeText(Admin_Staff_Settings.this,
                                            "Error: " + task.getException().getMessage(),
                                            Toast.LENGTH_LONG).show();
                                    Log.e("Admin_Staff_Settings", "Add staff error", task.getException());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Admin_Staff_Settings.this,
                        "DB Error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeStaffMember() {
        if (spinner_delete == null) {
            Toast.makeText(this, "Remove spinner not found.", Toast.LENGTH_SHORT).show();
            return;
        }
        String staffEmail = addemail.getText().toString().trim();
        String selectedRole = spinner_delete.getSelectedItem().toString().trim();

        if (staffEmail.isEmpty() || "Select role".equals(selectedRole)) {
            Toast.makeText(this, "Please enter email and select a role to remove.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> roleMapping = new HashMap<>();
        roleMapping.put("Διαχειριστής", "Admin");
        roleMapping.put("Σερβιτόρος/Σερβιτόρα", "Waiter");
        roleMapping.put("Μπαριστα/Μπαρμαν/Μπαργουμαν", "Barista-Barman-Barwoman");
        roleMapping.put("Μάγειρας/Μαγείρισσα/Αρχιμάγειρας", "Cook-Chef");
        String normalizedRole = roleMapping.containsKey(selectedRole) ? roleMapping.get(selectedRole) : selectedRole;

        if (enterpriseName == null || adminUsername == null) {
            Toast.makeText(this, "Admin info not loaded yet. Please wait or re-check DB structure.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference staffSettingsRef = mDatabase.child(enterpriseName)
                .child("Admin")
                .child(adminUsername)
                .child("staffSettings");

        staffSettingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean found = false;
                for (DataSnapshot staffSnap : snapshot.getChildren()) {
                    String emailInDb = staffSnap.child("email").getValue(String.class);
                    String roleInDb = staffSnap.child("role").getValue(String.class);
                    if (emailInDb != null && roleInDb != null) {
                        if (emailInDb.equalsIgnoreCase(staffEmail) &&
                                roleInDb.equalsIgnoreCase(normalizedRole)) {
                            staffSnap.getRef().removeValue();
                            found = true;
                            Toast.makeText(Admin_Staff_Settings.this, "Staff member removed.", Toast.LENGTH_SHORT).show();
                            addemail.setText("");
                            spinner_delete.setSelection(0);
                            break;
                        }
                    }
                }
                if (!found) {
                    Toast.makeText(Admin_Staff_Settings.this, "No matching staff found for removal.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Admin_Staff_Settings.this, "DB Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Admin_Staff_Settings", "Remove staff error", error.toException());
            }
        });
    }
}
