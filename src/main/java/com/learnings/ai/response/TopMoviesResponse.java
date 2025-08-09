package com.learnings.ai.response;

import java.util.List;

public class TopMoviesResponse {
    private List<MovieInfo> movies;

    public TopMoviesResponse() {}

    public TopMoviesResponse(List<MovieInfo> movies) {
        this.movies = movies;
    }

    public List<MovieInfo> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieInfo> movies) {
        this.movies = movies;
    }
}