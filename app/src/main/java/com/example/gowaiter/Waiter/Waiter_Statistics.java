package com.example.gowaiter.Waiter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.gowaiter.R;

public class Waiter_Statistics extends AppCompatActivity {

    private Spinner spinnerContent;
    private Spinner spinnerFilters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_statistics);

        // Find the Spinners
        spinnerContent = findViewById(R.id.spinner_content_waiter);
        spinnerFilters = findViewById(R.id.spinner_filters_waiter);

        // Set up the adapter for spinnerContent with "content_options" array
        ArrayAdapter<CharSequence> contentAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.content_options,
                android.R.layout.simple_spinner_item
        );
        contentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerContent.setAdapter(contentAdapter);

        // Set up the adapter for spinnerFilters with "filter_options" array
        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.filter_options,
                android.R.layout.simple_spinner_item
        );
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilters.setAdapter(filterAdapter);


        spinnerContent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedContent = parent.getItemAtPosition(position).toString();
                // Do something with the selected content
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do something when nothing is selected (optional)
            }
        });

        spinnerFilters.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFilter = parent.getItemAtPosition(position).toString();
                // Do something with the selected filter
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do something when nothing is selected (optional)
            }
        });
    }
}