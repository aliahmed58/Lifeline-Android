package com.main.frontend.activities.verification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.main.frontend.R;
import com.main.frontend.activities.driver.DriverHomepage;
import com.main.frontend.activities.hospital.HospitalHomepage;
import com.main.frontend.activities.user.UserHomepage;
import com.main.frontend.constants.Constants;
import com.main.frontend.network.VolleySingletonQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    // UI elements
    private RadioGroup radioGroup;
    private Button signupBtn;
    private EditText cellphoneInput;
    private EditText nameInput;

    // user data
    private String userType;
    private String name;
    private String cellphone;

    private RequestQueue reqQueue;

    // tag
    private static final String TAG = "SignUpActivity";

    // firebase
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        reqQueue = VolleySingletonQueue.getInstance(this).getRequestQueue();

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // show UI if user already logged in
        // to be implemented in login page
        //updateUI();

        radioGroup = (RadioGroup) findViewById(R.id.signUpTypeRadioGroup);
        signupBtn = (Button) findViewById(R.id.signUpButton);
        cellphoneInput = (EditText) findViewById(R.id.cellphoneInputText);
        nameInput = (EditText) findViewById(R.id.nameInputText);
        // handle UI events
        // radio selection
        handleRadioSelection();
        handleSignUpButton();
    }

    private void checkAccount() {

        cellphone = cellphoneInput.getText().toString();

        db.collection("users")
                .whereEqualTo("phone", cellphone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().size() == 0) {
                            proceedSignup();
                        }
                        else {
                            Toast.makeText(this, "User exists, please login", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getBaseContext(), LoginPage.class);
                            startActivity(i);
                            finish();
                        }
                    }
                    else {
                        Log.w(TAG, "error fetching data: ", task.getException());
                    }
                });

    }

    private void updateUI() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;
        user.getIdToken(false)
                .addOnSuccessListener(getTokenResult -> {
                    String type = Objects.requireNonNull(getTokenResult.getClaims().get("userType")).toString();
                    showUserTypeUI(type);
                    });
    }

    private void showUserTypeUI(String userType) {
        Intent i;
        switch(userType) {
            case "driver":
                i = new Intent(getBaseContext(), DriverHomepage.class);
                break;
            case "hospital":
                i = new Intent(getBaseContext(), HospitalHomepage.class);
                break;
            case "user":
                i = new Intent(getBaseContext(), UserHomepage.class);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + userType);
        }
        startActivity(i);
        finish();

    }

    private void handleRadioSelection() {
        // overrides the checkedChangeListener
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                // get the checked radiobutton
                RadioButton checkedRadioButton = (RadioButton) radioGroup.findViewById(checkedId);
                // gets the checked state of radiobutton
                boolean isChecked = checkedRadioButton.isChecked();
                // set the selected string option to the one selected by user
                if (isChecked) {
                    userType = checkedRadioButton.getText().toString().toLowerCase(Locale.ROOT);
                }
            }
        });
    }
    // TODO: AVOID EMPTY VALUES
    private void handleSignUpButton() {
        signupBtn.setOnClickListener(view -> {
            checkAccount();
        });
    }

    private void proceedSignup() {
        cellphone = cellphoneInput.getText().toString();
        name = nameInput.getText().toString();
        Intent verifySignUp = new Intent(getBaseContext(), SignUpActivity2.class);
        verifySignUp.putExtra("name", name);
        verifySignUp.putExtra("cellphone",  cellphone);
        verifySignUp.putExtra("userType", userType);
        startActivity(verifySignUp);
    }
}