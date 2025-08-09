package com.learnings.ai.models;

public class Show {
    private String showId;
    private String movie;
    private String genre;
    private String city;
    private String date;
    private String time;
    private String screen;

    public Show() {}

    public Show(String showId, String movie, String genre, String city, String date, String time, String screen) {
        this.showId = showId;
        this.movie = movie;
        this.genre = genre;
        this.city = city;
        this.date = date;
        this.time = time;
        this.screen = screen;
    }

    public String getShowId() { return showId; }
    public String getMovie() { return movie; }
    public String getGenre() { return genre; }
    public String getCity() { return city; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getScreen() { return screen; }

    public void setShowId(String showId) { this.showId = showId; }
    public void setMovie(String movie) { this.movie = movie; }
    public void setGenre(String genre) { this.genre = genre; }
    public void setCity(String city) { this.city = city; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setScreen(String screen) { this.screen = screen; }
}
