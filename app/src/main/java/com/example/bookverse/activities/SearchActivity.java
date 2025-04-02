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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.bookverse.AdapterCustom.HistoAdapter;
import com.example.bookverse.AdapterCustom.HomeAdapterRecycle;
import com.example.bookverse.AdapterCustom.KeyRelativeAdapter;
import com.example.bookverse.models.ApiClient;
import com.example.bookverse.models.Book;
import com.example.bookverse.models.GridSpacingItemDecoration;
import com.example.bookverse.models.ListOfBook;
import com.example.bookverse.R;
import com.example.bookverse.utilities.Constants;
import com.example.bookverse.utilities.PreferenceManager;
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
        //String email = preferenceManager.getString(Constants.KEY_EMAIL);
        //Toast.makeText(getApplicationContext(), "Email: " + email, Toast.LENGTH_SHORT).show();


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
        getRecentBook();
        adapterRecent = new HistoAdapter(getApplicationContext(), listRecentBook);
        searchHistoryRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        searchHistoryRv.setAdapter(adapterRecent);

        layoutHistory = findViewById(R.id.search_historyLayout);
        layoutSearch = findViewById(R.id.search_searchLayout);

        //search event
        search_searchResult = findViewById(R.id.search_searchResult);
        search_searchResult.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        search_searchResult.addItemDecoration(new GridSpacingItemDecoration(60));
        resultSearchAdapter = new HomeAdapterRecycle(getApplicationContext(), listResult);
        search_searchResult.setAdapter(resultSearchAdapter);
        errorNotify = findViewById(R.id.search_error);

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
                    drawable = inputSearch.getCompoundDrawables();
//                    for (int i = 0; i < drawable.length; i++) {
//                        Log.d("Drawable Check", "Drawable " + i + ": " + (drawable[i] != null ? "Exists" : "Null"));
//                    }
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
//            String textSearch = inputSearch.getText().toString();
//            if(!textSearch.isEmpty()) {
//                getListBook(textSearch);
//                Toast.makeText(getApplicationContext(), "Search", Toast.LENGTH_SHORT).show();
//            }
//            else{
//                Toast.makeText(getApplicationContext(), "Input is required", Toast.LENGTH_SHORT).show();
//            }
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

    public void getRecentBook(){
        firebaseFirestore.collection(Constants.KEY_COLLECTION_RECENTREAD)
                .document(preferenceManager.getString(Constants.KEY_EMAIL))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
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
                            int sizeList = stringBookId.size();
                            stringBookId = stringBookId.subList(0, Math.min(sizeList, 6));

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
                                                    adapterRecent.notifyItemInserted(listRecentBook.size() - 1); // Cập nhật item mới
                                                }
                                            }
                                            // Thông báo về số lượng sách đã được tải
                                            //Toast.makeText(getApplicationContext(), "Size: " + listRecentBook.size(), Toast.LENGTH_SHORT).show();
                                        } else {
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

    public void getListBook(String keySearch){
        ApiService apiService = ApiClient.getApiService();
        Call<ListOfBook> listOfBookCall = apiService.getListBook(keySearch, null);
        listOfBookCall.enqueue(new Callback<ListOfBook>() {
            @Override
            public void onResponse(Call<ListOfBook> call, Response<ListOfBook> response) {
                resultSearch = response.body();
                if(resultSearch.getResults().isEmpty()){
                    errorNotify.setVisibility(View.VISIBLE);
                    //Toast.makeText(getApplicationContext(), "List null", Toast.LENGTH_SHORT).show();
                }
                else{
                    listBook = resultSearch.getResults();
                    resultSearchAdapter = new HomeAdapterRecycle(getApplicationContext(), listBook);
                    //Toast.makeText(getApplicationContext(), Integer.toString(resultSearch.getResults().size()), Toast.LENGTH_SHORT).show();
                    search_searchResult.setAdapter(resultSearchAdapter);
                    errorNotify.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ListOfBook> call, Throwable t) {

            }
        });
    }

    public void searchBook(String keySearch){
        if(listResult != null){
            listResult.clear();
        }
        firebaseFirestore.collection(Constants.KEY_COLLECTION_BOOKS)
                .get()
                .addOnCompleteListener(taskSearch -> {
                   if(taskSearch.isSuccessful() && !taskSearch.getResult().isEmpty()) {
                       for (DocumentSnapshot documentSnapshot : taskSearch.getResult()) {
                           Map<String, Object> data = documentSnapshot.getData();
                           String title = (String) data.get("title");
                           if(title != null && title.toLowerCase().contains(keySearch.toLowerCase())){
                               Gson gson = new Gson();
                               Book book = gson.fromJson(gson.toJson(data), Book.class);
                               listResult.add(book);
                               resultSearchAdapter.notifyDataSetChanged();
                           }
                       }
                       //Toast.makeText(getApplicationContext(), "Size: " + listResult.size(), Toast.LENGTH_SHORT).show();
                   }
                });
    }
}