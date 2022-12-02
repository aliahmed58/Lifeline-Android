package com.main.frontend.activities.driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.frontend.R;
import com.main.frontend.entity.Ambulance;

import org.checkerframework.checker.units.qual.A;

import java.util.Locale;
import java.util.Objects;

public class AddAmbulance extends AppCompatActivity {

    private EditText numberPlateEditText;
    private RadioGroup ambTypes;
    private Button addAmbulance;

    private String ambType;
    private FirebaseFirestore db;
    private String ambId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ambulance);

        db = FirebaseFirestore.getInstance();
        numberPlateEditText = findViewById(R.id.numberPlateEditText);
        ambTypes = findViewById(R.id.ambulanceTypes);
        addAmbulance = findViewById(R.id.addAmbulanceBtn);

        addAmbulance();
    }

    private void clearActivity() {
        this.finish();
    }

    private void addAmbulance() {
        getAmbulanceType();
        addAmbulance.setOnClickListener(view -> {
            if (numberPlateEditText.getText().length() != 0 && ambType != null) {
                Ambulance a = new Ambulance(numberPlateEditText.getText().toString(), ambType);
                createAmbulance(a);
            }
        });
    }

    private void updateUser(String id) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(user.getPhoneNumber()));
            docRef.update("ambID", id)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getBaseContext(), "User updated", Toast.LENGTH_SHORT).show();
                        clearActivity();
                    })
                    .addOnFailureListener(e -> {
                        Log.d("Add Ambulance", "Error updating document", e);
                    });
        }
    }


    private void createAmbulance(Ambulance a){
        db.collection("ambulances").add(a)
                .addOnSuccessListener(docRef -> {
                    Log.d("Add Ambulance", docRef.getId());
                    ambId = docRef.getId();
                    updateUser(ambId);
                })
                .addOnFailureListener(e -> {Log.w("Add Ambulance", "Failed", e);
                });
    }

    private void getAmbulanceType() {
        ambTypes.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton r = radioGroup.findViewById(i);
            boolean isChecked = r.isChecked();
            if (isChecked) {
                ambType = r.getText().toString().toLowerCase(Locale.ROOT);
            }
        });
    }
}