package com.example.bookverse.repository;

import android.content.Context;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bookverse.R;
import com.example.bookverse.models.User;
import com.example.bookverse.sharepreference.SharedPrefManage;
import com.example.bookverse.utilities.Constants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

public class UserRepository {
    FirebaseFirestore firestore;
    SharedPrefManage sharedPrefManage;
    Context context;

    public interface CallBack {
        void onResult(Boolean result);
    }



    public UserRepository(Context context) {
        this.context = context;
        firestore = FirebaseFirestore.getInstance();
        sharedPrefManage = new SharedPrefManage(context);
    }

    public String getDob() {
        return sharedPrefManage.getDoB();
    }

    public String getEmailVal() {
        return sharedPrefManage.getEmail();
    }

    public String getImage() {
        return sharedPrefManage.getImg();
    }

    public String getPhoneNumber() {
        return sharedPrefManage.getPhone();
    }

    public String getUsername() {
        return sharedPrefManage.getUserName();
    }

    public void checkEmailisExits(String email, CallBack callback) {
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onResult(!task.getResult().isEmpty());
                    }
                });
    }


    public void loginTest(User user, boolean isSaveInf, CallBack callback) {
        String email = user.getEmail();
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(email)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        sharedPrefManage.saveEmail(documentSnapshot.getString(Constants.KEY_EMAIL));
                        sharedPrefManage.savePassword(documentSnapshot.getString(Constants.KEY_PASSWORD));
                        sharedPrefManage.setIsLogin(true);
                        sharedPrefManage.setSaveInf(isSaveInf);
                        sharedPrefManage.savaeUserName(documentSnapshot.getString(Constants.KEY_NAME));
                        sharedPrefManage.saveImg(documentSnapshot.getString(Constants.KEY_IMAGE));
                        sharedPrefManage.savePhone(documentSnapshot.getString(Constants.KEY_PHONE));
                        sharedPrefManage.saveDoB(documentSnapshot.getString(Constants.KEY_DOB));
                        sharedPrefManage.saveSex(documentSnapshot.getString(Constants.KEY_SEX));
                        callback.onResult(true);
                    } else callback.onResult(false);
                })
                .addOnFailureListener(e -> callback.onResult(false));
    }

    public void signup(Map<String, String> user, CallBack result) {
        firestore.collection(Constants.KEY_COLLECTION_USERS)
                .document(user.get(Constants.KEY_EMAIL))
                .set(user)
                .addOnSuccessListener(documentReference -> result.onResult(true))
                .addOnFailureListener(e -> result.onResult(false));
    }

    public void forgotPassword(String email, CallBack callBack) {}

    public void updateUser(Map<String, Object> user, CallBack callback) {
        String email = sharedPrefManage.getEmail();
        if (!email.isEmpty()) {
            firestore.collection(Constants.KEY_COLLECTION_USERS)
                    .document(email)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.exists()) {
                            queryDocumentSnapshots.getReference().update(user);
                            sharedPrefManage.savaeUserName(Objects.requireNonNull(user.get(Constants.KEY_NAME)).toString());
                            sharedPrefManage.saveImg(user.get(Constants.KEY_IMAGE).toString());
                            sharedPrefManage.savePhone(Objects.requireNonNull(user.get(Constants.KEY_PHONE)).toString());
                            sharedPrefManage.saveEmail(Objects.requireNonNull(user.get(Constants.KEY_EMAIL)).toString());
                            sharedPrefManage.saveDoB(user.get(Constants.KEY_DOB).toString());
                            sharedPrefManage.saveSex(user.get(Constants.KEY_SEX).toString());
                            callback.onResult(true);
                        } else {
                            callback.onResult(false);
                        }
                    })
                    .addOnFailureListener(e -> callback.onResult(false));
        }
    }

    public void loadUser() {
//        String email = sharedPrefManage.getEmail();
//        firestore.collection(Constants.KEY_COLLECTION_USERS)
//                .whereEqualTo(Constants.KEY_EMAIL, email)
//                .get()
//                .addOnCompleteListener(task ->{
//                    if(task.isSuccessful() && !task.getResult().isEmpty()){
//                        String strImg = task.getResult().getDocuments().get(0).getString(Constants.KEY_IMAGE);
//                        String fullname = task.getResult().getDocuments().get(0).getString(Constants.KEY_NAME);
//                        Object phone = task.getResult().getDocuments().get(0).get(Constants.KEY_PHONE);
//                        if (strImg != null) {
//                            image.setValue(strImg);
//                        } else {
//                            image.setValue(null);
//                        }
//                        username.setValue(fullname);
//                        emailVal.setValue(email);
//                        phoneNumber.setValue(String.valueOf(phone));
//                        dob.setValue(task.getResult().getDocuments().get(0).getString(Constants.KEY_BIRTH));
//                    }
//                });
    }

}
