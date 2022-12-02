package com.main.frontend.activities.driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.main.frontend.R;

public class DriverReqCancel extends AppCompatActivity {

    private Button cancelBooking;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_cancel);


        cancelBooking = findViewById(R.id.cancelBooking);

        cancelBooking.setOnClickListener(view -> {
            Toast.makeText(this, "Request cancelled", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, DriverHomepage.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }
}