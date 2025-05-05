package com.example.bookverse.viewmodels;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.bookverse.R;
import com.example.bookverse.activities.LoginActivity;
import com.example.bookverse.activities.SignupChooseImgActivity;
import com.example.bookverse.models.User;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class LoginViewModels extends AndroidViewModel {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final PreferenceManager preferenceManager;
//    private final SharedPreferences sharedPreferences;
    ObservableField<String> email = new ObservableField<>("");
    ObservableField<String> password = new ObservableField<>("");
    ObservableField<String> phoneNumber = new ObservableField<>("");
    ObservableField<String> username = new ObservableField<>("");
    private MutableLiveData<Boolean> isLoggingIn = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> loginSuccess = new MutableLiveData<>(false);
    private MutableLiveData<String> message = new MutableLiveData<>();

    private MutableLiveData<Boolean> isSignupIn = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> signupSuccess = new MutableLiveData<>(false);
    private MutableLiveData<String> signupMessage = new MutableLiveData<>();

    private MutableLiveData<Bitmap> imageBitmap = new MutableLiveData<>();
    private MutableLiveData<String> encodedImage = new MutableLiveData<>();
    private MutableLiveData<String> confirmPass = new MutableLiveData<>("");
    private MutableLiveData<Boolean> isSignUpLoad = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isSignUpSuccess = new MutableLiveData<>(false);
    private MutableLiveData<String> signUpMessage = new MutableLiveData<>("");

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

    public ObservableField<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(ObservableField<String> phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public MutableLiveData<String> getConfirmPass() {
        return confirmPass;
    }

    public void setConfirmPass(MutableLiveData<String> confirmPass) {
        this.confirmPass = confirmPass;
    }

    public MutableLiveData<String> getEncodedImage() {
        return encodedImage;
    }

    public MutableLiveData<Bitmap> getImageBitmap() {
        return imageBitmap;
    }

    public void setEncodedImage(MutableLiveData<String> encodedImage) {
        this.encodedImage = encodedImage;
    }

    public void setImageBitmap(MutableLiveData<Bitmap> imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public MutableLiveData<Boolean> getIsLoggingIn() {
        return isLoggingIn;
    }

    public void setIsLoggingIn(MutableLiveData<Boolean> isLoggingIn) {
        this.isLoggingIn = isLoggingIn;
    }

    public MutableLiveData<Boolean> getIsSignupIn() {
        return isSignupIn;
    }

    public void setIsSignupIn(MutableLiveData<Boolean> isSignupIn) {
        this.isSignupIn = isSignupIn;
    }

    public MutableLiveData<Boolean> getLoginSuccess() {
        return loginSuccess;
    }

    public void setLoginSuccess(MutableLiveData<Boolean> loginSuccess) {
        this.loginSuccess = loginSuccess;
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }

    public void setMessage(MutableLiveData<String> message) {
        this.message = message;
    }

    public MutableLiveData<String> getSignupMessage() {
        return signupMessage;
    }

    public void setSignupMessage(MutableLiveData<String> signupMessage) {
        this.signupMessage = signupMessage;
    }

    public MutableLiveData<Boolean> getSignupSuccess() {
        return signupSuccess;
    }

    public void setSignupSuccess(MutableLiveData<Boolean> signupSuccess) {
        this.signupSuccess = signupSuccess;
    }

    public MutableLiveData<Boolean> getIsSignUpLoad() {
        return isSignUpLoad;
    }

    public void setIsSignUpLoad(MutableLiveData<Boolean> isSignUpLoad) {
        this.isSignUpLoad = isSignUpLoad;
    }

    public MutableLiveData<Boolean> getIsSignUpSuccess() {
        return isSignUpSuccess;
    }

    public void setIsSignUpSuccess(MutableLiveData<Boolean> isSignUpSuccess) {
        this.isSignUpSuccess = isSignUpSuccess;
    }

    public MutableLiveData<String> getSignUpMessage() {
        return signUpMessage;
    }

    public void setSignUpMessage(MutableLiveData<String> signUpMessage) {
        this.signUpMessage = signUpMessage;
    }

    public void login() {
        // Kiểm tra dữ liệu đầu vào
        if (email == null || password == null) {
            message.setValue("Email or password cannot be null!");
            loginSuccess.setValue(false);
            return;
        }

        isLoggingIn.setValue(true);
        User user = new User(email.get(), password.get());
        if (user.isValidEmail() && user.isValidPassword()) {
            firestore.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.KEY_EMAIL, email.get())
                    .whereEqualTo(Constants.KEY_PASSWORD, password.get())
                    .get()
                    .addOnCompleteListener(task -> {
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

    public void signup() {
        if (email == null || password == null || phoneNumber == null || username == null) {
            message.setValue("Information cannot be null!");
            signupSuccess.setValue(false);
            return;
        }

        User user = new User(username.get(), email.get(), password.get(), phoneNumber.get());
        if (user.isValidUseName() && user.isValidEmail() && user.isValidPassword() && user.isValidPhoneNumber()) {
            isSignupIn.setValue(true);
            Task<QuerySnapshot> queryUserName = firestore.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo(Constants.KEY_NAME, username.get())
                    .get();
            Task<QuerySnapshot> queryEmail = firestore.collection(Constants.KEY_EMAIL)
                    .whereEqualTo(Constants.KEY_EMAIL, email.get())
                    .get();
            Tasks.whenAllComplete(queryUserName, queryEmail).addOnCompleteListener(task ->{
                boolean checkUsename = queryUserName.getResult().isEmpty();
                boolean checkEmail = queryEmail.getResult().isEmpty();
                if(!checkUsename){
                    signupMessage.postValue("Username Invalid");
                    isSignupIn.postValue(false);
                    signupSuccess.postValue(false);
                } else if(!checkEmail){
                    signupMessage.postValue("Email Invalid");
                    isSignupIn.postValue(false);
                    signupSuccess.postValue(false);
                } else{
                    signupSuccess.postValue(true);
                }
            });
        } else {
            signupSuccess.postValue(false);
            signupMessage.postValue("Invalid information format");
            isSignupIn.postValue(false);
        }
    }

    public void signupChooseImg(){
        HashMap<String, String> newuser = new HashMap<>();
        newuser.put(Constants.KEY_NAME, username.get());
        newuser.put(Constants.KEY_EMAIL, email.get());
        newuser.put(Constants.KEY_PASSWORD, password.get());
        newuser.put(Constants.KEY_PHONE, phoneNumber.get());
        newuser.put(Constants.KEY_IMAGE, encodedImage.getValue());
        isSignUpLoad.setValue(true);
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .add(newuser)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
//                        Toast.makeText(SignupChooseImgActivity.this, "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
                        isSignUpLoad.postValue(false);
                        isSignUpSuccess.postValue(true);
                        signUpMessage.postValue("Sign up success!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(getApplicationContext(), "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show();
                        isSignUpLoad.postValue(false);
                        isSignUpSuccess.postValue(false);
                        signUpMessage.postValue("Sign up failure!");
                    }
                });

    }

    public void selectImage (Uri imageUri) {
        if (imageUri == null) return;
        new Thread(() -> {
            try {
                InputStream inputStream = getApplication().getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if(bitmap != null){
                    imageBitmap.postValue(bitmap);
                }
                else{
                    bitmap = getBitmapFromResource(getApplication(), R.drawable.background_default_user);
                    encodedImage.postValue(enCodeImage(bitmap));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
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
