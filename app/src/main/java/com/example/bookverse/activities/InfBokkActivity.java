package com.example.bookverse.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bookverse.Class.Book;
import com.example.bookverse.Class.Person;
import com.example.bookverse.R;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.PreferenceManager;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InfBokkActivity extends AppCompatActivity {
    LinearLayout layout;
    ImageView infB_back, infB_imageB, infB_favoriteB, infB_btndown;
    TextView infB_titleB, infB_authorB, infB_countDown;
    ConstraintLayout infB_readB;
    PreferenceManager preferenceManager;
    SharedPreferences sharedPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

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
        layout = findViewById(R.id.main);
        sharedPreferences = getSharedPreferences("MySharePref", MODE_PRIVATE);
        preferenceChangeListener = (sharedPreferences, key)->{
            if(key.equals("pathTheme")){
                int newpathTheme = sharedPreferences.getInt("pathTheme", R.drawable.background_app);
                updateBackground(newpathTheme);
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        int pathTheme = sharedPreferences.getInt("pathTheme", R.drawable.background_app);
        updateBackground(pathTheme);

        infB_back = findViewById(R.id.infB_back);
        infB_imageB = findViewById(R.id.infB_imageB);
        infB_favoriteB = findViewById(R.id.infB_favoriteB);
        infB_btndown = findViewById(R.id.infB_btndown);
        infB_titleB = findViewById(R.id.infB_titleB);
        infB_authorB = findViewById(R.id.infB_authorB);
        infB_countDown = findViewById(R.id.infB_countDown);
        infB_readB = findViewById(R.id.infB_readB);
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
        preferenceManager = new PreferenceManager(getApplicationContext());
        String email = preferenceManager.getString(Constants.KEY_EMAIL);
        infB_readB.setOnClickListener(infB_readB -> {
            if(email != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("BookId", FieldValue.arrayUnion(getBook.getId()));
                firebaseFirestore.collection(Constants.KEY_COLLECTION_RECENTREAD)
                        .document(String.valueOf(email))
                        .set(data, SetOptions.merge());
            }
            //Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(InfBokkActivity.this, ReadBookActivity.class);
            intent.putExtra("jsonBook", jsonBook);
            startActivity(intent);
        });
    }

    public String getUrlImg(Map<String, String> format){
        return format.get("image/jpeg");
    }

    public void updateBackground(int pathTheme) {
        Glide.with(this)
                .load(pathTheme)  // Load trực tiếp từ pathTheme
                .placeholder(R.drawable.ic_default_image)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        int layoutWidth = layout.getWidth();
                        int layoutHeight = layout.getHeight();

                        Bitmap bitmap = drawableToBitmap(resource);

                        int imageWidth = bitmap.getWidth();
                        int imageHeight = bitmap.getHeight();

                        float scaleX = (float)layoutWidth/imageWidth;
                        float scaleY = (float)layoutHeight/imageHeight;
                        float scale = Math.min(scaleX, scaleY);

                        int newWidth = Math.round(imageWidth * scale);
                        int newHeight = Math.round(imageHeight * scale);

                        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), scaleBitmap);
                        layout.setBackground(bitmapDrawable);  // Sử dụng Drawable từ Glide
                        layout.setBackground(resource);  // Sử dụng Drawable từ Glide
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        layout.setBackgroundResource(R.drawable.ic_error_load_image);
                    }
                });
    }

    private Bitmap drawableToBitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}