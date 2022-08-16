package com.example.park.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.park.R;
import com.example.park.databinding.ActivityRegisterBinding;
import com.example.park.fragments.Register1Fragment;


public class RegisterActivity extends AppCompatActivity {


    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRegister1Fragment();
    }

    private void setRegister1Fragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, Register1Fragment.newInstance());
        ft.commit();
    }


}