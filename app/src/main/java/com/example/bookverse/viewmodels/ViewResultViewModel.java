package com.example.bookverse.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bookverse.models.Book;
import com.example.bookverse.repository.BookRepository;

import java.util.List;

public class ViewResultViewModel extends ViewModel {
    MutableLiveData<List<Book>> listAllBook = new MutableLiveData<>();
    MutableLiveData<List<Book>> listRecentBook = new MutableLiveData<>();
    MutableLiveData<List<Book>> listViralBook = new MutableLiveData<>();
    MutableLiveData<List<Book>> listFavoriteBook = new MutableLiveData<>();
    MutableLiveData<List<Book>> topicBooks = new MutableLiveData<>();
    BookRepository bookRepository;

    public ViewResultViewModel(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public MutableLiveData<List<Book>> getListAllBook() {
        return listAllBook;
    }

    public MutableLiveData<List<Book>> getListRecentBook() {
        return listRecentBook;
    }

    public MutableLiveData<List<Book>> getListViralBook() {
        return listViralBook;
    }

    public MutableLiveData<List<Book>> getListFavoriteBook() {
        return listFavoriteBook;
    }

    public MutableLiveData<List<Book>> getTopicBooks() {
        return topicBooks;
    }

    public void getAllBook() {
        bookRepository.getAllBook(result -> {
            if (result != null && !result.isEmpty()) {
                listAllBook.setValue(result);
            } else {
                listAllBook.setValue(null);
            }
        });
    }

    public void getViralBook(){
        bookRepository.getViralBook(result -> {
            if (result != null && !result.isEmpty()) {
                listViralBook.setValue(result);
            } else {
                listViralBook.setValue(null);
            }
        });
    }

    public void getBookByTopic(String keyTopic) {
        bookRepository.getBookByTopic(keyTopic, result -> {
            if (result != null && !result.isEmpty()) {
                topicBooks.setValue(result);
            } else {
                topicBooks.setValue(null);
            }
        });
    }

    public void getRecentBook() {
        bookRepository.getRecentBook(result -> {
            if (result != null && !result.isEmpty()) {
                listRecentBook.setValue(result);
            } else {
                listRecentBook.setValue(null);
            }
        });
    }

    public void getFavoriteBook() {
        bookRepository.getFavoriteBook(result -> {
            if (result != null && !result.isEmpty()) {
                listFavoriteBook.setValue(result);
            } else {
                listFavoriteBook.setValue(null);
            }
        });
    }
}
