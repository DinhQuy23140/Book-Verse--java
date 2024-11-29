package com.example.bookverse.activities;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bookverse.API.ApiService;
import com.example.bookverse.AdapterCustom.HomeAdapterRecycle;
import com.example.bookverse.AdapterCustom.ViewAllAdapter;
import com.example.bookverse.Class.ApiClient;
import com.example.bookverse.Class.Book;
import com.example.bookverse.Class.GridSpacingItemDecoration;
import com.example.bookverse.Class.ListOfBook;
import com.example.bookverse.Fragment.HomeFragment;
import com.example.bookverse.R;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllRecyclerView extends AppCompatActivity {

    ConstraintLayout layout;
    SharedPreferences sharedPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    TextView titleViewAll;
    RecyclerView recycleBook;
    ImageView viewAllbtnBack;

    //api
    ListOfBook resultApi;
    ArrayList<Book> listAllBook;
    HomeAdapterRecycle adapterAllBook;
    String key_default = "books";
    String currentkey;


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

        viewAllbtnBack = findViewById(R.id.viewAllbtnBack);
        titleViewAll = findViewById(R.id.view_all_title);
        recycleBook = findViewById(R.id.recycleBook);
        int numberOfColumns = 2; // Số cột bạn muốn hiển thị
        recycleBook.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        // Thêm khoảng cách cho item
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getApplicationContext().getSystemService(WindowManager.class).getDefaultDisplay().getMetrics(displayMetrics);
        // get with and chang pixel to dp;
        float width = (displayMetrics.widthPixels);
        float desity = getApplicationContext().getResources().getDisplayMetrics().density;
        int spacingInPixels = (int)Math.floor((width - 150 * 2 * desity) / 4);
        //Toast.makeText(getApplicationContext(), "width: " + spacingInPixels, Toast.LENGTH_SHORT).show();
        recycleBook.addItemDecoration(new GridSpacingItemDecoration(60));

        Intent getIntent = getIntent();
        String value = getIntent.getStringExtra("key");
        String getkey = getIntent.getStringExtra("keySearch");
        if(value != null){
            switch (value){
                case "allBook":{
                    titleViewAll.setText(R.string.ViewAllBookTitle);
                    break;
                }
            }
        }
        else if(getkey != null){
            titleViewAll.setText(getkey);
        }
        Toast.makeText(this, currentkey, Toast.LENGTH_SHORT).show();

        listAllBook = new ArrayList<>();
        adapterAllBook = new HomeAdapterRecycle(getApplicationContext(), listAllBook);

        getListBook(getkey, null);
        recycleBook.setAdapter(adapterAllBook);
        recycleBook.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int init = 600;
                int vt = recyclerView.computeVerticalScrollOffset();
                int newHeight = init - vt;
                newHeight = Math.max(newHeight, 10 );
                LinearLayout viewall_background = findViewById(R.id.viewall_background);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) viewall_background.getLayoutParams();
                layoutParams.height = newHeight;
                viewall_background.setLayoutParams(layoutParams);
                //Toast.makeText(getApplicationContext(), "Vt: " + Integer.toString(vt), Toast.LENGTH_SHORT).show();
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null && layoutManager.findLastCompletelyVisibleItemPosition() == listAllBook.size() - 1) {
                    // Người dùng đã cuộn đến cuối, tải thêm dữ liệu ở đây
                    if (resultApi.getNext()!= null){
                        getListBook(getkey, null);
                    }
                }
            }
        });

        viewAllbtnBack.setOnClickListener(viewBack ->{
            finish();
        });
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

    public void getListBook(String keySearch, String keyTopic){
        ApiService apiService = ApiClient.getApiService();
        Call<ListOfBook> listOfBookCall = apiService.getListBook(keySearch, keyTopic);
        listOfBookCall.enqueue(new Callback<ListOfBook>() {
            @Override
            public void onResponse(Call<ListOfBook> call, Response<ListOfBook> response) {
                //Toast.makeText(requireContext(), "Call Api success", Toast.LENGTH_SHORT).show();
                Log.d("API Response", response.body().toString());
                resultApi = response.body();
                ArrayList<Book> currentBook = resultApi.getResults();
                listAllBook.addAll(currentBook);
                adapterAllBook.notifyItemRangeChanged(listAllBook.size(), currentBook.size());
                if(resultApi.getNext() != null){
                    getListBook(keySearch, keyTopic);
                }
            }

            @Override
            public void onFailure(Call<ListOfBook> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Call Api failure", Toast.LENGTH_SHORT).show();
                if (t instanceof IOException){
                    Log.e("API","Network failure: " + t.getMessage());
                }
                else{
                    Log.e("API", "Conversion error: "+ t.getMessage());
                }
            }
        });
    }
}