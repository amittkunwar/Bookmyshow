package com.example.bookmyshow;

import java.util.HashMap;
import java.util.Map;

public class Movie {
    private String name;
    private String description;
    private String rating;
    private String cast;
    private String posterUrl;

    // Default constructor for Firestore
    public Movie() {}

    // Constructor for creating Movie objects
    public Movie(String name, String description, String rating, String cast, String posterUrl) {
        this.name = name;
        this.description = description;
        this.rating = rating;
        this.cast = cast; // Initialize cast
        this.posterUrl = posterUrl;
    }

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getCast() { return cast; } // Add getter for cast
    public void setCast(String cast) { this.cast = cast; } // Add setter for cast

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    // Convert object to a map for custom data structure
    public Map<String, Object> toMap() {
        Map<String, Object> movieMap = new HashMap<>();
        movieMap.put("name", name);
        movieMap.put("description", description);
        movieMap.put("rating", rating);
        movieMap.put("cast", cast); // Add cast to the map
        movieMap.put("posterUrl", posterUrl);
        return movieMap;
    }
}
