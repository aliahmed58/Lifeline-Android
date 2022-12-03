package com.main.frontend.activities.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.main.frontend.R;
import com.main.frontend.entity.AmbulanceOrder;
import com.main.frontend.entity.BedOrder;

public class BedReqAccepted extends AppCompatActivity {

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed_req_accepted);

        db = FirebaseFirestore.getInstance();
        Button button = findViewById(R.id.goHome);
        button.setOnClickListener(view -> {
            Intent i = new Intent(this, UserHomepage.class);
            startActivity(i);
            finish();
        });

        checkAcceptance();
    }

    private void checkAcceptance() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            db.collection("ambulanceOrders")
                    .whereEqualTo("userid", user.getPhoneNumber())
                    .addSnapshotListener((value, error) -> {
                        if (error != null) {
                            Log.w("Wait", error);
                            return;
                        }
                        else {
                            BedOrder order = null;
                            for (QueryDocumentSnapshot doc : value) {
                                if (doc.get("hospId") != "") {
                                    Log.d("DRIVERID", (String) doc.get("hospId"));
                                    order = doc.toObject(BedOrder.class);
                                }
                            }
                            if (order != null) {
                                //Toast.makeText(this, "Request accepted by: " + order.getDriverId(), Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(this, BedReqAccepted.class);
                                i.putExtra("order", order);
                                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                i.putExtra("driverid", order.getHospId());
                                startActivity(i);
                            }
                        }
                    });
        }
    }
}