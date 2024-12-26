package com.example.bookmyshow;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookmyshow.Models.Movie;
import com.example.bookmyshow.Models.MovieDetails;
import com.example.bookmyshow.api.OMDBService;
import com.example.bookmyshow.api.RetrofitInstance;
import com.google.firebase.firestore.FirebaseFirestore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AddMovieActivity extends AppCompatActivity {

    private EditText movieNameEditText;
    private Button addMovieButton;
    private Button fetchMovieDetails;
    private String posterUrl = "";
    private String rating, description, cast;
    private Movie movie;
    private TextView MovieRating , MoviesCast , MoviesDescription ;
    private ImageView MoviePoster ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        // Initialize Views
        movieNameEditText = findViewById(R.id.movie_name_edit_text);
        addMovieButton = findViewById(R.id.add_movie_button);
        fetchMovieDetails = findViewById(R.id.fetch_details);
        MovieRating = findViewById(R.id.imdbRating);
        MoviesDescription=findViewById(R.id.Plot);
        MoviesCast =findViewById(R.id.Cast);


        // Handle fetch movie details button click
        fetchMovieDetails.setOnClickListener(v -> {
            String movieName = movieNameEditText.getText().toString().trim();
            if (movieName.isEmpty()) {
                Toast.makeText(AddMovieActivity.this, "Please enter the movie name", Toast.LENGTH_SHORT).show();
                return;
            }

            fetchMovieDetails(movieName);
        });

        // Handle add movie button click
        addMovieButton.setOnClickListener(v -> {
            if (movie != null) {
                saveMovieToFirebase(movie);
            } else {
                Toast.makeText(AddMovieActivity.this, "Please fetch movie details first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMovieDetails(String movieName) {
        Log.d("AddMovieActivity", "Fetching details for movie: " + movieName);

        Retrofit retrofit = RetrofitInstance.getRetrofitInstance();
        OMDBService service = retrofit.create(OMDBService.class);
        Call<MovieDetails> call = service.getMovieDetails(movieName, "9f8c09cf"); // Ensure API key is correct

        call.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MovieDetails movieDetails = response.body();

                    // Log the response data
                    Log.d("AddMovieActivity", "Movie details fetched successfully:");
                    Log.d("AddMovieActivity", "Rating: " + movieDetails.getImdbRating());
                    Log.d("AddMovieActivity", "Description: " + movieDetails.getPlot());
                    Log.d("AddMovieActivity", "Cast: " + movieDetails.getActors());
                    Log.d("AddMovieActivity", "Poster URL: " + movieDetails.getPoster());




                    rating = movieDetails.getImdbRating();
                    description = movieDetails.getPlot();
                    cast = movieDetails.getActors();
                    posterUrl = movieDetails.getPoster();
                    MovieRating.setText(rating);
                    MoviesDescription.setText(description);
                    MoviesCast.setText(cast);

String name = movieName.toString();
                    // Create Movie object with the fetched details

                    movie = new Movie(name, description, rating, cast, posterUrl);

                    // Show success message
                    Toast.makeText(AddMovieActivity.this, "Movie details fetched successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("AddMovieActivity", "Error fetching movie details: " + response.message());
                    Toast.makeText(AddMovieActivity.this, "Error fetching movie details", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                // Log the error for debugging
                Log.e("AddMovieActivity", "Failed to fetch movie details: " + t.getMessage());
                Toast.makeText(AddMovieActivity.this, "Failed to fetch movie details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveMovieToFirebase(Movie movie) {


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Save the movie to Firebase Firestore
        db.collection("movies")
                .add(movie) // Add movie object directly
                .addOnSuccessListener(documentReference -> {
                    Log.d("AddMovieActivity", "Movie added to Firebase successfully");
                    Toast.makeText(AddMovieActivity.this, "Movie Added", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> {
                    Log.e("AddMovieActivity", "Error adding movie to Firebase: " + e.getMessage());
                    Toast.makeText(AddMovieActivity.this, "Error adding movie: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
