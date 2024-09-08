package com.example.gowaiter.Admin;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.gowaiter.R;

public class Admin_Modify_Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_modify_menu);

        ImageButton buttonShowDialog = findViewById(R.id.imageButton_add_menu_category);
        buttonShowDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog();
            }
        });
    }

    private void showInputDialog() {
        // Create an EditText widget programmatically
        final EditText input = new EditText(this);
        input.setHint("Type something...");

        // Create an AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Input Dialog")
                .setMessage("Please enter some text")
                .setView(input) // Set the EditText as the view of the dialog
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String enteredText = input.getText().toString();
                        // Do something with the entered text
                        Toast.makeText(Admin_Modify_Menu.this, "You entered: " + enteredText, Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                })
                .show();
    }
}