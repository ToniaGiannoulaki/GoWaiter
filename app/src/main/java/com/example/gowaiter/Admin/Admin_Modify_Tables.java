package com.example.gowaiter.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.example.gowaiter.R;

public class Admin_Modify_Tables extends AppCompatActivity {

    ImageButton btnBack;
    RadioButton indoor, outdoor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_modify_tables);

        // back button activity
        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(view -> onBackPressed());
        }

        indoor = findViewById(R.id.radioButton_indoor);
        outdoor = findViewById(R.id.radioButton_outdoor);

        // Set radioButton_indoor as the default selected radio button
        indoor.setChecked(true);
    }
}
