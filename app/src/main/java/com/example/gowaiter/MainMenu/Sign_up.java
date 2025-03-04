package com.example.gowaiter.MainMenu;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gowaiter.Loading.Loading_Screen;
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
    private boolean is_password_visible = false;
    private Loading_Screen loadingScreen = new Loading_Screen();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Elements for DB
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

        is_password_visible = false;

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
                            // Change the drawable to open lock
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
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    String selectedRole = parent.getItemAtPosition(position).toString();
                    // You can use the selected role if needed
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Callback when nothing is selected
            }
        });
    }

    private void registerUser() {
        // Get the text from EditTexts
        String usernameText = username.getText().toString().trim();
        String enterpriseText = enterprise_name.getText().toString().trim();
        String emailText = email.getText().toString().trim();
        String passwordText = password.getText().toString().trim();
        String selectedRole = spinner.getSelectedItem().toString().trim().replace("/", "-");

        // Check if fields are filled
        if (usernameText.isEmpty() || enterpriseText.isEmpty() || emailText.isEmpty() ||
                passwordText.isEmpty() || selectedRole.equals("Select role")) {
            Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if email contains any Greek characters (Unicode range for Greek and Coptic)
        if (emailText.matches(".*[\\u0370-\\u03FF].*")) {
            Toast.makeText(Sign_up.this, getString(R.string.email_no_greek_chars), Toast.LENGTH_SHORT).show();
            return;
        }

        // Show the loading screen before starting the registration process
        loadingScreen.show(this);

        // Create user with email and password in Firebase Authentication
        mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // User created in Auth; next, store details in the database
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();

                        // Clear the fields
                        username.setText("");
                        enterprise_name.setText("");
                        email.setText("");
                        password.setText("");
                        spinner.setSelection(0);

                        if (firebaseUser != null) {
                            // Prepare user data map
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("username", usernameText);
                            userMap.put("enterprise", enterpriseText);
                            userMap.put("role", selectedRole);
                            userMap.put("email", emailText);

                            // Structure: enterprise -> role -> username
                            mDatabase.child(enterpriseText)
                                    .child(selectedRole)
                                    .child(usernameText)
                                    .setValue(userMap)
                                    .addOnCompleteListener(dbTask -> {
                                        // Hide the loading screen once database operation completes
                                        loadingScreen.hide();
                                        if (dbTask.isSuccessful()) {
                                            Toast.makeText(Sign_up.this,
                                                    getString(R.string.registration_successful),
                                                    Toast.LENGTH_SHORT).show();
                                            // TODO: Redirect to another activity or finish this one
                                        } else {
                                            Toast.makeText(Sign_up.this,
                                                    getString(R.string.error_storing_data) + ": " +
                                                            dbTask.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Hide loading screen if FirebaseUser is null
                            loadingScreen.hide();
                        }
                    } else {
                        // Hide loading screen if registration failed
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
