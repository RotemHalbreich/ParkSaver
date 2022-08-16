package com.example.park.models;

import android.text.TextUtils;

import java.util.Calendar;
import java.util.Locale;

public class CreditCard {
    private String creditCardNumber;
    private String expirationDate;
    private String cvv;
    private String firstName;
    private String lastName;
    private String idClient;

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String id) {
        this.idClient = id;
    }


    public CreditCard() {
    }

    public CreditCard(String creditCardNumber, String expirationDate, String cvv, String firstName, String lastName, String idClient) {
        this.creditCardNumber = creditCardNumber;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
        this.firstName = firstName;
        this.lastName = lastName;
        this.idClient = idClient;
    }

    public static boolean isValidCardNumber(String cardNumber) {
        return !TextUtils.isEmpty(cardNumber) && cardNumber.length() == 16;
    }

    public static boolean isClientIdValid(String id) {
        return !TextUtils.isEmpty(id) && id.length() == 9;
    }


    public static boolean isValidExpirationDate(String expirationDate) {
        String[] expDateParts = expirationDate.split("/");
        if (expDateParts.length != 2 || expDateParts[0].isEmpty() || expDateParts[1].isEmpty()) {
            return false;
        }
        int expMonth = Integer.parseInt(expDateParts[0]);
        int expYear = Integer.parseInt(expDateParts[1]);
        if (!(expMonth >= 1 && expMonth <= 12)) {
            return false;
        }
        Calendar today = Calendar.getInstance(Locale.getDefault());
        int todayMonth = today.get(Calendar.MONTH) + 1;
        int todayYear = today.get(Calendar.YEAR) % 100;
        return (expYear > todayYear) || (expYear == todayYear && expMonth >= todayMonth);
    }

    public static boolean isValidCvv(String cardNumber, String cvv) {
        // see https://www.cvvnumber.com/cvv.html as linked to in the specs
        return !TextUtils.isEmpty(cvv) && cvv.length() == 3;
    }

    public static String cleanName(String firstOrLastName) {
        // strip leading and trailing spaces, change any consecutive multiple internal spaces to one
        return firstOrLastName.trim().replaceAll("\\s+", " ");
    }

    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
