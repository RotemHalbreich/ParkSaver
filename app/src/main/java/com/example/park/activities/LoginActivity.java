package com.example.park.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.park.R;
import com.example.park.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


//$$Second
public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ActivityLoginBinding binding;

    //First method when
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Attach the view
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //loggin button
        binding.loginButton.setOnClickListener(view -> login());
        //registerbutton
        binding.registerTextView.setOnClickListener(view -> moveToRegisterActivity());
    }


    //Second method when
    @Override
    public void onStart() {
        super.onStart();
        //If used already logged in move to main activity
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            moveToMainActivity();
        }
    }

    private void moveToMainActivity() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void moveToRegisterActivity() {
        Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(i);
    }

    private void login() {
        binding.progressBar.setVisibility(View.VISIBLE);
        String email = binding.emailEditText.getText().toString();
        String password = binding.passwordEditText.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            onLoginFailed();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            moveToMainActivity();
                        } else {
                            onLoginFailed();
                        }
                    });
        }
    }

    private void onLoginFailed() {
        binding.progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(this, getString(R.string.login_failed),
                Toast.LENGTH_SHORT).show();
    }
}