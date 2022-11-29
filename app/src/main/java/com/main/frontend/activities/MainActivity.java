package com.main.frontend.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.main.frontend.R;
import com.main.frontend.activities.verification.SignUpActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button signUpIntentButton = (Button) findViewById(R.id.signupHomePage);
        Button loginIntentButton = (Button) findViewById(R.id.loginHomePage);

        signUpIntentButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(view.getContext(), SignUpActivity.class));
            }
        });
    }
}