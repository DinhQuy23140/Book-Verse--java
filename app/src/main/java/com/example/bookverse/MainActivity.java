package com.example.bookverse;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bookverse.Fragment.HomeFragment;
import com.example.bookverse.Fragment.LibFragment;
import com.example.bookverse.Fragment.PersonFragment;
import com.example.bookverse.Fragment.SearchFragment;
import com.example.bookverse.utilities.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    DrawerLayout layout;
    FrameLayout frameLayout;
    SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutmain), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        View layoutV = findViewById(R.id.fragment_container);
        ViewCompat.setOnApplyWindowInsetsListener(layoutV, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());

            // Dùng padding để tránh bị che
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ViewCompat.setFitsSystemWindows(layoutV, true);


        frameLayout = findViewById(R.id.fragment_container);
        layout = findViewById(R.id.layoutmain);
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
        if(pathTheme == R.drawable.background_app){
            editor.putInt("pathTheme", R.drawable.background_app);
            editor.apply();
        }
        updateBackground(pathTheme);

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        if (savedInstanceState != null) {
            int selectedId = savedInstanceState.getInt(Constants.KEY_STATE);
            bottomNavigationView.setSelectedItemId(selectedId);
        } else {
            FragmentTransaction fragmentTransaction;
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new HomeFragment()).commit();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.bottom_home){
                FragmentTransaction transaction;
                transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new HomeFragment()).commit();
                transaction.addToBackStack(null);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            else if(item.getItemId() == R.id.bottom_search){
                FragmentTransaction replaceFragment = getSupportFragmentManager().beginTransaction();
                replaceFragment.replace(R.id.fragment_container, new SearchFragment());
                replaceFragment.addToBackStack(null); // Optional
                replaceFragment.commit();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            else if(item.getItemId() == R.id.bottom_Lib){
                FragmentTransaction replaceFragment = getSupportFragmentManager().beginTransaction();
                replaceFragment.replace(R.id.fragment_container, new LibFragment());
                replaceFragment.addToBackStack(null); // Optional
                replaceFragment.commit();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            else if(item.getItemId() == R.id.bottom_person){
                FragmentTransaction replaceFragment = getSupportFragmentManager().beginTransaction();
                replaceFragment.replace(R.id.fragment_container, new PersonFragment());
                replaceFragment.addToBackStack(null); // Optional
                replaceFragment.commit();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            return false;
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Đảm bảo hủy đăng ký listener để tránh rò rỉ bộ nhớ
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
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

                        // Đảm bảo layout đã được vẽ (kích thước > 0)
                        if (layoutWidth <= 0 || layoutHeight <= 0) {
                            // Layout chưa được vẽ, có thể xử lý lại sau
                            layout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                @Override
                                public boolean onPreDraw() {
                                    layout.getViewTreeObserver().removeOnPreDrawListener(this);
                                    updateBackground(pathTheme); // Gọi lại phương thức khi layout đã được vẽ
                                    return true;
                                }
                            });
                            return;
                        }

                        Bitmap bitmap = drawableToBitmap(resource);

                        int imageWidth = bitmap.getWidth();
                        int imageHeight = bitmap.getHeight();

                        float scaleX = (float)layoutWidth / imageWidth;
                        float scaleY = (float)layoutHeight / imageHeight;
                        float scale = Math.min(scaleX, scaleY);

                        int newWidth = Math.round(imageWidth * scale);
                        int newHeight = Math.round(imageHeight * scale);

                        Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), scaleBitmap);

                        // Set background chỉ một lần
                        layout.setBackground(bitmapDrawable);
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

    public void hideBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setVisibility(                                                                                                              View.GONE);
    }

    public void showBottomNavigationView() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(Constants.KEY_STATE, bottomNavigationView.getSelectedItemId());
    }
}