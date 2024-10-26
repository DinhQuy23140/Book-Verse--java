package com.example.bookverse.activities;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookverse.R;

public class SearchActivity extends AppCompatActivity {
    TextView btnCancel;
    EditText inputSearch;
    Drawable[] drawable;
    Drawable rightIcon;
    ImageView btnSearch;
    LinearLayout errorNotify, layoutSearch, layoutHistory;
    RecyclerView searchHistoryRv, search_searchRelativeRv, search_searchResult;

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
        drawable = inputSearch.getCompoundDrawables();
        inputSearch.setOnTouchListener((v, event)->{
            if(event.getAction() == MotionEvent.ACTION_UP){
                rightIcon = drawable[2];
                if(rightIcon != null){
                    //lay vi tri cua rightIcon
                    int icontStart = inputSearch.getWidth() - inputSearch.getPaddingEnd() - rightIcon.getIntrinsicWidth();
                    if(event.getX() >= icontStart){
                        inputSearch.setText("" );
                        Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                else{
                    Toast.makeText(this, "RightIcon null", Toast.LENGTH_SHORT).show();
                }
                for (int i = 0; i < drawable.length; i++) {
                    Log.d("Drawable Check", "Drawable " + i + ": " + (drawable[i] != null ? "Exists" : "Null"));
                }
            }
            return false;
        });
    }
}