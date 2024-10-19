package com.example.bookverse.Class;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class Book{
    private String title, author, publisher, genre, description, pathBook;
    private int isbn, year, page, pathImage;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getIsbn() {
        return isbn;
    }

    public void setIsbn(int isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getPathBook() {
        return pathBook;
    }

    public void setPathBook(String pathBook) {
        this.pathBook = pathBook;
    }

    public int getPathImage() {
        return pathImage;
    }

    public void setPathImage(int pathImage) {
        this.pathImage = pathImage;
    }


//    public Book(String author, int pathImage) {
//        super();
//        this.author = author;
//        this.pathImage = pathImage;
//    }


    public Book(String author, int pathImage) {
        this.author = author;
        this.pathImage = pathImage;
    }

    public Book(String author, String description, String genre, int isbn, int page, String pathBook, int pathImage, String publisher, String title, int year) {
        this.author = author;
        this.description = description;
        this.genre = genre;
        this.isbn = isbn;
        this.page = page;
        this.pathBook = pathBook;
        this.pathImage = pathImage;
        this.publisher = publisher;
        this.title = title;
        this.year = year;
    }
}
