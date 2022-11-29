package com.main.frontend.activities.verification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnSuccessListener;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginPage2 extends AppCompatActivity {

    private Button verifyOtpBtn;
    private EditText otpEditText;

    // firebase variables
    private FirebaseAuth auth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callBacks;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private String mVerificationId;
    private PhoneAuthenticator phoneAuthenticator;
    private FirebaseUser user;

    private String smsCode;
    private String phone;
    private static final String TAG = "LOGIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page2);

        auth = FirebaseAuth.getInstance();

        verifyOtpBtn = findViewById(R.id.verifyLogin);
        otpEditText = findViewById(R.id.loginOtpEditText);

        handleAuthCallbacks();
        setPhoneNumberFromBundle();

        phoneAuthenticator = new PhoneAuthenticator(auth, this, callBacks);
        phoneAuthenticator.startPhoneAuth("+16505553436");
        // restore verification id if lost in activity destruction
        if (mVerificationId == null && savedInstanceState != null) onRestoreInstanceState(savedInstanceState);

        handleLogin();
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

    private void handleLogin() {
        verifyOtpBtn.setOnClickListener(view -> {
            smsCode = otpEditText.getText().toString();
            if (smsCode.length() == 6) {
                if (mVerificationId != null) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, smsCode);
                    phoneAuthenticator.signInWithPhone(credential);
                    user = auth.getCurrentUser();
                    if (user != null) {
                        Log.d(TAG, "Success finding user");
                        updateUI();
                    }
                    else {
                        Toast.makeText(this, "Error signing in", Toast.LENGTH_SHORT).show();

                    }

                }
            }
        });
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

    // handle auth call backs
    private void handleAuthCallbacks() {
        callBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                phoneAuthenticator.signInWithPhone(phoneAuthCredential);
            }

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
    private void setPhoneNumberFromBundle() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            phone = extras.getString("phone");
        }
    }

}