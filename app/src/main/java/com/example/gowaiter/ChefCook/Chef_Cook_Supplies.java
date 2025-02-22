package com.example.gowaiter.ChefCook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.gowaiter.BaristaBarman.Barista_Barman_Supplies;
import com.example.gowaiter.R;
import com.google.android.material.tabs.TabLayout;

public class Chef_Cook_Supplies extends AppCompatActivity {

    ImageButton btnBack;
    private TabLayout tabLayout;
    private RecyclerView recyclerViewSupplies;
    private CardView cardViewLowStock;
    private Button btnRequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chef_cook_supplies);

        // back button activity
        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(view -> onBackPressed());
        }

        // Initialize views
        tabLayout = findViewById(R.id.tabLayout_supplies_chef);
        recyclerViewSupplies = findViewById(R.id.recyclerView_view_supplies_chef);
        cardViewLowStock = findViewById(R.id.cardView_low_stock_chef);
        btnRequest = findViewById(R.id.button_low_stock_chef);

        // Tab selection listener
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    // View Supplies Tab
                    recyclerViewSupplies.setVisibility(View.VISIBLE);
                    cardViewLowStock.setVisibility(View.GONE);
                } else if (tab.getPosition() == 1) {
                    // Low Stock Notifications Tab
                    recyclerViewSupplies.setVisibility(View.GONE);
                    cardViewLowStock.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // No action needed
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // No action needed
            }
        });

        // Request Button Click Listener
        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the request button logic here
                Toast.makeText(Chef_Cook_Supplies.this, "Request sent!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}