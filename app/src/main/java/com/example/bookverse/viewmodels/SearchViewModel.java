package com.example.bookverse.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bookverse.models.Book;
import com.example.bookverse.repository.BookRepository;

import java.util.List;

public class SearchViewModel extends ViewModel {
    BookRepository bookRepository;
    MutableLiveData<List<Book>> listSearch = new MutableLiveData<>();
    MutableLiveData<List<Book>> listRecent = new MutableLiveData<>();

    public SearchViewModel(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public MutableLiveData<List<Book>> getListSearch() {
        return listSearch;
    }

    public MutableLiveData<List<Book>> getListRecent() {
        return listRecent;
    }

    public void getBookByTitlte(String search) {
        bookRepository.getBookByTitle(search, result -> {
            listSearch.setValue(result);
        });
    }

    public void getRecentBook() {
        bookRepository.getRecentBook(result -> {
            listRecent.setValue(result);
        });
    }
}
