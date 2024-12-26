package com.example.bookmyshow.Models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Movie {

    private String name;
    private String description;
    private String rating;
    private String cast;
    private String posterUrl;

    // Default constructor required for Firestore serialization
    public Movie() {
    }

    // Constructor with all fields
    public Movie(String name, String description, String rating, String cast, String posterUrl) {
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.cast = cast;
        this.posterUrl = posterUrl;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCast() {
        return cast;
    }

    public void setCast(String cast) {
        this.cast = cast;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", rating='" + rating + '\'' +
                ", cast='" + cast + '\'' +
                ", posterUrl='" + posterUrl + '\'' +
                '}';
    }
}
