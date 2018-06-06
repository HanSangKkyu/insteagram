package com.example.ten.myapplication;

public class ReviewData {

    String id;
    double rating;
    String review;

    public ReviewData() {

    }

    public ReviewData(String id, double rating, String review) {
        this.id = id;
        this.rating = rating;
        this.review = review;
    }

    public double getRating() {
        return this.rating;
    }

    public String getReview() {
        return this.review;
    }

    public String getId() {
        return this.id;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public void setId(String id) {
        this.id = id;
    }
}
