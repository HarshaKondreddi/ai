package com.learnings.ai.request;

public class ReviewRequest {
    private String movieName;

    public ReviewRequest() {}

    public ReviewRequest(String movieName) {
        this.movieName = movieName;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }
}