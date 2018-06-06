package com.example.ten.myapplication;

import java.io.Serializable;

public class User implements Serializable {
    String id;
    String pw;
    String preferences;

    public User() {

    }
//    public User(String id, String pw){
//        this.id = id;
//        this.pw = pw;
//    }
    public User(String id, String pw, String preferences) {
        this.id = id;
        this.pw = pw;
        this.preferences = preferences;
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

    public String getPreferences() {
        return this.preferences;
    }

    public void setPreferences(String preferences) {
        this.preferences = preferences;
    }
}
