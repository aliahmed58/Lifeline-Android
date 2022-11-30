package com.main.frontend.activities.user;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.main.frontend.R;

import java.util.Locale;

public class CallAmbulance extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private EditText location;
    private EditText specEditText;
    private RadioGroup ambTypeGrp;
    private RadioGroup paymentMethodGrp;
    private Button findAmbulanceBtn;
    private Button cancelFind;

    private String ambulanceType;
    private String paymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_ambulance);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        location = findViewById(R.id.locationEditText);
        specEditText = findViewById(R.id.callAmbulanceSpecialization);
        ambTypeGrp = findViewById(R.id.userCallAmbulanceTypes);
        paymentMethodGrp = findViewById(R.id.paymentGrp);
        cancelFind = findViewById(R.id.cancelFindAmbulance);
        findAmbulanceBtn = findViewById(R.id.findAmbulanceBtn);

        cancelFind.setOnClickListener(view -> {finish();});
        handleProceed();

    }

    private void handleProceed() {
        findAmbulanceBtn.setOnClickListener(view -> {
            if (validationChecks()) {
                // add to db proceed to waiting screen
            }
        });
    }

    private boolean validationChecks() {
        boolean success = true;
        if (location.getText().length() == 0) {
            success = false;
            Toast.makeText(this, "Enter location", Toast.LENGTH_LONG).show();
        }

        ambTypeGrp.setOnCheckedChangeListener(((radioGroup, i) -> {
            RadioButton checkedButton = (RadioButton) radioGroup.findViewById(i);
            if (checkedButton.isChecked()) {
                ambulanceType = checkedButton.getText().toString().toLowerCase(Locale.ROOT);
            }
        }));
        if (ambulanceType == null) {
            success = false;
            Toast.makeText(this, "Please select ambulance type", Toast.LENGTH_SHORT).show();
        }

        paymentMethodGrp.setOnCheckedChangeListener(((radioGroup, i) -> {
            RadioButton checkedButton = (RadioButton) radioGroup.findViewById(i);
            if (checkedButton.isChecked()) {
                paymentMethod = checkedButton.getText().toString().toLowerCase(Locale.ROOT);
            }
        }));

        if (paymentMethod == null) {
            success = false;
            Toast.makeText(this, "Please select payment method", Toast.LENGTH_SHORT).show();
        }

        return success;
    }

}