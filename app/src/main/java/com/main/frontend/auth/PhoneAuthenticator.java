package com.main.frontend.auth;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneAuthenticator {

    private FirebaseAuth auth;
    private Activity activity;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callBacks;
    private FirebaseUser user;

    public PhoneAuthenticator(FirebaseAuth auth, Activity activity,
                              PhoneAuthProvider.OnVerificationStateChangedCallbacks callBacks) {
        this.auth = auth;
        this.activity = activity;
        this.callBacks = callBacks;
    }

    public void startPhoneAuth(String phoneNumber) {
        Log.d("StartAuth", "CAlled");
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber(phoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(activity)
                    .setCallbacks(callBacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(activity)                 // Activity (for callback binding)
                        .setCallbacks(callBacks)          // OnVerificationStateChangedCallbacks
                        .setForceResendingToken(token)     // ForceResendingToken from callbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void verifyPhoneNumberWithCode(String verificationId, String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
    }

    public boolean signInWithPhone(PhoneAuthCredential credential) {

        auth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("AUTH", "signInWithCredential:success");
                            user = task.getResult().getUser();
                        }
                        else {
                            Log.w("AUTH", "signInWithCredential:failed", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                user = null;
                            }
                        }
                    }
                });
        return true;
    }

}
