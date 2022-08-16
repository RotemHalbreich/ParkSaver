package com.example.park.utilities;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.park.R;


public class FragmentUtility {

    public static void addFragmentToBackStack(Fragment fragment, FragmentTransaction transaction) {
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
