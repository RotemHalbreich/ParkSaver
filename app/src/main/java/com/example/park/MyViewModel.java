package com.example.park;

import android.view.View;
import android.widget.ProgressBar;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.park.db.Consts;
import com.example.park.models.Appointment;
import com.example.park.models.CreditCard;
import com.example.park.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyViewModel extends ViewModel {
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Appointment>> getAllMyAppointments(boolean isOwner) {
        final MutableLiveData<List<Appointment>> appointments = new MutableLiveData<>();
        db.collection(Consts.APPOINTEMNTS_DB)
                .whereEqualTo(isOwner ? "ownerUid" : "renterUid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .get().addOnCompleteListener(task -> {
                    List<Appointment> list = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Appointment appointment = document.toObject(Appointment.class);
                            list.add(appointment);
                        }
                        appointments.postValue(list);
                    }
                });
        return appointments;
    }

    public LiveData<Boolean> addCard(CreditCard creditCard) {
        final MutableLiveData<Boolean> liveData = new MutableLiveData();
        db.collection(Consts.CREDIT_CARDS_DB).document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .set(creditCard).addOnCompleteListener(task -> liveData.postValue(task.isSuccessful()));
        return liveData;
    }

    public LiveData<User> getUser() {
        final MutableLiveData<User> user = new MutableLiveData<>();
        db.collection(Consts.USERS_DB).document(firebaseAuth.getUid()).get().addOnSuccessListener(documentSnapshot -> user.postValue(documentSnapshot.toObject(User.class)));
        return user;
    }


    public LiveData<List<User>> getAllUsersParkOwners(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        final MutableLiveData<List<User>> useres = new MutableLiveData<>();
        db.collection(Consts.USERS_DB)
                .whereEqualTo("isOwner", true)
                .get().addOnCompleteListener(task -> {
                    List<User> list = new ArrayList<>();
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            User user = document.toObject(User.class);
                            user.setUid(document.getId());
                            if (user.location != null) {
                                list.add(user);
                            }
                        }
                        useres.postValue(list);
                    }
                });
        return useres;
    }


    public LiveData<CreditCard> getCard(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        final MutableLiveData<CreditCard> liveData = new MutableLiveData();
        db.collection(Consts.CREDIT_CARDS_DB).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            if (task.isSuccessful()) {
                CreditCard creditCard = task.getResult().toObject(CreditCard.class);
                liveData.postValue(creditCard);
            }

        });
        return liveData;
    }

    public MutableLiveData<List<String>> fetchAllNotAvaliableHours(ProgressBar progressBar, String date, String ownerUid) {
        progressBar.setVisibility(View.VISIBLE);
        MutableLiveData<List<String>> result = new MutableLiveData<>();
        db.collection(Consts.APPOINTEMNTS_DB)
                .whereEqualTo("ownerUid", ownerUid)
                .whereEqualTo("date", date)
                .get().addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    List<String> list = new ArrayList<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Appointment appointment = document.toObject(Appointment.class);
                            list.add(appointment.timeRange);
                        }
                        result.postValue(list);
                    }
                });
        return result;

    }
}
