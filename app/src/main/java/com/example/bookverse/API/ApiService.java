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
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("books")
    Call<ListOfBook> getListBook(@Query("search") String search, @Query("topic") String topic);
}
