package com.main.frontend.activities.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.frontend.R;
import com.main.frontend.entity.AmbulanceOrder;
import com.main.frontend.entity.BedOrder;
import com.main.frontend.entity.User;

import java.util.Locale;
import java.util.UUID;

public class RequestHospitalBed extends AppCompatActivity {

    TextView specs;
    TextView age;
    Button addBooking;

    FirebaseFirestore db;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_hospital_bed);

        specs = findViewById(R.id.typeSpec);
        age = findViewById(R.id.typeAge);
        addBooking = findViewById(R.id.addBooking);

        db = FirebaseFirestore.getInstance();

        setUser();
        Toast.makeText(this, user.getName(), Toast.LENGTH_SHORT).show();
        handleProceed();
    }

    private void setUser() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                user = (User) extras.getSerializable("user");
            }
        }
    }

    private void handleProceed() {
        addBooking.setOnClickListener(view -> {
            if (validationChecks()) {
                if (user != null) {
                    // add to db proceed to waiting screen
                    BedOrder order = new BedOrder(
                            UUID.randomUUID().toString(),
                            specs.getText().toString(),
                            user.getPhone().toString(),
                            false,
                            Integer.valueOf(age.getText().toString()),
                            "");
                    db.collection("bedOrders")
                            .document(order.getOrderId())
                            .set(order)
                            .addOnSuccessListener(documentReference -> {
                                // go to waiting screen if added successfully
                                Intent i = new Intent(view.getContext(), BedReqAccepted.class);
                                i.putExtra("order", order);
                                startActivity(i);
                            })
                            .addOnFailureListener(e -> {
                                Log.w("CallAmbulance", "Error adding order", e);
                            });
                }
            }
        });
    }

    private boolean validationChecks() {
        boolean success = true;
        if (specs.getText().length() == 0) {
            success = false;
            Toast.makeText(this, "Enter specialization", Toast.LENGTH_LONG).show();
        }


        if (age.getText().length() == 0) {
            success = false;
            Toast.makeText(this, "Please input age", Toast.LENGTH_SHORT).show();
        }
        return success;
    }
}