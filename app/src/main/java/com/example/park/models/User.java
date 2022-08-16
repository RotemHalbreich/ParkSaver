package com.example.park.models;

import com.google.firebase.firestore.Exclude;

public class User {

    public String fullName;
    public String imageUrl;
    public String phoneNumber;
    public String location;
    public Boolean isOwner;
    public String adress;
    public String fromHour;
    public String toHour;

    @Exclude
    public String getId() {
        return uid;
    }

    @Exclude
    public String uid;

    @Exclude
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean isOwner) {
        this.isOwner = isOwner;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getFromHour() {
        return fromHour;
    }

    public void setFromHour(String fromHour) {
        this.fromHour = fromHour;
    }

    public String getToHour() {
        return toHour;
    }

    public void setToHour(String toHour) {
        this.toHour = toHour;
    }


    public User(String fullName, String imageUrl, String phoneNumber, String location, boolean isOwner, String adress, String fromHour, String toHour) {
        this.fullName = fullName;
        this.imageUrl = imageUrl;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.isOwner = isOwner;
        this.adress = adress;
        this.fromHour = fromHour;
        this.toHour = toHour;
    }

    public User() {
    }


    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


}
