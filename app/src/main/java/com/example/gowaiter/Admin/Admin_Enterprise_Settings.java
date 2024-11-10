package com.example.gowaiter.Admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.gowaiter.R;

public class Admin_Enterprise_Settings extends AppCompatActivity {

    Button tables, menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_enterprise_settings);

        tables = findViewById(R.id.button_modify_tables);
        menu = findViewById(R.id.button_modify_menu);

        tables.setOnClickListener(v -> startActivity(new Intent(Admin_Enterprise_Settings.this, Admin_Modify_Tables.class)));
        menu.setOnClickListener(v -> startActivity(new Intent(Admin_Enterprise_Settings.this, Admin_Modify_Menu.class)));
    }
}
