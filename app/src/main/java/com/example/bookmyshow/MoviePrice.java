package com.example.bookmyshow;

import java.util.Map;

public class MoviePrice {
    private String name;
    private Map<String, Integer> cinemas; // Map to hold cinema names and their prices

    public MoviePrice() {
        // Default constructor for Firestore
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Integer> getCinemas() {
        return cinemas;
    }

    public void setCinemas(Map<String, Integer> cinemas) {
        this.cinemas = cinemas;
    }
}

