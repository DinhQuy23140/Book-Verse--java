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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookverse.AdapterCustom.HomeAdapterRecycle;
import com.example.bookverse.Class.Book;
import com.example.bookverse.Fragment.HomeFragment;
import com.example.bookverse.R;

import java.util.ArrayList;

public class ViewAllRecyclerView extends AppCompatActivity {

    TextView titleViewAll;
    RecyclerView recycleBook;
    ArrayList<Book> listBook;
    HomeAdapterRecycle adapterRecycle;
    ImageView viewAllbtnBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_all_recycler_view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewAllbtnBack = findViewById(R.id.viewAllbtnBack);
        titleViewAll = findViewById(R.id.titleViewAll);
        recycleBook = findViewById(R.id.recycleBook);
        recycleBook.setLayoutManager(new LinearLayoutManager(this));

        Intent getIntent = getIntent();
        String value = getIntent.getStringExtra("key");
        switch (value){
            case "allBook":{
                titleViewAll.setText(R.string.ViewAllBookTitle);
                break;
            }
        }
        int[] listPathImage = {R.drawable.favorite, R.drawable.theme_alfomedeiros18926843,
                R.drawable.theme_exel, R.drawable.theme_mati12509859, R.drawable.theme_mdsnmdsnmdsn1831234,
                R.drawable.theme_padrinan19670, R.drawable.theme_pixabay459277, R.drawable.background_app};
        String[] authorBook = {"book1", "book2", "book3", "book4", "book5", "book6", "book7", "book8"};
        listBook = new ArrayList<>();
        for (int i = 0; i < listPathImage.length; i++){
            Book item = new Book(authorBook[i], listPathImage[i]);
            listBook.add(item);
        }
        adapterRecycle = new HomeAdapterRecycle(this, listBook);
        recycleBook.setAdapter(adapterRecycle);

        viewAllbtnBack.setOnClickListener(viewBack ->{
            finish();
        });
    }
}