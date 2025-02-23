package com.example.gowaiter.MainMenu;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.gowaiter.R;

public class Get_Started extends AppCompatActivity {

    TextView text_GoWaiter, getStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if this is the first run
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);

        if (!isFirstRun) {
            // Not the first run, so go directly to the Sign_in activity
            startActivity(new Intent(Get_Started.this, Sign_in.class));
            finish();  // Finish this activity so it's removed from the back stack
            return;
        }

        // Set the content view if it is the first run
        setContentView(R.layout.activity_get_started);

        // Update the flag so that this activity doesn't show again
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isFirstRun", false);
        editor.apply();

        // Initialize views
        text_GoWaiter = findViewById(R.id.textView_GoWaiter);
        getStarted = findViewById(R.id.button_get_started);

        // Start an animation on the welcome text
        YoYo.with(Techniques.Landing)
                .duration(3000)
                .repeat(0)
                .playOn(text_GoWaiter);

        // Set up the Get Started button to navigate to Sign_in activity
        getStarted.setOnClickListener(v -> {
            startActivity(new Intent(Get_Started.this, Sign_in.class));
            finish();  // Optionally finish this activity after launching the next one
        });
    }
}
