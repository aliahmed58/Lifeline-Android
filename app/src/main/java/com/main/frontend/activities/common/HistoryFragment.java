package com.main.frontend.activities.common;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.main.frontend.R;

public class HistoryFragment extends Fragment {

    private BottomNavigationView historyMenu;

    private HistoryPassed historyPassedFragment = new HistoryPassed();
    private HistoryActive historyActive = new HistoryActive();

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        historyMenu = view.findViewById(R.id.historyMenuBar);
        historyNavbarListener(view);
        historyMenu.setSelectedItemId(R.id.activeHistory);
        return view;
    }

    private void historyNavbarListener(View view) {
        historyMenu.setOnNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.activeHistory) {
                getChildFragmentManager().beginTransaction().replace(R.id.historyFragmentContainer, historyActive).commit();
                return true;
            }
            else if (item.getItemId() == R.id.pastHistory){
                getChildFragmentManager().beginTransaction().replace(R.id.historyFragmentContainer, historyPassedFragment).commit();
                return true;
            }
            return false;
        });
    }
}