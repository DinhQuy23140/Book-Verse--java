package com.example.bookverse.models;

import android.util.Patterns;

public class User {
    private String username, email, password, phoneNumber, image, idUser, birthDay;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String birthDay, String email, String idUser, String image, String password, String phoneNumber, String username) {
        this.birthDay = birthDay;
        this.email = email;
        this.idUser = idUser;
        this.image = image;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.username = username;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isValidEmail() {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidPassword() {
        return !password.isEmpty() && password.length() >= 8;
    }

    public boolean isValidPhoneNumber() {
        return phoneNumber.length() >= 10 && Patterns
                .PHONE.matcher(phoneNumber).matches();
    }
}
