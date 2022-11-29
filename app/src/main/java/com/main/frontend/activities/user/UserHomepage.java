package com.main.frontend.activities.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.frontend.R;
import com.main.frontend.activities.MainActivity;
import com.main.frontend.entity.User;
import com.main.frontend.network.VolleySingletonQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserHomepage extends AppCompatActivity {

    private static final String TAG = "UserHomepage";

    // Entities
    private User user;

    // UI Elements
    private TextView userDisplayText;

    // firebase variables
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_homepage);

        userDisplayText = findViewById(R.id.userDisplayName);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        checkAuth();

    }

    private void checkAuth() {
        FirebaseUser fbUser = auth.getCurrentUser();
        if (fbUser == null) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        }
        else {
            Toast.makeText(this, fbUser.getPhoneNumber(), Toast.LENGTH_SHORT).show();
            DocumentReference docRef = db.collection("users").document(Objects.requireNonNull(fbUser.getPhoneNumber()));
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                user = documentSnapshot.toObject(User.class);
                userDisplayText.setText("Welcome, " + user.getName());
            });

        }
    }


}