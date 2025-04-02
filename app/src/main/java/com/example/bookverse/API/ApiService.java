package com.example.bookverse.API;

import com.example.bookverse.models.ListOfBook;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {
    @GET("books")
    Call<ListOfBook> getListBook(@Query("search") String search, @Query("topic") String topic);

    @GET
    Call<ListOfBook> getListBookByUrl(@Url String url);
}
