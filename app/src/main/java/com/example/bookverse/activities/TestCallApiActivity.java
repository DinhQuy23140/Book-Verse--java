package com.example.bookverse.activities;

import android.app.admin.DevicePolicyIdentifiers;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookverse.API.ApiService;
import com.example.bookverse.AdapterCustom.TestCallApiAdapter;
import com.example.bookverse.Class.User;
import com.example.bookverse.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestCallApiActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    List<User> listUser;
    TestCallApiAdapter Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_test_call_api);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        recyclerView = findViewById(R.id.callApiListUserRv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        listUser = new ArrayList<>();

        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        //callApiGetUsers();
    }

//    void callApiGetUsers(){
//        ApiService.service.getListUser(1).enqueue(new Callback<List<User>>() {
//            @Override
//            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
//                listUser = response.body();
//                Adapter = new TestCallApiAdapter(TestCallApiActivity.this, listUser);
//                recyclerView.setAdapter(Adapter);
//            }
//
//            @Override
//            public void onFailure(Call<List<User>> call, Throwable t) {
//                Toast.makeText(TestCallApiActivity.this, "Call Api failure", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
}