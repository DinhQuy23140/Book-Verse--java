package com.example.bookverse.API;

import com.example.bookverse.Class.ListOfBook;
import com.example.bookverse.Class.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
//    Gson gson = new GsonBuilder().setDateFormat("dd-MM-yyyy").create();
//    ApiService service = new Retrofit.Builder()
////                .baseUrl("https://jsonplaceholder.typicode.com/")
//                .baseUrl("https://jsonplaceholder.typicode.com/")
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .build()
//                .create(ApiService.class);
//
//    @GET("posts")
//    Call<List<User>> getListUser(@Query("userId") int userId);
    @GET("books?author_year_start=2023&author_year_end=2024")
    Call<ListOfBook> getListBook();
}
