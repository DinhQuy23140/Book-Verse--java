package com.example.bookverse.activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bookverse.API.ApiService;
import com.example.bookverse.AdapterCustom.HistoAdapter;
import com.example.bookverse.AdapterCustom.HomeAdapterRecycle;
import com.example.bookverse.AdapterCustom.KeyRelativeAdapter;
import com.example.bookverse.models.ApiClient;
import com.example.bookverse.models.Book;
import com.example.bookverse.models.GridSpacingItemDecoration;
import com.example.bookverse.models.ListOfBook;
import com.example.bookverse.R;
import com.example.bookverse.repository.BookRepository;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.PreferenceManager;
import com.example.bookverse.viewmodels.SearchViewModel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    LinearLayout layout;
    SharedPreferences sharedPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;
    PreferenceManager preferenceManager;
    ListOfBook resultSearch;
    ArrayList<Book> listBook = new ArrayList<>();
    ArrayList<Book> listRecentBook = new ArrayList<>();
    ArrayList<Book> listResult = new ArrayList<>();
    HistoAdapter adapterRecent;
    TextView btnCancel;
    EditText inputSearch;
    Drawable[] drawable;
    Drawable rightIcon;
    ImageView btnSearch;
    LinearLayout errorNotify, layoutSearch, layoutHistory;
    RecyclerView searchHistoryRv, search_searchRelativeRv, search_searchResult;
    KeyRelativeAdapter keyRelativeAdapter;
    HomeAdapterRecycle resultSearchAdapter;
    FirebaseFirestore firebaseFirestore;
    BookRepository bookRepository;
    SearchViewModel searchViewModel;

    ArrayList<String> listKey = new ArrayList<>(Arrays.asList("Kết quả phù hợp nhất", "Sách", "Thể loại", "Tác giả"));
    private static final int[] path = {R.drawable.background_search, R.drawable.background_purple200,
            R.drawable.background_search500, R.drawable.background_search_lavent, R.drawable.background_search_teal200, R.drawable.background_search_teal700,
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bookRepository = new BookRepository(getApplicationContext());
        searchViewModel = new SearchViewModel(bookRepository);
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

        firebaseFirestore = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());

        btnCancel = findViewById(R.id.search_cancelBtn);
        btnCancel.setOnClickListener(view->{
            finish();
        });
        btnSearch = findViewById(R.id.search_searchBtn);
        inputSearch = findViewById(R.id.search_inputSearch);

        search_searchRelativeRv = findViewById(R.id.search_searchRelativeRv);
        search_searchRelativeRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        Random randomBackGround = new Random();
        ArrayList<Integer> listPath = new ArrayList<>();
        for (int i = 0; i < listKey.size(); i++){
            int index = randomBackGround.nextInt(path.length);
            listPath.add(path[index]);
        }
        keyRelativeAdapter = new KeyRelativeAdapter(getApplicationContext(), listKey, listPath);
        search_searchRelativeRv.setAdapter(keyRelativeAdapter);

        searchHistoryRv = findViewById(R.id.searchHistoryRv);
        searchHistoryRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        getRecentBook();
        searchViewModel.getRecentBook();
        searchViewModel.getListRecent().observe(this, recentBook -> {
            listRecentBook = (ArrayList<Book>) recentBook;
            adapterRecent = new HistoAdapter(getApplicationContext(), listRecentBook);
            searchHistoryRv.setAdapter(adapterRecent);
        });

        layoutHistory = findViewById(R.id.search_historyLayout);
        layoutSearch = findViewById(R.id.search_searchLayout);

        //search event
        search_searchResult = findViewById(R.id.search_searchResult);
        search_searchResult.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        search_searchResult.addItemDecoration(new GridSpacingItemDecoration(10));
        errorNotify = findViewById(R.id.search_error);

        drawable = inputSearch.getCompoundDrawables();
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!inputSearch.getText().toString().isEmpty()){
                    inputSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_cancel, 0);
                    layoutSearch.setVisibility(View.VISIBLE);
                    layoutHistory.setVisibility(View.GONE);
                    searchBook(inputSearch.getText().toString());
                }
                else{
                    inputSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0);
                    layoutSearch.setVisibility(View.GONE);
                    if (resultSearchAdapter != null) {
                        resultSearchAdapter.clearData();
                    } else {
                        Log.e("Error", "resultSearchAdapter is null");
                    }
                    errorNotify.setVisibility(View.GONE);
                    layoutHistory.setVisibility(View.VISIBLE);
                }

                rightIcon = drawable[2];
                if(rightIcon != null){
                    inputSearch.setOnTouchListener((v, event)->{
                        int iconStart = inputSearch.getWidth() - inputSearch.getPaddingEnd() - rightIcon.getIntrinsicWidth();
                        if (event.getX() >= iconStart){
                            inputSearch.setText("");
                        }
                        return false;
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSearch.setOnClickListener(searchClick->{
        });
    }

    public void updateBackground(int pathTheme) {
        Glide.with(this)
                .load(pathTheme)  // Load trực tiếp từ pathTheme
                .placeholder(R.drawable.ic_default_image)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
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

    @SuppressLint("NotifyDataSetChanged")
    public void searchBook(String keySearch){
        if(listResult != null){
            listResult.clear();
        }
        searchViewModel.getBookByTitlte(inputSearch.getText().toString());
        searchViewModel.getListSearch().observe(SearchActivity.this, books -> {
            listResult = (ArrayList<Book>) books;
            resultSearchAdapter = new HomeAdapterRecycle(getApplicationContext(), listResult);
            search_searchResult.setAdapter(resultSearchAdapter);
        });
        Toast.makeText(this, Integer.toString(listResult.size()), Toast.LENGTH_SHORT).show();
    }
}