package com.example.bookverse.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;
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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bookverse.AdapterCustom.BookAdapter;
import com.example.bookverse.AdapterCustom.HomeAdapterRecycle;
import com.example.bookverse.models.Book;
import com.example.bookverse.models.ListOfBook;
import com.example.bookverse.R;
import com.example.bookverse.repository.BookRepository;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.GridSpaceDecoration;
import com.example.bookverse.utilities.PreferenceManager;
import com.example.bookverse.viewmodels.ViewResultViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;

public class ViewResultActivity extends AppCompatActivity {

    BookRepository bookRepository;
    ViewResultViewModel viewResultViewModel;
    ConstraintLayout layout;
    SharedPreferences sharedPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    TextView titleViewAll;
    RecyclerView recycleBook;
    ImageView viewAllbtnBack;
    FirebaseFirestore firebaseFirestore;
    PreferenceManager preferenceManager;
    String emailUser;

    //api
    ListOfBook resultApi;
    ArrayList<Book> listAllBook;
    HomeAdapterRecycle adapterAllBook;
    BookAdapter bookAdapter;
    String value, getkey;


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

        bookRepository = new BookRepository(this);
        viewResultViewModel = new ViewResultViewModel(bookRepository);
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
        int numberOfColumns; // Số cột bạn muốn hiển thị
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = (int) (metrics.widthPixels/metrics.density);
        if (width > 600) numberOfColumns = 4;
        else numberOfColumns = 2;
        recycleBook.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recycleBook.addItemDecoration(new GridSpaceDecoration(numberOfColumns, dpToPx(this, 5)));

        firebaseFirestore = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        emailUser = preferenceManager.getString(Constants.KEY_EMAIL);
        listAllBook = new ArrayList<>();
        adapterAllBook = new HomeAdapterRecycle(getApplicationContext(), listAllBook);
        Intent getIntent = getIntent();
        value = getIntent.getStringExtra("keyView");
        getkey = getIntent.getStringExtra("keySearch");
        if(value != null){
            switch (value){
                case "ViewAllBook": {
                    titleViewAll.setText(R.string.ViewAllBookTitle);
                    getAllBook();
                    break;
                }
                case "ViewViral": {
                    titleViewAll.setText(R.string.txtViral);
                    getViralBook();
                    break;
                }
                case "ViewRecent": {
                    titleViewAll.setText(R.string.txtRecent);
                    getRecentBook();
                    break;
                }
                case "ViewFavorite": {
                    titleViewAll.setText(R.string.favoriteBook);
                    getFavoriteBook();
                    break;
                }
                default: {
                    titleViewAll.setText(value);
                    getBookByTopic(value);
                    break;
                }
            }
        }
//        recycleBook.setAdapter(adapterAllBook);
        recycleBook.setAdapter(bookAdapter);
        Toast.makeText(this, Integer.toString(listAllBook.size()), Toast.LENGTH_SHORT).show();
        recycleBook.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
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

    public void getAllBook(){
        viewResultViewModel.getAllBook();
        viewResultViewModel.getListAllBook().observe(this, allBook -> {
            listAllBook = (ArrayList<Book>) allBook;
            Toast.makeText(this, "Size: " + listAllBook.size(),  Toast.LENGTH_SHORT).show();
            bookAdapter = new BookAdapter(this, listAllBook);
            recycleBook.setAdapter(bookAdapter);
        });
    }

    public void getViralBook(){
        viewResultViewModel.getViralBook();
        viewResultViewModel.getListViralBook().observe(this, viralBook -> {
            listAllBook = (ArrayList<Book>) viralBook;
            Toast.makeText(this, "Size: " + listAllBook.size(),  Toast.LENGTH_SHORT).show();
            bookAdapter = new BookAdapter(this, listAllBook);
            recycleBook.setAdapter(bookAdapter);
        });
    }

    public void getRecentBook() {
        viewResultViewModel.getRecentBook();
        viewResultViewModel.getListRecentBook().observe(this, recentBook -> {
            listAllBook = (ArrayList<Book>) recentBook;
            Toast.makeText(this, "Size: " + listAllBook.size(),  Toast.LENGTH_SHORT).show();
            bookAdapter = new BookAdapter(this, listAllBook);
            recycleBook.setAdapter(bookAdapter);
        });
    }

    public void getFavoriteBook() {
        viewResultViewModel.getFavoriteBook();
        viewResultViewModel.getListFavoriteBook().observe(this, favoriteBook -> {
            listAllBook = (ArrayList<Book>) favoriteBook;
            Toast.makeText(this, "Size: " + listAllBook.size(),  Toast.LENGTH_SHORT).show();
            bookAdapter = new BookAdapter(this, listAllBook);
            recycleBook.setAdapter(bookAdapter);
        });
    }

    public void getBookByTopic(String keyTopic){

        viewResultViewModel.getBookByTopic(keyTopic);
        viewResultViewModel.getTopicBooks().observe(this, bookByTopic -> {
            listAllBook = (ArrayList<Book>) bookByTopic;
            Toast.makeText(this, "Size: " + listAllBook.size(), Toast.LENGTH_SHORT).show();
            bookAdapter = new BookAdapter(this, listAllBook);
            recycleBook.setAdapter(bookAdapter);
        });
    }

    public int dpToPx(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
}