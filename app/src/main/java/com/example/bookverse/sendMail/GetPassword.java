package com.example.bookverse.sendMail;

import com.example.bookverse.utilities.Constants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GetPassword {
    public interface FirebaseCallback {
        void onCallback(String password);
    }

    public void getPassword( String email, FirebaseCallback firebaseCallback) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, email)
                .get()
                .addOnCompleteListener(task->{
                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        String password = documentSnapshot.getString(Constants.KEY_PASSWORD);
                        firebaseCallback.onCallback(password);
                    }
                    else{
                        firebaseCallback.onCallback(null);
                    }
                });
    }
}

