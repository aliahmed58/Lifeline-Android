package com.main.frontend.activities.common;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.frontend.R;
import com.main.frontend.activities.MainActivity;
import com.main.frontend.activities.driver.AddAmbulance;
import com.main.frontend.entity.User;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    public ProfileFragment() {
        // Required empty public constructor
    }

    private TextView userName;
    private TextView userPhone;
    private TextView userAccountType;
    private Button logOutBtn;
    private Button addAmbBtn;

    private FirebaseFirestore db;

    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        userName = view.findViewById(R.id.userNameTextView);
        userPhone = view.findViewById(R.id.userPhoneTextView);
        userAccountType = view.findViewById(R.id.userAccountTypeTextView);
        logOutBtn = view.findViewById(R.id.logOutBtn);
        addAmbBtn = view.findViewById(R.id.addAmbBtn);

        db = FirebaseFirestore.getInstance();
        setProfile(view);
        logOutUser();
        ambBtnListener();

        return view;
    }

    private void logOutUser() {
        logOutBtn.setOnClickListener(view -> {
            FirebaseUser authAcc = FirebaseAuth.getInstance().getCurrentUser();
            if (authAcc != null) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(view.getContext(), MainActivity.class);
                startActivity(i);
                if (getActivity() != null) getActivity().finish();
            }
        });
    }

    private void ambBtnListener() {
        if (addAmbBtn.isEnabled()) {
            addAmbBtn.setOnClickListener(view -> {
                Intent i = new Intent(view.getContext(), AddAmbulance.class);
                startActivity(i);
            });
        }
    }

    private void setProfile(View view) {
        FirebaseUser authAcc = FirebaseAuth.getInstance().getCurrentUser();
        if (authAcc != null) {
            if (user == null) {
                DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(authAcc.getPhoneNumber()));
                docRef.get().addOnSuccessListener(dc -> {
                   user = dc.toObject(User.class);
                   if (user != null) {
                       setFields();
                       setAmbBtnVisibility();
                   }
                });
            }
            else {
                setFields();
                setAmbBtnVisibility();
            }
        }
    }

    private void setAmbBtnVisibility() {
        if (user.getUserType().equals("driver")) {
            addAmbBtn.setVisibility(View.VISIBLE);
            addAmbBtn.setEnabled(true);
        }
        else {
            addAmbBtn.setEnabled(false);
            addAmbBtn.setVisibility(View.GONE);
        }
    }

    private void setFields() {
        userName.setText(user.getName());
        userPhone.setText(user.getPhone());
        userAccountType.setText(user.getUserType());
    }
}