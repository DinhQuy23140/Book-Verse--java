package com.example.bookverse.repository;

import android.content.Context;
import android.util.Log;

import com.example.bookverse.models.Book;
import com.example.bookverse.sharepreference.SharedPrefManage;
import com.example.bookverse.utilities.Constants;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookRepository {
    FirebaseFirestore firebaseFirestore;
    SharedPrefManage sharedPrefManage;
    Context context;

    public interface CallBack {
        void onResult(List<Book> result);
    }

    public BookRepository(Context context) {
        this.context = context;
        firebaseFirestore = FirebaseFirestore.getInstance();
        sharedPrefManage = new SharedPrefManage(context);
    }

    public void getAllBook(CallBack callBack) {
        firebaseFirestore.collection(Constants.KEY_COLLECTION_BOOKS)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() & !task.getResult().isEmpty()) {
                        List<Book> listAllBook = new ArrayList<>();
                        Gson gson = new Gson();
                        for (DocumentSnapshot documentSnapshot : task.getResult()){
                            Map<String, Object> data = documentSnapshot.getData();
                            Book book = gson.fromJson(gson.toJson(data), Book.class);
                            listAllBook.add(book);
                        }
                        callBack.onResult(listAllBook);
                    } else {
                        callBack.onResult(new ArrayList<>());
                    }
                });
    }

    public void getBookByType() {

    }

    public void getRecentBook(CallBack callBack) {
        String email = sharedPrefManage.getEmail();
        firebaseFirestore.collection(Constants.KEY_COLLECTION_RECENTREAD)
                .document(email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<Book> listRecentBook = new ArrayList<>();
                        List<Object> BookId = (List<Object>) task.getResult().get("BookId");
                        if (BookId != null && !BookId.isEmpty()) {
                            // Chuyển đổi tất cả các giá trị trong BookId thành String
                            List<String> stringBookId = new ArrayList<>();
                            for (Object id : BookId) {
                                if (id instanceof Long) {
                                    // Nếu ID là Long, chuyển thành String
                                    stringBookId.add(String.valueOf(id));
                                } else if (id instanceof String) {
                                    stringBookId.add((String) id);
                                }
                            }

                            // Tiến hành truy vấn với danh sách BookId dạng String
                            firebaseFirestore.collection(Constants.KEY_COLLECTION_BOOKS)
                                    .whereIn(FieldPath.documentId(), stringBookId)
                                    .get()
                                    .addOnCompleteListener(bookTask -> {
                                        if (bookTask.isSuccessful() && bookTask.getResult() != null) {
                                            for (DocumentSnapshot documentSnapshot : bookTask.getResult()) {
                                                Gson gson = new Gson();
                                                Map<String, Object> data = documentSnapshot.getData();
                                                if (data != null) {
                                                    Book book = gson.fromJson(gson.toJson(data), Book.class);
                                                    listRecentBook.add(book);
                                                }
                                            }
                                            callBack.onResult(listRecentBook);
                                            // Thông báo về số lượng sách đã được tải
                                            //Toast.makeText(getContext(), "Size: " + listRecentBook.size(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            callBack.onResult(new ArrayList<>());
                                            Log.e("Firestore", "Error fetching books", bookTask.getException());
                                        }
                                    });
                        } else {
                            Log.w("Firestore", "No BookId found in the document");
                        }
                    } else {
                        Log.e("Firestore", "Error fetching recent read document", task.getException());
                    }
                });
    }

    public void getBookById() {

    }

    public void getViralBook(CallBack callBack) {
        firebaseFirestore.collection(Constants.KEY_COLLECTION_BOOKS)
                .orderBy("download_count")
                .limit(100)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && !task.getResult().isEmpty()){
                        List<Book> listMostPopular = new ArrayList<>();
                        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                            Gson gson = new Gson();
                            Map<String, Object> data = documentSnapshot.getData();
                            Book book = gson.fromJson(gson.toJson(data), Book.class);
                            listMostPopular.add(book);
                        }
                        callBack.onResult(listMostPopular);
                    } else {
                        callBack.onResult(new ArrayList<>());
                    }
                });
    }

    public void getFavoriteBook() {

    }
}
