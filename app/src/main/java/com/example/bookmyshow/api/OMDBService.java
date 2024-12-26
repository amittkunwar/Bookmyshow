package com.example.bookmyshow.api;

import com.example.bookmyshow.Models.MovieDetails;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;




public interface OMDBService {
    @GET("/")
    Call<MovieDetails> getMovieDetails(
            @Query("t")
            String movieName,
            @Query("apikey")
            String apiKey
    );
}

