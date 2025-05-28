package com.example.bookverse.viewmodels;

import android.content.Context;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bookverse.R;
import com.example.bookverse.repository.UserRepository;
import com.example.bookverse.sharepreference.SharedPrefManage;
import com.example.bookverse.utilities.Constants;

import java.util.Map;

public class InfUserViewModel extends ViewModel {
    SharedPrefManage sharedPrefManage;
    Context context;
    UserRepository userRepository;
    MutableLiveData<String> username = new MediatorLiveData<>("");
    MutableLiveData<String> emailVal = new MediatorLiveData<>("");
    MutableLiveData<String> phoneNumber = new MediatorLiveData<>("");
    MutableLiveData<String> image = new MediatorLiveData<>("");
    MutableLiveData<String> dob = new MediatorLiveData<>("");
    MutableLiveData<Boolean> isSuccess = new MutableLiveData<>(false);
    MutableLiveData<String> message = new MediatorLiveData<>("");

    public InfUserViewModel(Context context, UserRepository userRepository) {
        this.context = context;
        this.sharedPrefManage = new SharedPrefManage(context);
        this.userRepository = userRepository;
    }

    public MutableLiveData<String> getDob() {
        return dob;
    }

    public MutableLiveData<String> getEmailVal() {
        return emailVal;
    }

    public MutableLiveData<String> getImage() {
        return image;
    }

    public MutableLiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public MutableLiveData<String> getUsername() {
        return username;
    }

    public MutableLiveData<Boolean> getIsSuccess() {
        return isSuccess;
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public void loadUser() {
        username.setValue(userRepository.getUsername());
        emailVal.setValue(userRepository.getEmailVal());
        phoneNumber.setValue(userRepository.getPhoneNumber());
        image.setValue(userRepository.getImage());
        dob.setValue(userRepository.getDob());
    }

    public void updateUser(Map<String, Object> user) {
        userRepository.updateUser(user, result -> {
            isSuccess.setValue(result);
            if (result) {
                message.setValue(context.getString(R.string.notifiUpdateSuccess));
            } else {
                message.setValue(context.getString(R.string.notifiFailure));
            }
        });
    }

    public void setIsLogin(Boolean status) {
        userRepository.setLogin(status);
    }
}
