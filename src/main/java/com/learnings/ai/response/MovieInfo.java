package com.learnings.ai.response;

public class MovieInfo {
    private String title;
    private int year;
    private String director;
    private String genre;
    private String oneLineSummary;

    public MovieInfo() {}

    public MovieInfo(String title) {
        this.title = title;
        this.director = "Trivikram";
        this.genre = "Family Film";
        this.year = 2025;
        this.oneLineSummary = "Mahesh's hit films";
    }

    public MovieInfo(String title, int year, String director, String genre, String oneLineSummary) {
        this.title = title;
        this.year = year;
        this.director = director;
        this.genre = genre;
        this.oneLineSummary = oneLineSummary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getOneLineSummary() {
        return oneLineSummary;
    }

    public void setOneLineSummary(String oneLineSummary) {
        this.oneLineSummary = oneLineSummary;
    }
}