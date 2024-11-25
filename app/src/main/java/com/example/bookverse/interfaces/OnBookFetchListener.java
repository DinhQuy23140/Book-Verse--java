package com.example.bookverse.interfaces;

import android.util.Log;

import com.example.bookverse.Class.Book;
import com.example.bookverse.utilities.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.Map;

public interface OnBookFetchListener {
    void onBookFetched(Book book);

    default void getBook(String id, OnBookFetchListener listener) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(Constants.KEY_COLLECTION_BOOKS)
                .document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Map<String, Object> data = task.getResult().getData();
                        if (data != null) {
                            Gson gson = new Gson();
                            Book book = gson.fromJson(gson.toJson(data), Book.class);
                            listener.onBookFetched(book); // Gửi kết quả qua callback
                        } else {
                            listener.onBookFetched(null);
                        }
                    } else {
                        Log.e("Firestore", "Error fetching book", task.getException());
                        listener.onBookFetched(null);
                    }
                });
    }
}
