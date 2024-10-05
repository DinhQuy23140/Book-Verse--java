package com.example.bookverse;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.bookverse.Fragment.HomeFragment;
import com.example.bookverse.Fragment.LibFragment;
import com.example.bookverse.Fragment.PersonFragment;
import com.example.bookverse.Fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
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