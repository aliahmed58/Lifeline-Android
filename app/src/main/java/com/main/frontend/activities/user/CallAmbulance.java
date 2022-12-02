package com.main.frontend.activities.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.main.frontend.R;
import com.main.frontend.entity.AmbulanceOrder;
import com.main.frontend.entity.User;

import org.checkerframework.checker.units.qual.A;

import java.util.Locale;
import java.util.UUID;

public class CallAmbulance extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private EditText location;
    private EditText specEditText;
    private RadioGroup ambTypeGrp;
    private RadioGroup paymentMethodGrp;
    private Button findAmbulanceBtn;
    private Button cancelFind;

    private String ambulanceType;
    private String paymentMethod;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_ambulance);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        location = findViewById(R.id.locationEditText);
        specEditText = findViewById(R.id.callAmbulanceSpecialization);
        ambTypeGrp = findViewById(R.id.userCallAmbulanceTypes);
        paymentMethodGrp = findViewById(R.id.paymentGrp);
        cancelFind = findViewById(R.id.cancelFindAmbulance);
        findAmbulanceBtn = findViewById(R.id.findAmbulanceBtn);

        cancelFind.setOnClickListener(view -> {finish();});

        setUser();
        reqExists();
        handleProceed();
        setListeners();

    }

    private void reqExists() {
        if (user != null) {
            db.collection("ambulanceOrders")
                    .whereEqualTo("userid", user.getPhone())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // request already exists by this number
                            if (task.getResult().size() != 0) {

                                Toast.makeText(this, "Request exists", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(this, CallAmbulanceWaiting.class);
                                startActivity(i);
                                finish();
                            }
                        } else {
                            Log.d("CallAmbulance", "Error fetching request");
                        }
                    });
        }
    }

    private void setUser() {
        if (auth.getCurrentUser() != null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                user = (User) extras.getSerializable("user");
            }
        }
    }

    // TODO: IF request already exists, do not let user see this screen

    private void handleProceed() {
        findAmbulanceBtn.setOnClickListener(view -> {
            if (validationChecks()) {
                if (user != null) {
                    // add to db proceed to waiting screen
                    AmbulanceOrder order = new AmbulanceOrder(
                            ambulanceType,
                            location.getText().toString(),
                            UUID.randomUUID().toString(),
                            paymentMethod,
                            specEditText.getText().toString(),
                            user.getPhone(),
                            "",
                            false);
                    db.collection("ambulanceOrders")
                            .document(order.getOrderId())
                            .set(order)
                            .addOnSuccessListener(documentReference -> {
                                // go to waiting screen if added successfully
                                Intent i = new Intent(view.getContext(), CallAmbulanceWaiting.class);
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

    private void setListeners() {
        ambTypeGrp.setOnCheckedChangeListener(((radioGroup, i) -> {
            RadioButton checkedButton = (RadioButton) radioGroup.findViewById(i);
            if (checkedButton.isChecked()) {
                ambulanceType = checkedButton.getText().toString().toLowerCase(Locale.ROOT);
            }
        }));

        paymentMethodGrp.setOnCheckedChangeListener(((radioGroup, i) -> {
            RadioButton checkedButton = (RadioButton) radioGroup.findViewById(i);
            if (checkedButton.isChecked()) {
                paymentMethod = checkedButton.getText().toString().toLowerCase(Locale.ROOT);
            }
        }));
    }

    private boolean validationChecks() {
        boolean success = true;
        if (location.getText().length() == 0) {
            success = false;
            Toast.makeText(this, "Enter location", Toast.LENGTH_LONG).show();
        }


        if (ambulanceType == null) {
            success = false;
            Toast.makeText(this, "Please select ambulance type", Toast.LENGTH_SHORT).show();
        }

        if (paymentMethod == null) {
            success = false;
            Toast.makeText(this, "Please select payment method", Toast.LENGTH_SHORT).show();
        }

        return success;
    }

}