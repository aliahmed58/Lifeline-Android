package com.main.frontend.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.frontend.R;
import com.main.frontend.activities.driver.DriverHomepage;
import com.main.frontend.activities.hospital.HospitalHomepage;
import com.main.frontend.activities.user.UserHomepage;
import com.main.frontend.activities.verification.LoginPage;
import com.main.frontend.activities.verification.SignUpActivity;
import com.main.frontend.entity.User;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        Button signUpIntentButton = (Button) findViewById(R.id.signupHomePage);
        Button loginIntentButton = (Button) findViewById(R.id.loginHomePage);

        updateUI();

        signUpIntentButton.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(i);
        });

        loginIntentButton.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, LoginPage.class);
            startActivity(i);
        });
    }

    private void updateUI() {
        FirebaseUser user = auth.getCurrentUser();

        if (user != null) {
            DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(user.getPhoneNumber()));
            docRef.get().addOnSuccessListener(documentSnapshot ->  {
                User dbUser = documentSnapshot.toObject(User.class);
                if (dbUser != null)
                    showUserTypeUI(dbUser.getUserType());
            });
        }
    }

    private void showUserTypeUI(String userType) {
        Intent i;
        switch (userType) {
            case "driver":
                i = new Intent(this, DriverHomepage.class);
                break;
            case "user":
                i = new Intent(this, UserHomepage.class);
                break;
            case "hospital":
                i = new Intent(this, HospitalHomepage.class);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + userType);
        }
        startActivity(i);
        finish();
    }
}