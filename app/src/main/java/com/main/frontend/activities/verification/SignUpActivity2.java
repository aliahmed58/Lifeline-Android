package com.main.frontend.activities.verification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.main.frontend.R;
import com.main.frontend.activities.driver.DriverHomepage;
import com.main.frontend.activities.hospital.HospitalHomepage;
import com.main.frontend.activities.user.UserHomepage;
import com.main.frontend.auth.PhoneAuthenticator;
import com.main.frontend.constants.Constants;
import com.main.frontend.network.VolleySingletonQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity2 extends AppCompatActivity {

    // firebase auth objects
    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callBacks;
    // TODO:: CHECK RESEND TOKEN
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private String mVerificationId;
    private PhoneAuthenticator phoneAuthenticator;
    private FirebaseUser user;

    // Volley request queue
    private RequestQueue reqQueue;

    // ui elements
    private Button signUp;
    private EditText otpInput;

    private String name;
    private String cellphone;
    private String userType;
    private String smsCode;

    private static final String TAG = "SignUpVerify";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        auth = FirebaseAuth.getInstance();
        reqQueue = VolleySingletonQueue.getInstance(this.getApplicationContext()).getRequestQueue();

        signUp = (Button) findViewById(R.id.proceedSignUp);
        otpInput = (EditText) findViewById(R.id.otpInput);

        // get user values from intent
        setUserValuesFromSignup();
        handleAuthCallbacks();
        phoneAuthenticator = new PhoneAuthenticator(auth, this, callBacks);
        phoneAuthenticator.startPhoneAuth(cellphone);
        if (mVerificationId == null && savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
        handleSignUp();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("verificationId", mVerificationId);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mVerificationId = savedInstanceState.getString("verificationId");
    }

    private void handleAuthCallbacks() {
        callBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                phoneAuthenticator.signInWithPhone(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                } else if (e instanceof FirebaseTooManyRequestsException) {

                }
            }

            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                mVerificationId = verificationId;
                resendToken = forceResendingToken;
                Log.d("onCodeSent", verificationId);
            }
        };
    }

    private void setUserValuesFromSignup() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            name = extras.getString("name");
            cellphone = extras.getString("cellphone");
            userType = extras.getString("userType");
        }
    }

    private void handleSignUp() {
        signUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                smsCode = otpInput.getText().toString();

                Log.d("USERTYPE", userType);
                // if sms code is entered do not attempt verification
                if (smsCode.length() == 6) {
                    if (mVerificationId != null) {
                        // create phone credential from verification id and sms code
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, smsCode);
                        // authenticate phone
                        phoneAuthenticator.signInWithPhone(credential);
                        // get verified user
                        user = auth.getCurrentUser();
                        if (user != null) {
                            Toast.makeText(SignUpActivity2.this, user.getPhoneNumber(), Toast.LENGTH_LONG).show();
                            sendJWTtoServer();
                        }
                        else {
                            Toast.makeText(SignUpActivity2.this, "Auth failed, try again", Toast.LENGTH_LONG).show();
                        }
                    }
                }
                // display prompt if sms code is not of length 6
                else {
                    Toast.makeText(SignUpActivity2.this, "Invalid SMS code", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void sendJWTtoServer() {
        if (user != null) {
            user.getIdToken(false)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String JWT = task.getResult().getToken();
                                Log.d("JWT", JWT);
                                sendUserToBackend(JWT);
                            } else {
                                Log.w(TAG, "noJWT", task.getException());
                                // Todo: handle error if no token found
                            }
                        }
                    });
        }
    }

    private void updateUI() {
        if (user != null) {
            user.getIdToken(true)
                .addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                    public void onSuccess(GetTokenResult getTokenResult) {
                        String userType = Objects.requireNonNull(getTokenResult.getClaims().get("userType")).toString();
                        if (userType.equals("driver")) showDriverUI();
                        if (userType.equals("user")) showUserUI();
                        if (userType.equals("hospital")) showHospitalUI();
                    }
                });
        }
    }

    // show user UI
    private void showUserUI() {
        Intent intent = new Intent(this, UserHomepage.class);
        startActivity(intent);
        finish();
    }
    // show driver UI
    private void showDriverUI() {
        Intent intent = new Intent(this, DriverHomepage.class);
        startActivity(intent);
        finish();
    }
    // show hospital UI
    private void showHospitalUI() {
        Intent intent = new Intent(this, HospitalHomepage.class);
        startActivity(intent);
        finish();
    }


    // TODO: clean up code | handle errors properly | empty user type errors
    private void sendUserToBackend(String JWT) {
        try {
            String URL = Constants.URL + "signup";
            JSONObject jsonBody = new JSONObject();

            jsonBody.put("name", name);
            jsonBody.put("userType", userType);
            jsonBody.put("jwt", JWT);

            JsonObjectRequest signupReq = new JsonObjectRequest(Request.Method.POST, URL, jsonBody, new Response.Listener<JSONObject>() {
                public void onResponse(JSONObject response) {
                    try {
                        String result = response.getString("status");

                        // different actions from backend
                        if (result.equals("authFailed")) {
                            Toast.makeText(SignUpActivity2.this, "auth failed", Toast.LENGTH_SHORT).show();
                        }
                        // show login page since account exists
                        else if (result.equals("accountExits")) {
                            Toast.makeText(SignUpActivity2.this, "Please login", Toast.LENGTH_SHORT).show();
                            Intent loginPage = new Intent(getBaseContext(), LoginPage.class);
                            startActivity(loginPage);
                            finish();
                        }
                        // refresh token to get claims and change UI
                        else if (result.equals("success")) {
                            updateUI();
                        }

                        Log.d(TAG, result);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                public void onErrorResponse(VolleyError error) {
                    Log.d(TAG, "Error: " + error.getMessage());
                }
            }) {
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }
            };

            reqQueue.add(signupReq);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}