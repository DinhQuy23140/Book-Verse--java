package com.example.bookverse.viewmodels;
import android.app.Application;

import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bookverse.BR;
import com.example.bookverse.R;
import com.example.bookverse.models.User;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.PreferenceManager;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginViewModels extends AndroidViewModel {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final PreferenceManager preferenceManager;
    public MutableLiveData<Boolean> isLoggingIn = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>(false);
    public MutableLiveData<String> message = new MutableLiveData<>();
    public ObservableField<Boolean> isSucces;
    ObservableField<String> email = new ObservableField<>("");
    ObservableField<String> password = new ObservableField<>("");

    public LoginViewModels(Application application, PreferenceManager preferenceManager) {
        super(application);
        this.preferenceManager = preferenceManager;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public void login() {
        // Kiểm tra dữ liệu đầu vào
        if (email == null || password == null) {
            message.setValue("Email or password cannot be null!");
            loginSuccess.setValue(false);
            return;
        }

        // Đặt trạng thái đang đăng nhập
        isLoggingIn.setValue(true);

        User user = new User(email.get(), password.get());
        if (user.isValidEmail() && user.isValidPassword()) {
            firestore.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.KEY_EMAIL, email.get())
                    .whereEqualTo(Constants.KEY_PASSWORD, password.get())
                    .get()
                    .addOnCompleteListener(task -> {
                        // Chạy trong background thread
                        isLoggingIn.postValue(false); // Đặt lại trạng thái không còn đăng nhập

                        if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                            // Cập nhật thông tin người dùng
                            preferenceManager.putString(Constants.KEY_EMAIL, email.get());
                            preferenceManager.putString(Constants.KEY_PASSWORD, password.get());
                            preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                            preferenceManager.putString(Constants.KEY_IMAGE, task.getResult().getDocuments().get(0).getString("image"));

                            // Thông báo thành công
                            loginSuccess.postValue(true);
                            message.postValue("Login success");
                        } else {
                            // Thông báo thất bại
                            loginSuccess.postValue(false);
                            message.postValue("Email or password is incorrect!");
                        }
                    });
        } else {
            loginSuccess.postValue(false);
            message.postValue("Invalid email or password format");
            isLoggingIn.postValue(false);
        }
    }
}

