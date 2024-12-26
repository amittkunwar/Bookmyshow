package com.example.bookmyshow.api;

import com.example.bookmyshow.Models.MovieResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TMDBService {
    @GET("movie/popular")
    Call<MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page
    );
}
