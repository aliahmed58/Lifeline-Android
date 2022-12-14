package com.main.frontend.activities.hospital;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.main.frontend.R;
import com.main.frontend.activities.common.HistoryFragment;
import com.main.frontend.activities.common.ProfileFragment;

public class HospitalHomepage extends AppCompatActivity {

    private BottomNavigationView navbar;

    private ProfileFragment profileFragment = new ProfileFragment();
    private HospitalHomeFragment homeFragment = new HospitalHomeFragment();
    private HistoryFragment historyFragment = new HistoryFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_homepage);

        navbar = findViewById(R.id.userNavbar);
        navbarListener();
        navbar.setSelectedItemId(R.id.userHome);
    }

    private void navbarListener() {
        navbar.setOnNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.userHome) {
                getSupportFragmentManager().beginTransaction().replace(R.id.userFragmentContainer, homeFragment).commit();
                return true;
            } else if (item.getItemId() == R.id.userProfile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.userFragmentContainer, profileFragment).commit();
                return true;
            } else if (item.getItemId() == R.id.userHistory) {
                getSupportFragmentManager().beginTransaction().replace(R.id.userFragmentContainer, historyFragment).commit();
                return true;
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.userFragmentContainer, homeFragment).commit();
                return true;
            }
        });
    }
}