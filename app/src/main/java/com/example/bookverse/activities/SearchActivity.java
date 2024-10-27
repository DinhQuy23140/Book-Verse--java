package com.example.bookverse.activities;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookverse.API.ApiService;
import com.example.bookverse.AdapterCustom.HomeAdapterRecycle;
import com.example.bookverse.AdapterCustom.KeyRelativeAdapter;
import com.example.bookverse.AdapterCustom.ViewAllAdapter;
import com.example.bookverse.Class.ApiClient;
import com.example.bookverse.Class.Book;
import com.example.bookverse.Class.GridSpacingItemDecoration;
import com.example.bookverse.Class.ListOfBook;
import com.example.bookverse.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    ListOfBook resultSearch;
    ArrayList<Book> listBook = new ArrayList<>();
    TextView btnCancel;
    EditText inputSearch;
    Drawable[] drawable;
    Drawable rightIcon;
    ImageView btnSearch;
    LinearLayout errorNotify, layoutSearch, layoutHistory;
    RecyclerView searchHistoryRv, search_searchRelativeRv, search_searchResult;
    KeyRelativeAdapter keyRelativeAdapter;
    HomeAdapterRecycle resultSearchAdapter;

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
        layoutHistory = findViewById(R.id.search_historyLayout);
        layoutSearch = findViewById(R.id.search_searchLayout);
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
                    for (int i = 0; i < drawable.length; i++) {
                        Log.d("Drawable Check", "Drawable " + i + ": " + (drawable[i] != null ? "Exists" : "Null"));
                    }
                }
                else{
                    inputSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0);
                    layoutSearch.setVisibility(View.GONE);
                    resultSearchAdapter.clearData();
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

        //search event
        search_searchResult = findViewById(R.id.search_searchResult);
        search_searchResult.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
        search_searchResult.addItemDecoration(new GridSpacingItemDecoration(60));
        errorNotify = findViewById(R.id.search_error);
        btnSearch.setOnClickListener(searchClick->{
            String textSearch = inputSearch.getText().toString();
            if(!textSearch.isEmpty()) {
                getListBook(textSearch);
                Toast.makeText(getApplicationContext(), "Search", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Input is required", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), "List null", Toast.LENGTH_SHORT).show();
                }
                else{
                    listBook = resultSearch.getResults();
                    resultSearchAdapter = new HomeAdapterRecycle(getApplicationContext(), listBook);
                    Toast.makeText(getApplicationContext(), Integer.toString(resultSearch.getResults().size()), Toast.LENGTH_SHORT).show();
                    search_searchResult.setAdapter(resultSearchAdapter);
                    errorNotify.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<ListOfBook> call, Throwable t) {

            }
        });
    }
}