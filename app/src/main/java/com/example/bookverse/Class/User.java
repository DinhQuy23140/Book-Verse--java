package com.example.bookverse.Class;

public class User {
    String userId;
    String id;
    String title;
    String body;

    public User(String body, String id, String title, String userId) {
        this.body = body;
        this.id = id;
        this.title = title;
        this.userId = userId;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
