package com.example.bookverse.viewmodels;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bookverse.models.User;
import com.example.bookverse.repository.UserRepository;
import com.example.bookverse.sharepreference.SharedPrefManage;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.Map;

public class LoginViewModel extends ViewModel {
    private FirebaseFirestore firestore;
    private SharedPrefManage sharedPrefManage;
    private UserRepository userRepository;
    MutableLiveData<Boolean> isLogin = new MutableLiveData<>(false);
    MutableLiveData<Boolean> isSignup = new MutableLiveData<>(false);
    MutableLiveData<String> messageLogin = new MutableLiveData<>("");
    MutableLiveData<String> messageSignup = new MutableLiveData<>("");
    Context context;

    MutableLiveData<String> strImg = new MutableLiveData<>("");

    public LoginViewModel(UserRepository userRepository, Context context) {
        this.userRepository = userRepository;
        this.context = context;
        sharedPrefManage = new SharedPrefManage(context);
        firestore = FirebaseFirestore.getInstance();
    }

    private MutableLiveData<Bitmap> imageBitmap = new MutableLiveData<>();
    private MutableLiveData<String> encodedImage = new MutableLiveData<>();
    MutableLiveData<Boolean> checkEmail = new MutableLiveData<>(false);
    MutableLiveData<Bitmap> imgBitMap = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIsLogin() {
        return isLogin;
    }

    public MutableLiveData<String> getMessageLogin() {
        return messageLogin;
    }

    public MutableLiveData<Boolean> getIsSignup() {
        return isSignup;
    }

    public MutableLiveData<String> getMessageSignup() {
        return messageSignup;
    }

    public MutableLiveData<Bitmap> getImageBitmap() {
        return imageBitmap;
    }

    public void login(User user, boolean isSaveInf) {
        if (!user.isValidEmail()) {
            isLogin.setValue(false);
            messageLogin.setValue("Email không hợp lệ");
        } else if (!user.isValidPassword()) {
            isLogin.setValue(false);
            messageLogin.setValue("Mật không khó hợp lệ");
        } else {
            userRepository.loginTest(user, isSaveInf, success -> {
                if (success) {
                    isLogin.setValue(true);
                    messageLogin.setValue("Đăng nhập thành công");
                } else {
                    isLogin.setValue(false);
                    messageLogin.setValue("Email hoặc mật khẩu không hợp lệ");
                }
            });
        }
    }

    public void signupTest(Map<String, String> user ) {
        userRepository.signup(user, result -> {
            if (result) {
                isSignup.setValue(true);
                messageSignup.setValue("Đăng kí thành công");
            } else {
                isSignup.setValue(false);
                messageSignup.setValue("Kiểm tra lại thông tin");
            }
        });
    }


    public void selectImage (Bitmap bitmap) {
        String imgStr = enCodeImage(bitmap);
        strImg.setValue(imgStr);
    }

    private String enCodeImage(Bitmap bitmap){
        //set with
        int previewWith = 150;
        //set height
        int previewHeight = bitmap.getHeight() * previewWith / bitmap.getWidth();
        //scale image
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWith, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public Bitmap getBitmapFromResource(Context context, int id){
        return BitmapFactory.decodeResource(context.getResources(), id);
    }
}
