package com.example.bookverse.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Book implements Serializable {
    private int id, download_count;
    private String title, media_type;
    private String[] subjects, bookshelves, languages, users;
    private List<String> summaries;
    private ArrayList<Person> authors, translators;
    private boolean copyright;
    private Map<String, String> formats;

    public Book(ArrayList<Person> authors, String[] bookshelves, boolean copyright, int download_count, Map<String, String> formats, int id, String[] languages, String media_type, String[] subjects, String title, ArrayList<Person> translators, String[] users) {
        this.authors = authors;
        this.bookshelves = bookshelves;
        this.copyright = copyright;
        this.download_count = download_count;
        this.formats = formats;
        this.id = id;
        this.languages = languages;
        this.media_type = media_type;
        this.subjects = subjects;
        this.title = title;
        this.translators = translators;
        this.users = users;
    }

    public Book(ArrayList<Person> authors, String[] bookshelves, boolean copyright, int download_count, Map<String, String> formats, int id, String[] languages, String media_type, String[] subjects, List<String> summaries, String title, ArrayList<Person> translators, String[] users) {
        this.authors = authors;
        this.bookshelves = bookshelves;
        this.copyright = copyright;
        this.download_count = download_count;
        this.formats = formats;
        this.id = id;
        this.languages = languages;
        this.media_type = media_type;
        this.subjects = subjects;
        this.summaries = summaries;
        this.title = title;
        this.translators = translators;
        this.users = users;
    }

    public List<String> getSummaries() {
        return summaries;
    }

    public void setSummaries(List<String> summaries) {
        this.summaries = summaries;
    }

    public ArrayList<Person> getAuthors() {
        return authors;
    }

    public void setAuthors(ArrayList<Person> authors) {
        this.authors = authors;
    }

    public String[] getBookshelves() {
        return bookshelves;
    }

    public void setBookshelves(String[] bookshelves) {
        this.bookshelves = bookshelves;
    }

    public boolean isCopyright() {
        return copyright;
    }

    public void setCopyright(boolean copyright) {
        this.copyright = copyright;
    }

    public int getDownload_count() {
        return download_count;
    }

    public void setDownload_count(int download_count) {
        this.download_count = download_count;
    }

    public Map<String, String> getFormats() {
        return formats;
    }

    public void setFormats(Map<String, String> formats) {
        this.formats = formats;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String[] getLanguages() {
        return languages;
    }

    public void setLanguages(String[] languages) {
        this.languages = languages;
    }

    public String getMedia_type() {
        return media_type;
    }

    public void setMedia_type(String media_type) {
        this.media_type = media_type;
    }

    public String[] getSubjects() {
        return subjects;
    }

    public void setSubjects(String[] subjects) {
        this.subjects = subjects;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Person> getTranslators() {
        return translators;
    }

    public void setTranslators(ArrayList<Person> translators) {
        this.translators = translators;
    }

    public String[] getUsers() {
        return users;
    }

    public void setUsers(String[] users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Formats: " + (formats != null ? formats.toString() : "Not Available");
    }
}
