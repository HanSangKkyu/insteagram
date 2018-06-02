package com.example.ten.myapplication;

import java.util.ArrayList;

public class Data {
    String display_url;
    String shortcode;
    ArrayList<String> hashtag;

    public Data(String display_url, String shortcode) {
        this.display_url = display_url;
        this.shortcode = shortcode;
    }

    public String getDisplay_url() {
        return display_url;
    }

    public void setDisplay_url(String display_url) {
        this.display_url = display_url;
    }

    public String getShortcode() {
        return shortcode;
    }

    public void setShortcode(String shortcode) {
        this.shortcode = shortcode;
    }

    public ArrayList<String> getHashtag() {
        return hashtag;
    }

    public void setHashtag(ArrayList<String> hashtag) {
        this.hashtag = hashtag;
    }
}
