package com.example.gowaiter.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.gowaiter.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class Admin_Supplies extends AppCompatActivity {

    ImageButton btnBack;
    private EditText supplyNameInput, supplyQuantityInput;
    private Spinner roleSpinner;
    private Button addSupplyButton;
    private RecyclerView requestsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_supplies);

        // back button activity
        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(view -> onBackPressed());
        }

        TabLayout tabLayout = findViewById(R.id.tabLayout_supplies_admin);
        requestsRecyclerView = findViewById(R.id.recyclerView_supplies);

        View cardViewAdd = findViewById(R.id.cardView_add_supply);
        View cardViewRemove = findViewById(R.id.cardView_remove_supply);
        View cardViewUpdate = findViewById(R.id.cardView_update_supply);
        View cardViewLowStock = findViewById(R.id.cardView_low_stock_notification);
        View cardViewSupplies =  findViewById(R.id.cardView_view_supplies_admin);

        // Set up RecyclerView
        setupRecyclerView();

        // Initialize UI for the default tab (View Supplies)
        cardViewSupplies.setVisibility(View.VISIBLE);
        cardViewAdd.setVisibility(View.GONE);
        cardViewRemove.setVisibility(View.GONE);
        cardViewUpdate.setVisibility(View.GONE);
        cardViewLowStock.setVisibility(View.GONE);
        requestsRecyclerView.setVisibility(View.GONE);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                if(tab.getText().equals("View Supplies") || tab.getText().equals("Εμφάνιση Προμηθειών")){
                    // Hide CardViews
                    cardViewSupplies.setVisibility(View.VISIBLE);
                    cardViewAdd.setVisibility(View.GONE);
                    cardViewRemove.setVisibility(View.GONE);
                    cardViewUpdate.setVisibility(View.GONE);
                    cardViewLowStock.setVisibility(View.GONE);

                    // Show RecyclerView
                    requestsRecyclerView.setVisibility(View.GONE);
                } else if (tab.getText().equals("Requests") || tab.getText().equals("Αιτήματα")){
                    // Hide CardViews
                    cardViewAdd.setVisibility(View.GONE);
                    cardViewRemove.setVisibility(View.GONE);
                    cardViewUpdate.setVisibility(View.GONE);
                    cardViewLowStock.setVisibility(View.GONE);
                    cardViewSupplies.setVisibility(View.GONE);

                    // Show RecyclerView
                    requestsRecyclerView.setVisibility(View.VISIBLE);
                } else if (tab.getText().equals("Manage Supplies") || tab.getText().equals("Διαχείρηση Προμηθειών")){
                    // Show CardViews
                    cardViewAdd.setVisibility(View.VISIBLE);
                    cardViewRemove.setVisibility(View.VISIBLE);
                    cardViewUpdate.setVisibility(View.VISIBLE);
                    cardViewLowStock.setVisibility(View.VISIBLE);
                    cardViewSupplies.setVisibility(View.GONE);

                    // Hide RecyclerView
                    requestsRecyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onTabUnselected(@NonNull TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(@NonNull TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        // Initialize RecyclerView
        requestsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        requestsRecyclerView.setVisibility(View.GONE); // Initially hidden

        // Sample data for RecyclerView
        List<String> sampleRequests = new ArrayList<>();
        sampleRequests.add("Request 1");
        sampleRequests.add("Request 2");
        sampleRequests.add("Request 3");

        // Set adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sampleRequests);
        RecyclerView.Adapter recyclerAdapter = new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return null;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            }

            @Override
            public int getItemCount() {
                return 0;
            }
        };
        requestsRecyclerView.setAdapter(recyclerAdapter);
    }

        /**
        supplyNameInput = findViewById(R.id.add_supply_name);
        supplyQuantityInput = findViewById(R.id.add_supply_quantity);
        roleSpinner = findViewById(R.id.role_spinner);
        addSupplyButton = findViewById(R.id.add_supply_button);

        // Populate roles in the spinner
        List<String> roles = new ArrayList<>();
        roles.add("Waiter");
        roles.add("Barista");
        roles.add("Chef");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        // Handle Add Supply Button Click
        addSupplyButton.setOnClickListener(v -> {
            String supplyName = supplyNameInput.getText().toString().trim();
            String supplyQuantity = supplyQuantityInput.getText().toString().trim();
            String selectedRole = roleSpinner.getSelectedItem().toString();

            if (supplyName.isEmpty() || supplyQuantity.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                addSupplyToDatabase(supplyName, supplyQuantity, selectedRole);
            }
        });
    }

    private void addSupplyToDatabase(String name, String quantity, String role) {
        // Example: Add supply to database or backend
        Toast.makeText(this, "Supply Added: " + name + " (" + quantity + " units) for " + role, Toast.LENGTH_SHORT).show();

        // Clear inputs
        supplyNameInput.setText("");
        supplyQuantityInput.setText("");
        roleSpinner.setSelection(0);
    }
    **/
}