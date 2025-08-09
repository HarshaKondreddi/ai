package com.learnings.ai.models;

import com.learnings.ai.service.MovieBookingService;

import java.util.List;

public class Movie {
    private String movie;
    private List<Show> shows;

    public Movie(String movie, List<Show> shows) {
        this.movie = movie;
        this.shows = shows;
    }

    public String getMovie() { return movie; }
    public List<Show> getShows() { return shows; }
    public void setMovie(String movie) { this.movie = movie; }
    public void setShows(List<Show> shows) { this.shows = shows; }
}
