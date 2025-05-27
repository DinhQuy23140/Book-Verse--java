package com.example.bookverse.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ListView;

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
import com.example.bookverse.AdapterCustom.ThemeAdapter;
import com.example.bookverse.models.Image;
import com.example.bookverse.R;

import java.util.ArrayList;

public class ViewThemeActivity extends AppCompatActivity {

    FrameLayout layout;
    ListView lvTheme;
    ArrayList<Image> listTheme;
    ThemeAdapter adapter;
    SharedPreferences preferences;
    ConstraintLayout ctrBackTheme;
    SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_theme);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layout = findViewById(R.id.main);
        preferences = getApplicationContext().getSharedPreferences("MySharePref", Context.MODE_PRIVATE);
        int presentPath = preferences.getInt("pathTheme", 0);

        preferenceChangeListener = (sharedPreferences, key)->{
            if(key.equals("pathTheme")){
                int newpathTheme = sharedPreferences.getInt("pathTheme", R.drawable.background_app);
                updateBackground(newpathTheme);
            }
        };
        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);

        SharedPreferences.Editor editor = preferences.edit();
        int pathTheme = preferences.getInt("pathTheme", R.drawable.background_app);
        updateBackground(pathTheme);

        lvTheme = findViewById(R.id.Setting_lvTheme);
        int[] listPathImage = {R.drawable.background_app, R.drawable.background_login, R.drawable.favorite, R.drawable.theme_alfomedeiros18926843,
                R.drawable.theme_exel, R.drawable.theme_mati12509859, R.drawable.theme_mdsnmdsnmdsn1831234,
                R.drawable.theme_padrinan19670, R.drawable.theme_pixabay459277};
        listTheme = new ArrayList<Image>();
        for (int i : listPathImage) {
            if (i != presentPath){
                Image image = new Image(i);
                listTheme.add(image);
            }
            else{
                continue;
            }
        }
        adapter = new ThemeAdapter(getApplicationContext(), listTheme);
        lvTheme.setAdapter(adapter);
        lvTheme.setClickable(true);

        ctrBackTheme = findViewById(R.id.cstr_back_theme);
        ctrBackTheme.setOnClickListener(back -> finish());
    }

    public void updateBackground(int pathTheme) {
        Glide.with(this)
                .load(pathTheme)  // Load trực tiếp từ pathTheme
                .placeholder(R.drawable.ic_default_image)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        // Đảm bảo layout đã được đo xong
                        layout.post(() -> {
                            int layoutWidth = layout.getWidth();
                            int layoutHeight = layout.getHeight();

                            if (layoutWidth > 0 && layoutHeight > 0) { // Kiểm tra kích thước hợp lệ
                                Bitmap bitmap = drawableToBitmap(resource);

                                if (bitmap != null) { // Kiểm tra bitmap hợp lệ
                                    int imageWidth = bitmap.getWidth();
                                    int imageHeight = bitmap.getHeight();

                                    float scaleX = (float) layoutWidth / imageWidth;
                                    float scaleY = (float) layoutHeight / imageHeight;
                                    float scale = Math.min(scaleX, scaleY);

                                    int newWidth = Math.round(imageWidth * scale);
                                    int newHeight = Math.round(imageHeight * scale);

                                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                                    BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), scaledBitmap);
                                    layout.setBackground(bitmapDrawable);  // Đặt bitmap đã scale làm background
                                } else {
                                    layout.setBackgroundResource(R.drawable.ic_error_load_image); // Backup nếu bitmap null
                                }
                            } else {
                                layout.setBackgroundResource(R.drawable.ic_error_load_image); // Backup nếu kích thước không hợp lệ
                            }
                        });
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Backup khi Glide không còn giữ tài nguyên
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