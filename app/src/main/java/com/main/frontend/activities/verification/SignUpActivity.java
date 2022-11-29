package com.main.frontend.activities.verification;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.main.frontend.R;
import com.main.frontend.activities.driver.DriverHomepage;
import com.main.frontend.activities.hospital.HospitalHomepage;
import com.main.frontend.activities.user.UserHomepage;
import com.main.frontend.constants.Constants;
import com.main.frontend.network.VolleySingletonQueue;

import org.json.JSONException;
import org.json.JSONObject;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        reqQueue = VolleySingletonQueue.getInstance(this).getRequestQueue();

        auth = FirebaseAuth.getInstance();
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
        try {
            String URL = Constants.URL + "checkAccount";
            JSONObject body = new JSONObject();
            body.put("phone", cellphoneInput.getText().toString());

            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.POST, URL, body, response -> {
                try {
                    String result = response.getString("status");
                    // if account not found
                    if (result.equals("noAccount")) {
                        proceedSignup();
                    }
                    else if (result.equals("success")) {
                        Toast.makeText(SignUpActivity.this, "Account exists", Toast.LENGTH_SHORT).show();
                        Intent loginPage = new Intent(getBaseContext(), LoginPage.class);
                        startActivity(loginPage);
                        finish();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            },
                    error -> {

                    }){
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };

            reqQueue.add(req);
        }
        catch (JSONException e ) {
            e.printStackTrace();
        }
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