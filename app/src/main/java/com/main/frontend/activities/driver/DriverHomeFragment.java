package com.main.frontend.activities.driver;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.main.frontend.R;
import com.main.frontend.entity.Ambulance;
import com.main.frontend.entity.Driver;

import java.util.Objects;

public class DriverHomeFragment extends Fragment {

    private FirebaseFirestore db;

    private Driver driver;
    private Ambulance ambulance;

    private TextView ambType;
    private TextView welcomeMsgUser;
    private Button manageRequestBtn;

    private static final String TAG = "DriverHomepage";

    public DriverHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_home, container, false);

        db = FirebaseFirestore.getInstance();
        welcomeMsgUser = view.findViewById(R.id.welcomeMsgUser);
        ambType = view.findViewById(R.id.ambulanceTypeText);
        manageRequestBtn = view.findViewById(R.id.manageRequestsBtn);

        fetchUser(view);
        manageRequestListener(view);
        checkForUpdates();
        return view;
    }

    private void manageRequestListener(View parentView) {
        manageRequestBtn.setOnClickListener(view -> {
            if (ambulance != null) {
                Intent i = new Intent(parentView.getContext(), AmbulanceRequests.class);
                i.putExtra("user", driver);
                startActivity(i);
            }
            else {
                Toast.makeText(parentView.getContext(), "Please add an ambulance", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkForUpdates() {
        if (driver != null) {
            final DocumentReference docRef = db.collection("users").document(driver.getPhone());
            docRef.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
                if (snapshot != null && snapshot.exists()) {
                    driver = snapshot.toObject(Driver.class);
                    fetchAmbulance();
                } else {
                    Log.d(TAG, "Current data: null");
                }
            });
        }

    }

    private void fetchUser(View view) {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

        if (fbUser != null) {
            if (driver == null) {
                DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(fbUser.getPhoneNumber()));
                docRef.get()
                        .addOnSuccessListener(documentSnapshot -> {
                            driver = documentSnapshot.toObject(Driver.class);
                            if (driver != null) {
                                welcomeMsgUser.setText("Welcome, " + driver.getName());
                                if (ambulance == null) {
                                    fetchAmbulance();
                                }
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.w(TAG, "Error fetching user", e);
                        });
            }
            else {
                if (ambulance != null) {
                    ambType.setText(ambulance.getAmbulanceType());
                }
                else {
                    fetchAmbulance();
                }
                welcomeMsgUser.setText("Welcome, " + driver.getName());

            }
        }
    }

    private void fetchAmbulance() {
        if (driver != null) {
            if (driver.getAmbID() != null) {
                DocumentReference ambRef = db.collection("ambulances").document(driver.getAmbID());
                ambRef.get().addOnSuccessListener(docSnap -> {
                    ambulance = docSnap.toObject(Ambulance.class);
                    if (ambulance != null) {
                        ambType.setText(ambulance.getAmbulanceType());
                    }
                })
                        .addOnFailureListener(e -> {
                            Log.w(TAG, "Error fetching ambulance", e);
                        });
            }
        }
    }
}