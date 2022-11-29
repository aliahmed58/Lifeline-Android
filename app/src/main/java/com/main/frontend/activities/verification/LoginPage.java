package com.main.frontend.activities.verification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.frontend.R;
import com.main.frontend.constants.Constants;
import com.main.frontend.network.VolleySingletonQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginPage extends AppCompatActivity {

    // UI Elements
    private Button loginBtn;
    private Button signupBtn;
    private EditText phoneEditText;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // init UI Elements
        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.goToSignup);
        phoneEditText = findViewById(R.id.loginPhoneText);
        db = FirebaseFirestore.getInstance();

        // handle button events
        signupClicked();
        loginClicked();
    }

    private void signupClicked() {
        signupBtn.setOnClickListener(view -> {
            Intent x = new Intent(getBaseContext(), SignUpActivity.class);
            startActivity(x);
        });
    }

    private void proceedLogin() {
        Intent i = new Intent(this, LoginPage2.class);
        i.putExtra("phone", phoneEditText.getText().toString());
        startActivity(i);
        finish();
    }

    // TODO: ADD VALIDATION CHECKS
    // login button clicked
    private void loginClicked() {
        loginBtn.setOnClickListener(view -> {
            checkAccount();
        });
    }

    private void checkAccount() {
        db.collection("users")
                .whereEqualTo("phone", phoneEditText.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                   if (task.isSuccessful()) {
                       if (task.getResult().size() != 0) {
                           proceedLogin();
                       }
                       else {
                           Toast.makeText(this, "Account doesn't exist, please signup", Toast.LENGTH_SHORT).show();
                       }
                   }
                });
    }
}