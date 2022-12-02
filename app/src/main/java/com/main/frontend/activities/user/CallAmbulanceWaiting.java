package com.main.frontend.activities.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.main.frontend.R;
import com.main.frontend.entity.AmbulanceOrder;

import org.checkerframework.checker.units.qual.A;

public class CallAmbulanceWaiting extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Button cancelBtn;

    private AmbulanceOrder order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_ambulance2);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        cancelBtn = findViewById(R.id.cancelFindAmbulance);

        getOrder();
        handleCancelReq();
        checkAcceptance();
    }
    private void getOrder() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            order =(AmbulanceOrder) bundle.getSerializable("order");
        }
        else {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                db.collection("ambulanceOrders")
                        .whereEqualTo("userid", user.getPhoneNumber())
                        .get()
                        .addOnCompleteListener(task -> {
                           if (task.isSuccessful()) {
                               for (DocumentSnapshot doc : task.getResult()) {
                                   order = doc.toObject(AmbulanceOrder.class);
                               }
                           }
                           else {
                               Log.w("Wait", "Error fetching", task.getException());
                           }
                        });
            }
        }
    }

    private void handleCancelReq() {
        cancelBtn.setOnClickListener(view -> {
            if (order != null) {
                db.collection("ambulanceOrders").document(order.getOrderId())
                        .delete()
                        .addOnSuccessListener(unused -> {
                           Log.d("Wait", "request cancelled");
                           Intent i = new Intent(this, CallAmbulanceCancel.class);
                           startActivity(i);
                        })
                        .addOnFailureListener(e -> {
                            Log.w("Wait", "Error deleting", e);
                        });
            }
        });
    }

    private void checkAcceptance() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            db.collection("ambulanceOrders")
                    .whereEqualTo("userid", user.getPhoneNumber())
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Log.w("Wait", error);
                            return;
                        }
                        else {
                            AmbulanceOrder order = null;
                            for (QueryDocumentSnapshot doc : value) {
                                if (doc.get("driverId") != "") {
                                    Log.d("DRIVERID", (String) doc.get("driverId"));
                                    order = doc.toObject(AmbulanceOrder.class);
                                }
                            }
                            if (order != null) {
                                //Toast.makeText(this, "Request accepted by: " + order.getDriverId(), Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(this, CallAmbulanceFound.class);
                                i.putExtra("order", order);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.putExtra("driverid", order.getDriverId());
                                startActivity(i);
                            }

                        }
                    });
        }
    }
}