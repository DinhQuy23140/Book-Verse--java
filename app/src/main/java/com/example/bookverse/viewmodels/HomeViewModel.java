package com.example.bookverse.viewmodels;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bookverse.R;
import com.example.bookverse.models.Book;
import com.example.bookverse.repository.BookRepository;

import java.util.Calendar;
import java.util.List;

public class HomeViewModel extends ViewModel {
    MutableLiveData<String> messageHome = new MutableLiveData<>("");
    MutableLiveData<List<Book>> listAllBook = new MutableLiveData<>();
    MutableLiveData<List<Book>> listRecentBook = new MutableLiveData<>();
    MutableLiveData<List<Book>> listViralBook = new MutableLiveData<>();
    BookRepository bookRepository;
    Context context;

    public HomeViewModel(Context context, BookRepository bookRepository) {
        this.context = context;
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

    public MutableLiveData<String> getMessageHome() {
        return messageHome;
    }

    public void loadMessageHome() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        //Toast.makeText(requireContext(), Integer.toString(hour), Toast.LENGTH_SHORT).show();
        if(hour >= 7 && hour <12) {
            messageHome.setValue(context.getString(R.string.setTimeMorining));
        }
        else if(hour >=12 && hour < 18) {
            messageHome.setValue(context.getString(R.string.setTimeAfternoon));
        }
        else if(hour>= 18 && hour < 21) {
            messageHome.setValue(context.getString(R.string.setTimeEvening));
        }
        else {
            messageHome.setValue(context.getString(R.string.setTimeNight));
        }
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

    public void getRecentBook() {
        bookRepository.getRecentBook(result -> {
            if (result != null && !result.isEmpty()) {
                listRecentBook.setValue(result);
            } else {
                listRecentBook.setValue(null);
            }
        });
    }
}
