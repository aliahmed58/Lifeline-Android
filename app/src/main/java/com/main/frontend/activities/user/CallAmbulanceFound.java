package com.main.frontend.activities.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.main.frontend.R;
import com.main.frontend.activities.driver.DriverHomepage;
import com.main.frontend.entity.Ambulance;
import com.main.frontend.entity.AmbulanceOrder;
import com.main.frontend.entity.Driver;

public class CallAmbulanceFound extends AppCompatActivity {

    private TextView driverNameTextView;
    private TextView driverCellTextView;
    private TextView ambNumberPlate;

    private Button cancelAmbulance;
    private Button goToHome;

    private String driverid;
    private FirebaseFirestore db;

    private AmbulanceOrder order;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_ambulance3);

        db = FirebaseFirestore.getInstance();

        driverNameTextView = findViewById(R.id.driverNameTextView);
        driverCellTextView = findViewById(R.id.driverCellTextView);
        ambNumberPlate = findViewById(R.id.ambNumberPlate);
        goToHome = findViewById(R.id.goToHome);
        cancelAmbulance = findViewById(R.id.cancelAmbulance);

        getOrder();
        setFields();
        checkUpdates();
        homeBtnListener();
        cancelAmbListener();
    }

    private void getOrder() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            order = (AmbulanceOrder) extras.getSerializable("order");
            driverid = extras.getString("driverid");
        }
    }

    private void homeBtnListener() {
        goToHome.setOnClickListener(view -> {
            Intent i = new Intent(this, UserHomepage.class);
            startActivity(i);
        });
    }


    private void cancelAmbListener() {
        cancelAmbulance.setOnClickListener(view -> {
            if (order != null) {
                db.collection("ambulanceOrders").document(order.getOrderId())
                        .delete()
                        .addOnSuccessListener(unused -> {
                            Log.d("Found", "request cancelled");
                            Intent i = new Intent(this, CallAmbulanceCancel.class);
                            startActivity(i);
                        })
                        .addOnFailureListener(e -> {
                            Log.w("Found", "Error deleting", e);
                        });
            }
        });
    }


    private void setFields() {
        if (order != null){
            if (driverid != null) {
                Log.d("DRIVERID", driverid);
                DocumentReference docRef = db.collection("users").document(driverid);
                docRef.get().addOnSuccessListener(documentSnapshot -> {
                    Driver driver = documentSnapshot.toObject(Driver.class);
                    if (driver != null) {
                        driverNameTextView.setText(driver.getName());
                        driverCellTextView.setText(driver.getPhone());

                        DocumentReference ambRef = db.collection("ambulances").document(driver.getAmbID());
                        ambRef.get().addOnSuccessListener(dS -> {
                            Ambulance a = dS.toObject(Ambulance.class);
                            if (a != null) {
                                ambNumberPlate.setText(a.getNumberPlate());
                            }
                        }).addOnFailureListener(e -> {
                            Log.w("Found", "Error fetcing ambulance", e);
                        });
                    }
                }).addOnFailureListener(e -> {
                    Log.w("Found", "Error fetching user", e);
                });
            }
            else {
                Toast.makeText(this, "Error occured, please try again", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkUpdates() {
        final DocumentReference docRef = db.collection("ambulanceOrders").document(order.getOrderId());
        docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("ReqAccepted", e);
                return;
            }
            AmbulanceOrder order = null;
            if (snapshot != null && snapshot.exists()) {
                order = snapshot.toObject(AmbulanceOrder.class);
                assert order != null;
                Log.d("ORDER", order.getUserid());
            }
            if (order != null) {
                setFields();
            }
            else {
                Toast.makeText(this, "Order cancelled by user", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, UserHomepage.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        });

    }


}