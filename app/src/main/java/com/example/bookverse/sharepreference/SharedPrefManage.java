package com.example.bookverse.sharepreference;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.bookverse.utilities.Constants;

public class SharedPrefManage {
    private SharedPreferences sharedPreferences;

    public SharedPrefManage (Context context) {
        sharedPreferences = context.getSharedPreferences(Constants.KEY_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public void savaeUserName(String username) {
        sharedPreferences.edit().putString(Constants.KEY_NAME, username).apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(Constants.KEY_NAME, "");
    }

    public void saveEmail(String email) {
        sharedPreferences.edit().putString(Constants.KEY_EMAIL, email).apply();
    }

    public String getEmail() {
        return sharedPreferences.getString(Constants.KEY_EMAIL, "");
    }

    public void savePassword(String password) {
        sharedPreferences.edit().putString(Constants.KEY_PASSWORD, password).apply();
    }

    public String getPassword() {
        return sharedPreferences.getString(Constants.KEY_PASSWORD, "");
    }

    public void setIsLogin(boolean isLogin) {
        sharedPreferences.edit().putBoolean(Constants.KEY_IS_LOGIN, isLogin).apply();
    }

    public boolean isLogin() {
        return sharedPreferences.getBoolean(Constants.KEY_IS_LOGIN, false);
    }

    public void setSaveInf(boolean isSaveInf) {
        sharedPreferences.edit().putBoolean(Constants.KEY_IS_SAVE_INF, isSaveInf).apply();
    }

    public boolean isSaveInf() {
        return sharedPreferences.getBoolean(Constants.KEY_IS_SAVE_INF, false);
    }


    public void saveImg(String img) {
        sharedPreferences.edit().putString(Constants.KEY_IMAGE, img).apply();
    }

    public String getImg() {
        return sharedPreferences.getString(Constants.KEY_IMAGE, "");
    }

    public void savePhone(String phone) {
        sharedPreferences.edit().putString(Constants.KEY_PHONE, phone).apply();
    }

    public String getPhone() {
        return sharedPreferences.getString(Constants.KEY_PHONE, "");
    }

    public String getDoB() {return sharedPreferences.getString(Constants.KEY_DOB, ""); }

    public void saveDoB(String dob) {sharedPreferences.edit().putString(Constants.KEY_DOB, dob).apply();}

    public void saveSex(String sex) {sharedPreferences.edit().putString(Constants.KEY_SEX, sex).apply();}

    public String getSex() {return sharedPreferences.getString(Constants.KEY_SEX, "");}
}
