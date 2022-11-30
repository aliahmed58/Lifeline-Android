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
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.frontend.R;
import com.main.frontend.activities.driver.DriverHomepage;
import com.main.frontend.activities.hospital.HospitalHomepage;
import com.main.frontend.activities.user.UserHomepage;
import com.main.frontend.auth.PhoneAuthenticator;
import com.main.frontend.constants.Constants;
import com.main.frontend.entity.User;
import com.main.frontend.network.VolleySingletonQueue;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignUpActivity2 extends AppCompatActivity {


    // firebase auth objects
    private FirebaseAuth auth;
    private FirebaseFirestore db;
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

    private String jwt;

    private static final String TAG = "SignUpVerify";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
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
                        auth.addAuthStateListener(firebaseAuth -> {
                            if (firebaseAuth.getCurrentUser() != null) {
                                user = firebaseAuth.getCurrentUser();
                                sendUserToBackend();
                            }
                        });
                    }
                }
                // display prompt if sms code is not of length 6
                else {
                    Toast.makeText(SignUpActivity2.this, "Invalid SMS code", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    private void updateUI(String userType) {
        if (userType.equals("driver"))
            showDriverUI();
        if (userType.equals("user")) showUserUI();
        if (userType.equals("hospital"))
            showHospitalUI();
    }

    // show user UI
    private void showUserUI() {
        Intent intent = new Intent(this, UserHomepage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // show driver UI
    private void showDriverUI() {
        Intent intent = new Intent(this, DriverHomepage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    // show hospital UI
    private void showHospitalUI() {
        Intent intent = new Intent(this, HospitalHomepage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    // TODO: clean up code | handle errors properly | empty user type errors
    private void sendUserToBackend() {
        User fbUser = new User(name, cellphone, userType);
        if (user != null) {
            db.collection("users")
                    .document(cellphone)
                    .set(fbUser)
                    .addOnSuccessListener(unused -> {
                        updateUI(fbUser.getUserType());
                    });
        }
    }
}