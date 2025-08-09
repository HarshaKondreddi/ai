package com.learnings.ai.request;

public class TopMoviesRequest {
    private String language;

    public TopMoviesRequest() {}

    public TopMoviesRequest(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}