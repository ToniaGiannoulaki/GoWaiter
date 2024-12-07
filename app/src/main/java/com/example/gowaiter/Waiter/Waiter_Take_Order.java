package com.example.gowaiter.Waiter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.example.gowaiter.R;


public class Waiter_Take_Order extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_take_order);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        TextView textViewChooseTable = findViewById(R.id.textView_choose_table);

        // Set the default text for the "Take Order" tab
        textViewChooseTable.setText("Choose a table to take an order");

        // Listener for tab changes
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: // Take Order tab
                        textViewChooseTable.setText("Choose a table to take an order");
                        break;
                    case 1: // Change Order tab
                        textViewChooseTable.setText("Choose a table to change an order");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing
            }
        });
    }
}