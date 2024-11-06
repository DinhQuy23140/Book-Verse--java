package com.example.bookverse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.bookverse.Class.Book;
import com.example.bookverse.Class.Person;
import com.example.bookverse.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class InfBokkActivity extends AppCompatActivity {
    ImageView infB_back, infB_imageB, infB_favoriteB, infB_btndown;
    TextView infB_titleB, infB_authorB, infB_countDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inf_bokk);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        infB_back = findViewById(R.id.infB_back);
        infB_imageB = findViewById(R.id.infB_imageB);
        infB_favoriteB = findViewById(R.id.infB_favoriteB);
        infB_btndown = findViewById(R.id.infB_btndown);
        infB_titleB = findViewById(R.id.infB_titleB);
        infB_authorB = findViewById(R.id.infB_authorB);
        infB_countDown = findViewById(R.id.infB_countDown);
        Gson gson = new Gson();
        String jsonBook = getIntent().getStringExtra("jsonBook");
        Book getBook = gson.fromJson(jsonBook, Book.class);
        assert getBook != null;
        String title = getBook.getTitle();
        ArrayList<Person> authors = getBook.getAuthors();
        StringBuilder authorList = new StringBuilder();
        for(int i = 0; i < authors.size(); i++){
            authorList.append(authors.get(i).getName());
            if (i < authors.size()-1) authorList.append(", ");
        }
        float dowCount = getBook.getDownload_count();
        String UrlImage = getUrlImg(getBook.getFormats());

        Glide.with(this).load(UrlImage)
                        .placeholder(R.drawable.ic_default_image)
                                .error(R.drawable.ic_error_load_image)
                                        .into(infB_imageB);
        infB_titleB.setText(title);
        infB_authorB.setText(authorList);
        infB_countDown.setText(String.valueOf(dowCount));

        infB_back.setOnClickListener(v->finish());
    }

    public String getUrlImg(Map<String, String> format){
        return format.get("image/jpeg");
    }
}