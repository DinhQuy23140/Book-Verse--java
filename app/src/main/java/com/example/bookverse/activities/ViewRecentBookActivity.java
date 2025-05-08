package com.example.bookverse.activities;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bookverse.AdapterCustom.BookAdapter;
import com.example.bookverse.AdapterCustom.HomeAdapterRecycle;
import com.example.bookverse.models.Book;
import com.example.bookverse.R;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.GridSpaceDecoration;
import com.example.bookverse.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewRecentBookActivity extends AppCompatActivity {

    LinearLayout layoutmain;
    RecyclerView recent_recyclerBook;
    ArrayList<Book> listBook;
    BookAdapter adapter;
    PreferenceManager preferenceManager;
    FirebaseFirestore firebaseFirestore;
    SharedPreferences sharedPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

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

        layoutmain = findViewById(R.id.main);
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
        preferenceManager = new PreferenceManager(getApplicationContext());
        String email = preferenceManager.getString(Constants.KEY_EMAIL);
        Toast.makeText(ViewRecentBookActivity.this, "Email: " + email, Toast.LENGTH_SHORT).show();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recent_recyclerBook = findViewById(R.id.recent_recyclerBook);
        int numColmn;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int dp = (int) (metrics.widthPixels / metrics.density);
        if (dp > 600) numColmn = 4;
        else numColmn = 2;
        recent_recyclerBook.setLayoutManager(new GridLayoutManager(getApplicationContext(), numColmn));
        recent_recyclerBook.addItemDecoration(new GridSpaceDecoration(numColmn, 10));
        listBook = new ArrayList<>();
        adapter = new BookAdapter(getApplicationContext(), listBook);
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

    public void updateBackground(int pathTheme) {
        Glide.with(this)
                .load(pathTheme)  // Load trực tiếp từ pathTheme
                .placeholder(R.drawable.ic_default_image)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        int layoutWidth = layoutmain.getWidth();
                        int layoutHeight = layoutmain.getHeight();

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
                        layoutmain.setBackground(bitmapDrawable);  // Sử dụng Drawable từ Glide
                        layoutmain.setBackground(resource);  // Sử dụng Drawable từ Glide
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        layoutmain.setBackgroundResource(R.drawable.ic_error_load_image);
                    }
                });
    }

    public Bitmap drawableToBitmap(Drawable drawable){
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