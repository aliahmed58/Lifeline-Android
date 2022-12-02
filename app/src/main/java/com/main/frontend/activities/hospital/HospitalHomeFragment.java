package com.main.frontend.activities.hospital;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.frontend.R;
import com.main.frontend.activities.MainActivity;
import com.main.frontend.entity.Ambulance;
import com.main.frontend.entity.Driver;
import com.main.frontend.entity.User;

import java.util.Objects;

public class HospitalHomeFragment extends Fragment {

    private FirebaseFirestore db;

    private Driver driver;
    private Ambulance ambulance;

    private TextView welcomeMsgUser;
    private Button manageRequestBtn;

    User user;

    private static final String TAG = "DriverHomepage";

    public HospitalHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_driver_home, container, false);

        db = FirebaseFirestore.getInstance();
        welcomeMsgUser = view.findViewById(R.id.welcomeMsgUser);
        manageRequestBtn = view.findViewById(R.id.manageRequestsBtn);

        checkAuth(view);
        return view;
    }

    private void setManageRequestBtn() {

    }

    private void checkAuth(View view) {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (fbUser == null) {
            Intent i = new Intent(view.getContext(), MainActivity.class);
            startActivity(i);
        }
        else {
            if (user == null) {
                DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(fbUser.getPhoneNumber()));
                docRef.get().addOnSuccessListener(documentSnapshot -> {
                    user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        welcomeMsgUser.setText("Welcome, " + user.getName());
                    }
                });
            }
            else {
                welcomeMsgUser.setText("Welcome, " + user.getName());
            }
        }
    }
}