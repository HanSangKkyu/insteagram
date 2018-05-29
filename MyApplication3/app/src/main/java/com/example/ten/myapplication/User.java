package com.example.ten.myapplication;

public class User {
    String id;
    String pw;

    public User() {

    }

    public User(String id, String pw) {
        this.id = id;
        this.pw = pw;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPw() {
        return this.pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }
}
