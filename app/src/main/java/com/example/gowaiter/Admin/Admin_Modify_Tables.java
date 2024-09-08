package com.example.gowaiter.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.RadioButton;

import com.example.gowaiter.R;

public class Admin_Modify_Tables extends AppCompatActivity {

    RadioButton indoor, outdoor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_modify_tables);

        indoor = findViewById(R.id.radioButton_indoor);
        outdoor = findViewById(R.id.radioButton_outdoor);

        // Set radioButton_indoor as the default selected radio button
        indoor.setChecked(true);
    }
}