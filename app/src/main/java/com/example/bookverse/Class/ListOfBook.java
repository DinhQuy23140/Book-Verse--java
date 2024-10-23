package com.example.bookverse.Class;

import java.util.ArrayList;

public class ListOfBook {
    private int count;
    private String next, previous;
    private ArrayList<Book> results;

    public ListOfBook(int count, String next, String previous, ArrayList<Book> results) {
        this.count = count;
        this.next = next;
        this.previous = previous;
        this.results = results;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public ArrayList<Book> getResults() {
        return results;
    }

    public void setResults(ArrayList<Book> results) {
        this.results = results;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (results != null) {
            for (Book book : results) {
                sb.append(book.toString()).append("\n");
            }
        } else {
            sb.append("No results");
        }
        return sb.toString();
    }
}
