package com.example.gowaiter.MainMenu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.gowaiter.R;

public class Get_Started extends AppCompatActivity {

    TextView text_GoWaiter;
    ImageButton getStarted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_started);

        text_GoWaiter = findViewById(R.id.textView_GoWaiter);
        getStarted = findViewById(R.id.imageButton_getStarted);

        //Show movement in Welcome to GoWaiter text when the app starts
        YoYo.with(Techniques.Landing).duration(3000).repeat(0).playOn(text_GoWaiter);

        //click Get started button
        getStarted.setOnClickListener(v -> startActivity(new Intent(Get_Started.this, Sign_in.class)));
    }
}