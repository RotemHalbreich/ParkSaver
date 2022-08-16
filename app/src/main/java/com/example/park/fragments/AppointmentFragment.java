package com.example.park.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.park.MyViewModel;
import com.example.park.R;
import com.example.park.databinding.FragmentAppointmentBinding;
import com.example.park.db.Consts;
import com.example.park.models.Appointment;
import com.example.park.models.User;
import com.example.park.utilities.TextUtility;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.List;

public class AppointmentFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private ArrayAdapter hoursAdapter;
    StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private DatePickerDialog datePickerDialog;

    private MyViewModel viewModel;
    private AlertDialog alertDialog = null;


    private static final String USER_KEY = "BRANCH";
    private User selectedUser;
    private FragmentAppointmentBinding binding;
    private List<String> notAvliableList = null;
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAppointmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectedUser = TextUtility.fromJson(getArguments().getString(USER_KEY, ""), User.class);
        viewModel = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        binding.details.setText(getDetails(selectedUser));
        fetchPicture();
        viewModel.getUser().observe(getViewLifecycleOwner(), user -> currentUser = user);
        binding.createButton.setOnClickListener(view12 -> createAppointment());
        binding.calenderIcon.setOnClickListener(view1 -> showDateTimePicker());
        initFromAndToAdapters();

    }

    private void initFromAndToAdapters() {
        //cacluate the hours of the avlialbe time
        int start = Consts.AVAILABLE_HOURS.indexOf(selectedUser.fromHour);
        int end = Consts.AVAILABLE_HOURS.indexOf(selectedUser.toHour);
        List<String> hours = Consts.AVAILABLE_HOURS.subList(start, end + 1);
        hoursAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, hours);
        binding.toHour.setAdapter(hoursAdapter);
        binding.fromHour.setAdapter(hoursAdapter);
    }

    private void fetchPicture() {
        binding.progressBar.setVisibility(View.VISIBLE);
        storageRef.child("/" + selectedUser.getImageUrl()).getDownloadUrl().addOnSuccessListener(uri -> {
            binding.progressBar.setVisibility(View.GONE);
            Picasso.with(requireContext()).load(uri).centerCrop()
                    .resize(binding.profileImage.getMeasuredWidth(), binding.profileImage.getMeasuredHeight())
                    .into(binding.profileImage);
        });
    }


    private String getDetails(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.name_of_owner)).append(" ").append(user.fullName).append("\n")
                .append(getString(R.string.telephone)).append(" ").append(user.phoneNumber).append("\n")
                .append(getString(R.string.hours)).append(" : ").append(user.fromHour).append("-").append(user.toHour).append("\n")
                .append(getString(R.string.adress)).append("  ").append(user.adress);
        return sb.toString();


    }

    private void showDateTimePicker() {
        final Calendar min_date_c = Calendar.getInstance();
        int year = min_date_c.get(Calendar.YEAR);
        int month = min_date_c.get(Calendar.MONTH);
        int day = min_date_c.get(Calendar.DAY_OF_MONTH);
        if (datePickerDialog == null) {
            datePickerDialog = DatePickerDialog.newInstance(this, year, month, day);
            datePickerDialog.setThemeDark(false);
            datePickerDialog.showYearPickerFirst(false);
            datePickerDialog.setTitle("Date Picker");
            datePickerDialog.setMinDate(min_date_c);
            Calendar max_date_c = Calendar.getInstance();
            max_date_c.set(Calendar.YEAR, min_date_c.get(Calendar.YEAR) + 1);
            datePickerDialog.setMaxDate(max_date_c);
            for (Calendar loopdate = min_date_c; min_date_c.before(max_date_c); min_date_c.add(Calendar.DATE, 1), loopdate = min_date_c) {
                int dayOfWeek = loopdate.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
                    Calendar[] disabledDays = new Calendar[1];
                    disabledDays[0] = loopdate;
                    datePickerDialog.setDisabledDays(disabledDays);
                }
            }
        }
        if (!datePickerDialog.isVisible()) {
            datePickerDialog.show(requireActivity().getSupportFragmentManager(), "DatePickerDialog");
        }
    }


    private void createAppointment() {
        //Check all is fill
        if (!TextUtility.isValid(binding.dateEditText, binding.fromHour, binding.toHour) || currentUser == null) {
            Toast.makeText(requireContext(), getString(R.string.must_to_fill), Toast.LENGTH_SHORT).show();
        } else {
            if (alertDialog != null && alertDialog.isShowing()) {
                return;
            }
            String date = binding.dateEditText.getText().toString();
            String fromHour = binding.fromHour.getText().toString();
            String toHour = binding.toHour.getText().toString();
            //check valid of hours from - to
            if (!TextUtility.isFromHourToHourValid(fromHour, toHour)) {
                Toast.makeText(requireContext(), getString(R.string.error_hours), Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.INVISIBLE);
                return;
            }
            //
            if (!TextUtility.checkOverlapping(fromHour, toHour, notAvliableList)) {
                Toast.makeText(requireContext(), getString(R.string.over_lap_error), Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.INVISIBLE);
                return;
            }


            int price = TextUtility.getNumberOfHours(fromHour, toHour) * 15;
            String timeRange = fromHour + "-" + toHour;
            alertDialog = new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(getResources().getString(R.string.payement_verification))
                    .setMessage(getString(R.string.sum_to_pay) + " - " + "₪" + price)
                    .setNegativeButton(getResources().getString(R.string.decline), (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton(getResources().getString(R.string.pay_now), (dialogInterface, i) -> {
                        viewModel.getCard(binding.progressBar).observe(getViewLifecycleOwner(), creditCard -> {
                            if (creditCard == null) {
                                dialogInterface.dismiss();
                                Toast.makeText(requireContext(), getString(R.string.no_payment), Toast.LENGTH_SHORT).show();

                            } else {
                                Appointment appointment = new Appointment(selectedUser.uid, selectedUser.fullName, FirebaseAuth.getInstance().getUid(), creditCard.getFirstName(), date, timeRange, price, selectedUser.adress, selectedUser.phoneNumber, currentUser.phoneNumber);
                                FirebaseFirestore.getInstance().collection(Consts.APPOINTEMNTS_DB)
                                        .add(appointment).addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                dialogInterface.dismiss();
                                                Toast.makeText(requireContext(), getString(R.string.appointment_created), Toast.LENGTH_SHORT).show();
                                                getActivity().onBackPressed();
                                            } else {
                                                Toast.makeText(requireContext(), getString(R.string.appointment_failed), Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            }
                        });

                    }).show();


        }
    }


    public AppointmentFragment() {
        // Required empty public constructor
    }

    public static AppointmentFragment newInstance(User branch) {
        AppointmentFragment fragment = new AppointmentFragment();
        Bundle bundle = new Bundle();
        bundle.putString(USER_KEY, TextUtility.toJson(branch));
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        binding.dateEditText.setText(String.format("%d/%d/%d", dayOfMonth, monthOfYear + 1, year));
        binding.fromHour.setText("");
        binding.toHour.setText("");
        viewModel.fetchAllNotAvaliableHours(binding.progressBar, binding.dateEditText.getText().toString(), selectedUser.uid).observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                notAvliableList = strings;
                binding.hoursNotAvaliable.setText(getNotAvaliabeHours(strings));
            }
        });
    }


    private String getNotAvaliabeHours(List<String> list) {
        StringBuilder sb = new StringBuilder();
        sb.append(getString(R.string.hours_not_avaliable)).append(" : ");
        for (String str : list) {
            sb.append(str).append(" , ");
        }
        return sb.toString();
    }
}