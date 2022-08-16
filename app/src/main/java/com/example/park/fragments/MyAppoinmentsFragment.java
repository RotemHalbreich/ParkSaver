package com.example.park.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.park.MyViewModel;
import com.example.park.databinding.FragmentMyAppointmentsBinding;
import com.example.park.recycles.MyAppointmentAdapter;

public class MyAppoinmentsFragment extends Fragment {
    private FragmentMyAppointmentsBinding binding;
    private MyViewModel viewModel;

    private MyAppointmentAdapter adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMyAppointmentsBinding.inflate(inflater, container, false);
        binding.recycleView.setHasFixedSize(true);
        binding.recycleView.setLayoutManager(new LinearLayoutManager(requireContext()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        binding.progressBar.setVisibility(View.VISIBLE);
        boolean isOwner = getArguments().getBoolean(IS_OWNER_KEY);
        viewModel.getAllMyAppointments(isOwner).observe(getViewLifecycleOwner(), appointments -> {
            binding.progressBar.setVisibility(View.GONE);
            if (appointments != null && appointments.size() > 0) {
                binding.noDataDisplay.setVisibility(View.GONE);
                binding.recycleView.setVisibility(View.VISIBLE);
                adapter = new MyAppointmentAdapter(appointments, isOwner);
                binding.recycleView.setAdapter(adapter);
                binding.progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }


    public static MyAppoinmentsFragment newInstance(boolean isOwner) {
        MyAppoinmentsFragment fragment = new MyAppoinmentsFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_OWNER_KEY, isOwner);
        fragment.setArguments(bundle);
        return fragment;
    }

    private static final String IS_OWNER_KEY = "OWNER";


}