package com.example.park.recycles;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.park.R;
import com.example.park.databinding.AppointmentRowBinding;
import com.example.park.models.Appointment;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.List;

//Adapter of the recycler view
public class MyAppointmentAdapter extends RecyclerView.Adapter<MyAppointmentAdapter.ViewHolder> {

    private final List<Appointment> appointments;
    private final Boolean isOwner;


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final AppointmentRowBinding binding;

        public ViewHolder(AppointmentRowBinding view) {
            super(view.getRoot());
            binding = view;
        }

        public void bind(Appointment appointment) {
            if (isOwner) {
                binding.nameTVHeader.setText(itemView.getContext().getString(R.string.name_of_renter));
                binding.nameTV.setText(appointment.nameOfRenter);
                binding.telephoneTv.setText(appointment.telephoneOfRenter);
            } else {
                binding.nameTV.setText(appointment.nameOfOwner);
                binding.telephoneTv.setText(appointment.telephoneOfOwner);
            }
            binding.dateTV.setText(appointment.date);
            binding.hourTV.setText(appointment.timeRange);
            binding.priceTV.setText(appointment.price + "₪");
            binding.adressTv.setText(appointment.adress);
            FirebaseStorage.getInstance().getReference().child("/" + (isOwner ? appointment.renterUid : appointment.ownerUid) + ".png").getDownloadUrl().addOnSuccessListener(uri -> Picasso.with(itemView.getContext()).load(uri).centerCrop()
                    .resize(binding.profileImage.getMeasuredWidth(), binding.profileImage.getMeasuredHeight())
                    .into(binding.profileImage));
        }
    }

    public MyAppointmentAdapter(List<Appointment> appointmentList, boolean isOwner) {
        appointments = appointmentList;
        this.isOwner = isOwner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AppointmentRowBinding itemBinding = AppointmentRowBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Appointment appointment = appointments.get(position);
        viewHolder.bind(appointment);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return appointments.size();
    }

}

