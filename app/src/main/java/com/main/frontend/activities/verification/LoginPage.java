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

    // Volley request queue
    private RequestQueue reqQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // init UI Elements
        loginBtn = findViewById(R.id.loginBtn);
        signupBtn = findViewById(R.id.goToSignup);
        phoneEditText = findViewById(R.id.loginPhoneText);
        reqQueue = VolleySingletonQueue.getInstance(this).getRequestQueue();

        // handle button events
        signupClicked();
        loginClicked();

    }

    private void sendToBackend() {
        try {
            String URL = Constants.URL + "checkAccount";
            JSONObject body = new JSONObject();
            body.put("phone", phoneEditText.getText().toString());

            JsonObjectRequest req = new JsonObjectRequest(
                    Request.Method.POST, URL, body, response -> {
                try {
                    String result = response.getString("status");
                    // if account not found
                    if (result.equals("noAccount")) {
                        Toast.makeText(LoginPage.this, "Please Signup", Toast.LENGTH_SHORT).show();
                        Intent loginPage = new Intent(getBaseContext(), SignUpActivity.class);
                        startActivity(loginPage);
                        finish();
                    }
                    else if (result.equals("success")) {
                        proceedLogin();
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

    private void signupClicked() {
        signupBtn.setOnClickListener(view -> {
            Intent x = new Intent(getBaseContext(), SignUpActivity.class);
            startActivity(x);
            finish();
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
            sendToBackend();
        });
    }

}