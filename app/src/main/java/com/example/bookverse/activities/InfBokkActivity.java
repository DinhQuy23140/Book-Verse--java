package com.example.bookverse.activities;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.bookverse.models.Book;
import com.example.bookverse.models.Person;
import com.example.bookverse.R;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.PreferenceManager;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InfBokkActivity extends AppCompatActivity {
    LinearLayout layout;
    ImageView infB_back, infB_imageB, infB_favoriteB, infB_btndown;
    TextView infB_titleB, infB_authorB, infB_countDown;
    ConstraintLayout infB_readB;
    PreferenceManager preferenceManager;
    SharedPreferences sharedPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    String jsonBook;
    Book getBook;

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
        jsonBook = getIntent().getStringExtra("jsonBook");
        getBook = gson.fromJson(jsonBook, Book.class);
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
                        .document(email)
                        .set(data, SetOptions.merge());
            }
//            Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(InfBokkActivity.this, ReadBookActivity.class);
//            intent.putExtra("jsonBook", jsonBook);
//            startActivity(intent);
//            String url = getBook.getFormats().get("application/epub+zip");
//            downloadAndReadEpub(url);
            Intent intent = new Intent(this, EpubFolioActivity.class);
            intent.putExtra(Constants.KEY_EPUB_PATH, getBook.getFormats().get(Constants.KEY_TEXT_HTML));
            startActivity(intent);
        });
        isFavorite();

        infB_favoriteB.setOnClickListener(clickFavo -> {
            if(email != null){
                if(!infB_favoriteB.isActivated()){
                    infB_favoriteB.setImageResource(R.drawable.ic_favorite_click);
                    infB_favoriteB.setActivated(true);
                    Map<String, Object> data = new HashMap<>();
                    data.put("users", FieldValue.arrayUnion(email));
                    firebaseFirestore.collection(Constants.KEY_COLLECTION_BOOKS)
                            .document(String.valueOf(getBook.getId()))
                            .set(data, SetOptions.merge());
                }
                else{
                    infB_favoriteB.setImageResource(R.drawable.ic_favorite);
                    infB_favoriteB.setActivated(false);
                    firebaseFirestore.collection(Constants.KEY_COLLECTION_BOOKS)
                            .document(String.valueOf(getBook.getId()))
                            .update("users", FieldValue.arrayRemove(email));
                }
            }
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

    private void isFavorite(){
        firebaseFirestore.collection(Constants.KEY_COLLECTION_BOOKS)
                .document(String.valueOf(getBook.getId()))
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null){
                        List<String> users = (List<String>) task.getResult().get("users");
                        if(users != null){
                            Set<String> set = new HashSet<>(users);
                            if(set.contains(preferenceManager.getString(Constants.KEY_EMAIL))){
                                infB_favoriteB.setImageResource(R.drawable.ic_favorite_click);
                                infB_favoriteB.setActivated(true);
                            }
                        }
                    }
                });
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private void downloadAndReadEpub(String epubUrl) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(epubUrl));
        request.setTitle("Downloading book...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        // Đường dẫn lưu
        File epubFile = new File(getExternalFilesDir(null), "downloaded_book.epub");
        request.setDestinationUri(Uri.fromFile(epubFile));

        DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = dm.enqueue(request);

        // Đăng ký nhận sự kiện khi tải xong
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (id == downloadId) {
                    // Mở sách
                    Intent openIntent = new Intent(context, EpubFolioActivity.class);
                    openIntent.putExtra("epub_path", epubFile.getAbsolutePath());
                    startActivity(openIntent);
                    unregisterReceiver(this);
                }
            }
        };

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

}