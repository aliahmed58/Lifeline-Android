package com.main.frontend.activities.driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.frontend.R;
import com.main.frontend.activities.user.CallAmbulanceCancel;
import com.main.frontend.entity.AmbulanceOrder;
import com.main.frontend.entity.Driver;
import com.main.frontend.entity.User;

import org.w3c.dom.Document;

public class ReqAccepted extends AppCompatActivity {

    private Button cancel;
    private Button goToHome;

    TextView name;
    TextView cellphone;
    TextView spec;
    TextView paymentMethod;
    TextView location;

    private AmbulanceOrder order;
    private User user;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_request_individual);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        name = findViewById(R.id.userNameTextView);
        cellphone = findViewById(R.id.userPhoneTextView);
        spec = findViewById(R.id.specTextView);
        paymentMethod = findViewById(R.id.paymentTextView);
        location = findViewById(R.id.LocationTextView);

        cancel = findViewById(R.id.cancelAmbulance);
        goToHome = findViewById(R.id.goHome);

        getOrder();
        addListeners();
        checkUpdates();
    }

    private void getOrder() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            order = (AmbulanceOrder) bundle.getSerializable("order");
        }
    }

    private void fetchUser() {
        if (user == null) {
            if (order != null) {

                DocumentReference docRef = db.collection("users").document(order.getUserid());
                docRef.get().addOnSuccessListener(documentSnapshot -> {
                    User user = documentSnapshot.toObject(User.class);
                    name.setText(user.getName());
                }).addOnFailureListener(e -> {
                    Log.w("FetchUser", e);
                });
            }
        }
    }

    private void setFields() {
        if (order != null) {
            cellphone.setText(order.getUserid());
            spec.setText(order.getSpec());
            paymentMethod.setText(order.getPaymentMethod());
            location.setText(order.getLocation());
            if (user != null) {
                name.setText(user.getName());
            }
        }
    }

    private void checkUpdates() {
        if (order != null) {
            final DocumentReference docRef = db.collection("ambulanceOrders").document(order.getOrderId());
            docRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    Log.w("ReqAccepted", e);
                    return;
                }
                AmbulanceOrder order = null;
                if (snapshot != null && snapshot.exists()) {
                    order = snapshot.toObject(AmbulanceOrder.class);
                }
                if (order != null) {
                    fetchUser();
                    setFields();
                }else {
                    // go to homepage if order is null vlaue
                    Intent i = new Intent(this, DriverHomepage.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }

            });
        }

    }

    private void addListeners() {
        goToHome.setOnClickListener(v -> {
            Intent i = new Intent(this, DriverHomepage.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });

        cancel.setOnClickListener(view -> {
           if (order != null) {
               db.collection("ambulanceOrders").document(order.getOrderId())
                       .delete()
                       .addOnSuccessListener(unused -> {
                           Toast.makeText(this, "Request cancelled", Toast.LENGTH_SHORT).show();
                           Log.d("Wait", "request cancelled");
                           Intent i = new Intent(this, DriverReqCancel.class);
                           startActivity(i);
                       })
                       .addOnFailureListener(e -> {
                           Log.w("Wait", "Error deleting", e);
                       });
           }
        });
    }

}