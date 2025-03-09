package com.example.gowaiter.BaristaBarman;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.gowaiter.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

public class Barista_Barman_Training_and_Support extends AppCompatActivity {

    ImageButton btnBack;
    CardView cardViewPdf; // CardView that will trigger the PDF open action

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barista_barman_training_and_support);

        btnBack = findViewById(R.id.btnBack);
        cardViewPdf = findViewById(R.id.card_barista_guide);  // Make sure this ID exists in your layout

        if (btnBack != null) {
            btnBack.setOnClickListener(view -> onBackPressed());
        }

        // Set click listener for the CardView
        cardViewPdf.setOnClickListener(v -> openPdfFromStorage());
    }

    private void openPdfFromStorage() {
        // Determine the correct PDF path based on the current locale
        Locale currentLocale = getResources().getConfiguration().locale;
        String pdfPath;
        if (currentLocale.getLanguage().equals("el")) {
            // Greek locale
            pdfPath = "Barista Guide/Barista_training_and_support_gr.pdf";
        } else {
            // Default to English
            pdfPath = "Barista Guide/Barista_training_and_support_en.pdf";
        }

        // Reference the file in Firebase Storage
        StorageReference pdfRef = FirebaseStorage.getInstance().getReference().child(pdfPath);
        pdfRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Get the download URL and open it via an Intent
            String downloadUrl = uri.toString();
            openPdfWithIntent(downloadUrl);
        }).addOnFailureListener(e -> {
        });
    }

    private void openPdfWithIntent(String pdfUrl) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(pdfUrl), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivity(intent);
        } catch (Exception e) {}
    }
}

