package com.example.park.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.park.MyViewModel;
import com.example.park.R;
import com.example.park.databinding.FragmentLocationBinding;
import com.example.park.models.User;
import com.example.park.utilities.FragmentUtility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;


public class LocationFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private FragmentLocationBinding binding;
    private GoogleMap googleMap;
    private MyViewModel viewModel;
    private boolean zoomToTheFirstLocationFlag = true;

    private AlertDialog alertDialog = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLocationBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);


    }

    public static LocationFragment newInstance() {
        return new LocationFragment();
    }

    //Invoked when the map is ready
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMarkerClickListener(this);
        viewModel.getAllUsersParkOwners(binding.progressBar)
                .observe(getViewLifecycleOwner(), users -> {
                    for (User user : users) {
                        addGoogleMapMarker(user);
                    }
                });
    }


    private void addGoogleMapMarker(User user) {
        googleMap.addMarker(new MarkerOptions().position(parseLatLng(user.location))).setTag(user);
        if (zoomToTheFirstLocationFlag) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(parseLatLng(user.location), 15.0f));
            zoomToTheFirstLocationFlag = false;
        }
    }

    private LatLng parseLatLng(String location) {
        String[] latlong = location.split(",");
        double latitude = Double.parseDouble(latlong[0]);
        double longitude = Double.parseDouble(latlong[1]);
        return new LatLng(latitude, longitude);
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (alertDialog != null && alertDialog.isShowing()) {
            return true;
        }
        final User user = (User) marker.getTag();
        alertDialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle(getResources().getString(R.string.choose_park))
                .setMessage(getMessage(user))
                .setNegativeButton(getResources().getString(R.string.decline), (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton(getResources().getString(R.string.accept), (dialogInterface, i) -> {
                    FragmentUtility.addFragmentToBackStack(AppointmentFragment.newInstance(user), requireActivity().getSupportFragmentManager().beginTransaction());
                }).show();
        return true;
    }

    private String getMessage(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.name_of_owner)).append(" ").append(user.fullName).append("\n")
                .append(getString(R.string.telephone)).append(" ").append(user.phoneNumber).append("\n")
                .append(getString(R.string.hours)).append(" : ").append(user.fromHour).append("-").append(user.toHour);
        return sb.toString();


    }
}