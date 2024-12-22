package com.example.bookmyshow;

public class Movie2 {
    private String cast;
    private String description;
    private String name;
    private String posterUrl;
    private String rating;

    // Default constructor required for Firestore to map the data
    public Movie2() {
    }

    // Getter methods
    public String getCast() {
        return cast;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getRating() {
        return rating;
    }

    // Setter methods (if needed)
    public void setCast(String cast) {
        this.cast = cast;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}