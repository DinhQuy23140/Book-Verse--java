package com.example.bookverse;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.bookverse.Fragment.HomeFragment;
import com.example.bookverse.Fragment.LibFragment;
import com.example.bookverse.Fragment.PersonFragment;
import com.example.bookverse.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    DrawerLayout layout;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutmain), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        layout = findViewById(R.id.layoutmain);
        sharedPreferences = getSharedPreferences("MySharePref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int pathTheme = sharedPreferences.getInt("pathThem", 0);
        Glide.with(this)
                .load(R.drawable.background_app)
                .placeholder(R.drawable.ic_default_image)
                .into(new CustomTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        if (pathTheme != 0){
                            layout.setBackgroundResource(pathTheme);
                        }
                        else{
                            layout.setBackgroundResource(R.drawable.background_app);
                            editor.putInt("pathTheme", R.drawable.background_app);
                            editor.apply();
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        layout.setBackgroundResource(R.drawable.ic_error_load_image);
                    }
                });

        FragmentTransaction fragmentTransaction;
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.bottom_home);
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new HomeFragment()).commit();
        fragmentTransaction.addToBackStack(null);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.bottom_home){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            else if(item.getItemId() == R.id.bottom_search){
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                FragmentTransaction replaceFragment = getSupportFragmentManager().beginTransaction();
                replaceFragment.replace(R.id.fragment_container, new SearchFragment());
                replaceFragment.addToBackStack(null); // Optional
                replaceFragment.commit();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            else if(item.getItemId() == R.id.bottom_Lib){
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new LibFragment()).commit();
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                FragmentTransaction replaceFragment = getSupportFragmentManager().beginTransaction();
                replaceFragment.replace(R.id.fragment_container, new LibFragment());
                replaceFragment.addToBackStack(null); // Optional
                replaceFragment.commit();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            }
            else if(item.getItemId() == R.id.bottom_person){
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PersonFragment()).commit();
//                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
}