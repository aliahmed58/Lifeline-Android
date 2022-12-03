package com.main.frontend.activities.hospital;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.main.frontend.R;
import com.main.frontend.activities.driver.ReqAccepted;
import com.main.frontend.adapters.AmbRequestAdapter;
import com.main.frontend.adapters.BedRequestAdapter;
import com.main.frontend.entity.AmbulanceOrder;
import com.main.frontend.entity.BedOrder;
import com.main.frontend.entity.Driver;
import com.main.frontend.entity.User;

import java.util.ArrayList;

public class HospitalRequests extends AppCompatActivity {

    private ListView listView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private User user;

    BedRequestAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_requests);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        listView = findViewById(R.id.ambulanceReqListView);

        getUserObject();
        seeUpdates();
        checkReqExists();
    }

    private void checkReqExists() {
        if (user != null) {
            db.collection("bedOrders")
                    .whereEqualTo("hospId", user.getPhone())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            BedOrder order = null;
                            for (DocumentSnapshot doc : task.getResult()) {
                                order = doc.toObject(BedOrder.class);
                            }

                            if (order != null) {
                                //Intent i = new Intent(this, BedRequ.class);
                                //i.putExtra("order", order);
                                //startActivity(i);
                                //finish();;
                            }
                        }
                        else {
                            Log.w("Error", "Error fetching user");
                        }
                    });
        }
    }

    private void seeUpdates() {
        if (user != null) {
            db.collection("bedOrders")
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Log.w("ORDER", "Listen failed", error);
                            return;
                        }
                        ArrayList<BedOrder> orders = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("userId") != null) {
                                if (!doc.getBoolean("accepted")) {
                                    orders.add(doc.toObject(BedOrder.class));
                                }
                            }
                        }
                        adapter = new BedRequestAdapter(this, R.layout.bed_requests_layout, orders);
                        listView.setAdapter(adapter);
                    });
        }
    }


    private void getUserObject() {
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            user = (User) extras.getSerializable("user");
        }
    }
}