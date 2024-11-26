package com.example.bookverse.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookverse.AdapterCustom.HomeAdapterRecycle;
import com.example.bookverse.AdapterCustom.ViewAllAdapter;
import com.example.bookverse.Class.Book;
import com.example.bookverse.R;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewRecentBookActivity extends AppCompatActivity {

    RecyclerView recent_recyclerBook;
    ArrayList<Book> listBook;
    HomeAdapterRecycle adapter;
    PreferenceManager preferenceManager;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_recent_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferenceManager = new PreferenceManager(getApplicationContext());
        String email = preferenceManager.getString(Constants.KEY_EMAIL);
        Toast.makeText(ViewRecentBookActivity.this, "Email: " + email, Toast.LENGTH_SHORT).show();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recent_recyclerBook = findViewById(R.id.recent_recyclerBook);
        recent_recyclerBook.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        listBook = new ArrayList<>();
        adapter = new HomeAdapterRecycle(getApplicationContext(), listBook);
        getData(email);
        recent_recyclerBook.setAdapter(adapter);
    }

    void getData(String email){
        firebaseFirestore.collection(Constants.KEY_COLLECTION_RECENTREAD)
                .document(email)
                .get()
                .addOnCompleteListener(task ->{
                    if(task.isSuccessful() && task.getResult()!= null){
                        List<Object> listBookId = (List<Object>) task.getResult().get("BookId");
                        if(listBookId != null && !listBookId.isEmpty()){
                            List<String> BookIdStr = new ArrayList<>();
                            for (Object book : listBookId){
                                if( book instanceof Long){
                                    BookIdStr.add(String.valueOf(book));
                                } else if(book instanceof String){
                                    BookIdStr.add((String) book);
                                }
                            }

                            firebaseFirestore.collection(Constants.KEY_COLLECTION_BOOKS)
                                    .whereIn(FieldPath.documentId(), BookIdStr)
                                    .get()
                                    .addOnCompleteListener(taskGetBook->{
                                        if (taskGetBook.isSuccessful() && !taskGetBook.getResult().isEmpty()){
                                            for(DocumentSnapshot documentSnapshot : taskGetBook.getResult()){
                                                Gson gson = new Gson();
                                                Map<String, Object> data = documentSnapshot.getData();
                                                Book book = gson.fromJson(gson.toJson(data), Book.class);
                                                listBook.add(book);
                                            }
                                            adapter.notifyDataSetChanged();
                                            Toast.makeText(getApplicationContext(), "Size: " + listBook.size(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }
}