package com.example.park.fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.park.R;
import com.example.park.databinding.Register1FragmentBinding;
import com.example.park.db.Consts;
import com.example.park.models.User;
import com.example.park.utilities.FragmentUtility;
import com.example.park.utilities.TextUtility;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.InputStream;


public class Register1Fragment extends Fragment {

    private Register1FragmentBinding binding;
    private Uri selectedImage;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = Register1FragmentBinding.inflate(inflater, container, false);//$$Render
        binding.isOwnerCheckBox.setOnCheckedChangeListener((compoundButton, b) -> binding.registerButton.setText(getString(b ? R.string.continue_register : R.string.register_button)));
        binding.registerButton.setOnClickListener(view -> tryRegister());
        binding.profileImage.setOnClickListener(view -> {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 1);
        });
        return binding.getRoot();
    }

    public static Register1Fragment newInstance() {
        return new Register1Fragment();
    }

    private void tryRegister() {
        binding.progressBar.setVisibility(View.VISIBLE);
        if (!TextUtility.isValid(binding.emailEditText, binding.passwordEditText, binding.fullNameEditText, binding.telephoneEditText)) {
            Toast.makeText(requireContext(), getString(R.string.error_text), Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.INVISIBLE);
        } else if (selectedImage == null) {
            Toast.makeText(requireContext(), getString(R.string.fill_image), Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.INVISIBLE);
        } else {
            String email = binding.emailEditText.getText().toString();
            String password = binding.passwordEditText.getText().toString();
            String phoneNumber = binding.telephoneEditText.getText().toString();
            String fullName = binding.fullNameEditText.getText().toString();
            if (binding.isOwnerCheckBox.isChecked()) {
                FragmentUtility.addFragmentToBackStack(Register2Fragment.newInstance(email, password, phoneNumber, fullName, selectedImage), requireActivity().getSupportFragmentManager().beginTransaction());
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            //Get result here
                            if (task.isSuccessful()) {
                                try {
                                    InputStream iStream = requireActivity().getContentResolver().openInputStream(selectedImage);
                                    byte[] inputData = TextUtility.getBytes(iStream);
                                    final String uid = task.getResult().getUser().getUid();
                                    String imageName = uid + ".png";
                                    storageRef.child(imageName).putBytes(inputData).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            User user = new User(fullName, imageName, phoneNumber, null, false, null, null, null);
                                            db.collection(Consts.USERS_DB).document(uid).set(user)
                                                    .addOnSuccessListener(unused -> {
                                                                Toast.makeText(requireContext(), getString(R.string.register_sucsses), Toast.LENGTH_SHORT).show();
                                                        //$$ all sucsses finish the activity
                                                                requireActivity().finish();
                                                            }
                                                    ).addOnFailureListener(e -> registerFailed());
                                        }
                                    });
                                } catch (Exception ignored) {

                                }
                            }
                            binding.progressBar.setVisibility(View.INVISIBLE);
                            if (task.getException() != null) {
                                Toast.makeText(requireContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            }
        }
    }


    private void registerFailed() {
        Toast.makeText(requireContext(), getString(R.string.register_fail), Toast.LENGTH_SHORT).show();
        binding.progressBar.setVisibility(View.INVISIBLE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent
            imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
//                if (resultCode == RESULT_OK) {
//                    Uri selectedImage = imageReturnedIntent.getData();
//                    binding.profileImage.setImageURI(selectedImage);
//                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    selectedImage = imageReturnedIntent.getData();
                    binding.profileImage.setImageURI(selectedImage);
                }
                break;
        }
    }
}