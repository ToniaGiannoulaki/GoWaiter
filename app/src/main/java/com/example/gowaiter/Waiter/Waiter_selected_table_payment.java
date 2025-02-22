package com.example.gowaiter.Waiter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.example.gowaiter.R;

public class Waiter_selected_table_payment extends AppCompatActivity {

    ImageButton btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiter_selected_table_payment);

        // back button activity
        btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(view -> onBackPressed());
        }

        RadioGroup paymentMethodGroup = findViewById(R.id.payment_method_group);
        LinearLayout creditCardInfoLayout = findViewById(R.id.credit_card_info);

        paymentMethodGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.credit_card_option) {
                    // Show credit card fields
                    creditCardInfoLayout.setVisibility(View.VISIBLE);
                } else {
                    // Hide credit card fields
                    creditCardInfoLayout.setVisibility(View.GONE);
                }
            }
        });

    }
}