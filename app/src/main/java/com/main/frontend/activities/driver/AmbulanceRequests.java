package com.main.frontend.activities.driver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.main.frontend.R;
import com.main.frontend.adapters.AmbRequestAdapter;
import com.main.frontend.entity.Ambulance;
import com.main.frontend.entity.AmbulanceOrder;
import com.main.frontend.entity.Driver;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class AmbulanceRequests extends AppCompatActivity {

    private ListView listView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private Driver driver;

    AmbRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ambulance_requests);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        listView = findViewById(R.id.ambulanceReqListView);

        getUserObject();
        seeUpdates();
        checkReqExists();
    }

    private void checkReqExists() {
        if (driver != null) {
            db.collection("ambulanceOrders")
                    .whereEqualTo("driverId", driver.getPhone())
                    .get()
                    .addOnCompleteListener(task -> {
                       if (task.isSuccessful()) {
                           AmbulanceOrder order = null;
                           for (DocumentSnapshot doc : task.getResult()) {
                               order = doc.toObject(AmbulanceOrder.class);
                           }

                           if (order != null) {
                               Intent i = new Intent(this, ReqAccepted.class);
                               i.putExtra("order", order);
                               startActivity(i);
                               finish();;
                           }
                       }
                       else {
                           Log.w("Error", "Error fetching user");
                       }
                    });
        }
    }

    private void seeUpdates() {
        if (driver != null) {
            db.collection("ambulanceOrders")
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Log.w("ORDER", "Listen failed", error);
                            return;
                        }
                        ArrayList<AmbulanceOrder> orders = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("userid") != null) {
                                if (!doc.getBoolean("accepted")) {
                                    orders.add(doc.toObject(AmbulanceOrder.class));
                                }
                            }
                        }
                        adapter = new AmbRequestAdapter(this, R.layout.driver_requests_adapter, orders);
                        listView.setAdapter(adapter);
                    });
        }
    }


    private void getUserObject() {
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            driver = (Driver) extras.getSerializable("user");
        }
    }
}