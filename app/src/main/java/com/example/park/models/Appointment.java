package com.example.park.models;

public class Appointment {

    public String ownerUid;
    public String nameOfOwner;
    public String telephoneOfOwner;

    public String renterUid;
    public String nameOfRenter;

    public String getTelephoneOfOwner() {
        return telephoneOfOwner;
    }

    public void setTelephoneOfOwner(String telephoneOfOwner) {
        this.telephoneOfOwner = telephoneOfOwner;
    }

    public String getTelephoneOfRenter() {
        return telephoneOfRenter;
    }

    public void setTelephoneOfRenter(String telephoneOfRenter) {
        this.telephoneOfRenter = telephoneOfRenter;
    }

    public String telephoneOfRenter;


    public String adress;

    public String date;
    public String timeRange;
    public Integer price;

    public Appointment() {

    }

    public Appointment(String ownerUid, String nameOfOwner, String renterUid, String nameOfRenter, String date, String timeRange, Integer price, String adress, String telephoneOfOwner, String telephoneOfRenter) {
        this.ownerUid = ownerUid;
        this.nameOfOwner = nameOfOwner;
        this.renterUid = renterUid;
        this.nameOfRenter = nameOfRenter;
        this.date = date;
        this.timeRange = timeRange;
        this.price = price;
        this.telephoneOfOwner = telephoneOfOwner;
        this.telephoneOfRenter = telephoneOfRenter;
        this.adress = adress;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }

    public String getNameOfOwner() {
        return nameOfOwner;
    }

    public void setNameOfOwner(String nameOfOwner) {
        this.nameOfOwner = nameOfOwner;
    }

    public String getRenterUid() {
        return renterUid;
    }

    public void setRenterUid(String renterUid) {
        this.renterUid = renterUid;
    }

    public String getNameOfRenter() {
        return nameOfRenter;
    }

    public void setNameOfRenter(String nameOfRenter) {
        this.nameOfRenter = nameOfRenter;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }


    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }
}
