package com.example.bookverse.viewmodels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bookverse.models.Book;
import com.example.bookverse.repository.BookRepository;
import com.example.bookverse.sharepreference.SharedPrefManage;

public class InfBookViewModel extends ViewModel {
    SharedPrefManage sharedPrefManage;
    BookRepository bookRepository;

    MutableLiveData<Boolean> isFavorite = new MutableLiveData<>(false);

    public InfBookViewModel(BookRepository bookRepository, SharedPrefManage sharedPrefManage) {
        this.bookRepository = bookRepository;
        this.sharedPrefManage = sharedPrefManage;
    }

    public void addRecentBook(Book book){
        bookRepository.addRecentBook(book);
    }

    public void addFavoriteBook(Book book, Boolean isActivated){
        String email = sharedPrefManage.getEmail();
        if(!isActivated){
            bookRepository.addFavoriteBook(book);
        } else {
            bookRepository.deleteFavoriteBook(book);
        }
    }

    public void checkFavoriteBook(Book book){
        bookRepository.isFavoriteBook(book, result -> {
            isFavorite.setValue(result);
        });
    }
}
