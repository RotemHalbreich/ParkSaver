package com.example.park.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.park.MyViewModel;
import com.example.park.R;
import com.example.park.databinding.ActivityMainBinding;
import com.example.park.fragments.CreditCardFragment;
import com.example.park.fragments.LocationFragment;
import com.example.park.fragments.MyAppoinmentsFragment;
import com.example.park.models.User;
import com.example.park.utilities.FragmentUtility;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private MyViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initDrawer();
        viewModel = new ViewModelProvider(this).get(MyViewModel.class);
        viewModel.getUser().observe(this, user -> {
            TextView textView = binding.navView.getHeaderView(0).findViewById(R.id.hello);
            textView.setText(user.fullName + "\n" + FirebaseAuth.getInstance().getCurrentUser().getEmail());
            setUserFragment(user.isOwner);
        });

    }

    private void setUserFragment(boolean isOwner) {
        if (!isOwner) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, LocationFragment.newInstance());
            ft.commit();
        } else {
            binding.navView.getMenu().findItem(R.id.my_parkings).setVisible(false);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, MyAppoinmentsFragment.newInstance(true));
            ft.commit();
        }
    }

    //SIDE BAR
    private void initDrawer() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, binding.myDrawerLayout, R.string.nav_open, R.string.nav_close);
        binding.myDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.navView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout: {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
            }
            case R.id.nav_billing: {
                FragmentUtility.addFragmentToBackStack(CreditCardFragment.newInstance(), getSupportFragmentManager().beginTransaction());
                binding.myDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
            case R.id.my_parkings: {
                FragmentUtility.addFragmentToBackStack(MyAppoinmentsFragment.newInstance(false), getSupportFragmentManager().beginTransaction());
                binding.myDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            }
        }
        return false;
    }

}