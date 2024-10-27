package com.example.bookverse.activities;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookverse.AdapterCustom.KeyRelativeAdapter;
import com.example.bookverse.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class SearchActivity extends AppCompatActivity {
    TextView btnCancel;
    EditText inputSearch;
    Drawable[] drawable;
    Drawable rightIcon;
    ImageView btnSearch;
    LinearLayout errorNotify, layoutSearch, layoutHistory;
    RecyclerView searchHistoryRv, search_searchRelativeRv, search_searchResult;

    ArrayList<String> listKey = new ArrayList<>(Arrays.asList("Kết quả phù hợp nhất", "Có liên quan", "Sách", "Thể loại", "Tác giả"));
    private static final int[] path = {R.drawable.background_search, R.drawable.background_purple200,
            R.drawable.background_search500, R.drawable.background_search_lavent, R.drawable.background_search_teal200, R.drawable.background_search_teal700,
    };
    KeyRelativeAdapter keyRelativeAdapter;
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
//        inputSearch.setOnTouchListener((v, event)->{
//            if(event.getAction() == MotionEvent.ACTION_UP){
//                rightIcon = drawable[2];
//                if(rightIcon != null){
//                    //lay vi tri cua rightIcon
//                    int icontStart = inputSearch.getWidth() - inputSearch.getPaddingEnd() - rightIcon.getIntrinsicWidth();
//                    if(event.getX() >= icontStart){
//                        inputSearch.setText("" );
//                        Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
//                        return true;
//                    }
//                }
//                else{
//                    Toast.makeText(this, "RightIcon null", Toast.LENGTH_SHORT).show();
//                }
//                for (int i = 0; i < drawable.length; i++) {
//                    Log.d("Drawable Check", "Drawable " + i + ": " + (drawable[i] != null ? "Exists" : "Null"));
//                }
//            }
//            return false;
//        });
    }
}