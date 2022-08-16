package com.example.park.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.park.R;
import com.example.park.databinding.Register2FragmentBinding;
import com.example.park.db.Consts;
import com.example.park.models.User;
import com.example.park.utilities.TextUtility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;


public class Register2Fragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private Register2FragmentBinding binding;
    private GoogleMap googleMap;
    private LatLng latLng;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = Register2FragmentBinding.inflate(inflater, container, false);
        binding.registerButton.setOnClickListener(view -> tryRegister());
        binding.fromHour.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, Consts.AVAILABLE_HOURS));
        binding.toHour.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, Consts.AVAILABLE_HOURS));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps);
        //init map
        mapFragment.getMapAsync(this);
    }

    private static final String EMAIL = "EMAIL";
    private static final String PASSWORD = "PASSWORD";
    private static final String PHONE_NUMBER = "PHONE_NUMBER";
    private static final String FULL_NAME = "FULL_NAME";
    private static final String SELECTED_IMAGE = "SELECTED_IMAGE";
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static Register2Fragment newInstance(String email, String password, String phoneNumber, String fullName, Uri selectedImage) {
        Register2Fragment fragment = new Register2Fragment();
        Bundle args = new Bundle();
        args.putString(EMAIL, email);
        args.putString(PASSWORD, password);
        args.putString(PHONE_NUMBER, phoneNumber);
        args.putString(FULL_NAME, fullName);
        args.putParcelable(SELECTED_IMAGE, selectedImage);
        fragment.setArguments(args);
        return fragment;
    }

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMapClickListener(this);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(32.107177, 34.801011), 15.0f));
    }

    private void tryRegister() {
        binding.progressBar.setVisibility(View.VISIBLE);
        if (!TextUtility.isValid(binding.adress, binding.fromHour, binding.toHour)) {
            Toast.makeText(requireContext(), getString(R.string.error_text), Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.INVISIBLE);
        } else if (latLng == null) {
            Toast.makeText(requireContext(), getString(R.string.choose_location), Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.INVISIBLE);
        } else {
            String fromHour = binding.fromHour.getText().toString();
            String toHour = binding.toHour.getText().toString();
            if (!TextUtility.isFromHourToHourValid(fromHour, toHour)) {
                Toast.makeText(requireContext(), getString(R.string.error_hours), Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.INVISIBLE);
                return;
            }

            Bundle bundle = getArguments();
            String email = bundle.getString(EMAIL);
            String password = bundle.getString(PASSWORD);
            String phoneNumber = bundle.getString(PHONE_NUMBER);
            String fullName = bundle.getString(FULL_NAME);
            String adress = binding.adress.getText().toString();

            Uri selectedImage = bundle.getParcelable(SELECTED_IMAGE);
            String location = latLng.latitude + "," + latLng.longitude;
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            try {
                                InputStream iStream = requireActivity().getContentResolver().openInputStream(selectedImage);
                                byte[] inputData = TextUtility.getBytes(iStream);
                                final String uid = task.getResult().getUser().getUid();
                                String imageName = uid + ".png";
                                storageRef.child(imageName).putBytes(inputData).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        User user = new User(fullName, imageName, phoneNumber, location, true, adress, fromHour, toHour);
                                        db.collection(Consts.USERS_DB).document(uid).set(user)
                                                .addOnSuccessListener(unused -> {
                                                            Toast.makeText(requireContext(), getString(R.string.register_sucsses), Toast.LENGTH_SHORT).show();
                                                            requireActivity().finish();
                                                        }
                                                ).addOnFailureListener(e -> registerFailed());
                                    }
                                });
                            } catch (Exception e) {

                            }

                        } else {
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            if (task.getException() != null) {
                                Toast.makeText(requireContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }
    }


    private void registerFailed() {
        Toast.makeText(requireContext(), getString(R.string.register_fail), Toast.LENGTH_SHORT).show();
        binding.progressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        if (this.latLng != null) {
            googleMap.clear();
        }
        this.latLng = latLng;
        MarkerOptions marker = new MarkerOptions()
                .position(latLng);
        this.googleMap.addMarker(marker);
    }
}