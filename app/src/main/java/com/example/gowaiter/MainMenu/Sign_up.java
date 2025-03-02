package com.example.gowaiter.MainMenu;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gowaiter.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Elements
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        username = findViewById(R.id.editText_username);
        enterprise_name = findViewById(R.id.editText_enterprise);
        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        sing_up = findViewById(R.id.button_sign_up);
        spinner = findViewById(R.id.spinner_role);

        // Sign up button click
        sing_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        // Create a list with the hint and the items
        List<String> roles = new ArrayList<>();
        roles.add(getString(R.string.select_role_hint)); // Use the localized hint
        roles.addAll(Arrays.asList(getResources().getStringArray(R.array.dropdown_items)));

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, roles) {
            @Override
            public boolean isEnabled(int position) {
                // Disable the first item (hint)
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                // Set the hint text color
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedRole = parent.getItemAtPosition(position).toString();
                    // Do something with the selected role if needed
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    private void registerUser() {
        // Get the text from EditTexts
        String usernameText = username.getText().toString().trim();
        String enterpriseText = enterprise_name.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String selectedRole = spinner.getSelectedItem().toString()
                .trim()
                .replace("/", "-");

        // Check if fields are filled
        if (usernameText.isEmpty() || enterpriseText.isEmpty() || emailText.isEmpty() ||
                passwordText.isEmpty() || selectedRole.equals("Select role")) {
            Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        // Create user with email and password in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User is created in Auth. We'll store details in the DB next.
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Create a Map for user data
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("username", usernameText);
                            userMap.put("enterprise", enterpriseText);
                            userMap.put("role", selectedRole);
                            userMap.put("email", emailText);

                            // Build: enterprise_name -> role -> username
                            mDatabase.child(enterpriseText)
                                    .child(selectedRole)
                                    .child(usernameText)
                                    .setValue(userMap)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(Sign_up.this,
                                                    getString(R.string.registration_successful),
                                                    Toast.LENGTH_SHORT).show();
                                            // TODO: redirect to another activity or finish()
                                        } else {
                                            Toast.makeText(Sign_up.this,
                                                    getString(R.string.error_storing_data) + ": "
                                                            + dbTask.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        // Registration in Auth failed
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
