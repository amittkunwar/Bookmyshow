package com.example.bookmyshow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class MovieDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        // Find views
        TextView movieName = findViewById(R.id.movie_name);
        TextView movieDescription = findViewById(R.id.movie_description);
        TextView movieRating = findViewById(R.id.movie_rating);
        TextView movieCast = findViewById(R.id.movie_cast);
        ImageView moviePoster = findViewById(R.id.movie_poster);
        Button bookTicketsButton = findViewById(R.id.book_tickets_button);

        // Get movie details from Intent extras
        String name = getIntent().getStringExtra("name");
        String description = getIntent().getStringExtra("description");
        String rating = getIntent().getStringExtra("rating");
        String cast = getIntent().getStringExtra("cast");
        String posterUrl = getIntent().getStringExtra("posterUrl");

        if (name == null || description == null || rating == null || cast == null) {
            Toast.makeText(this, "Missing movie details", Toast.LENGTH_SHORT).show();
        }

        // Populate the views
        movieName.setText(name != null ? name : "N/A");
        movieDescription.setText(description != null && !description.isEmpty() ? description : "Description not available.");
        movieRating.setText(rating != null ? "Rating: " + rating : "Rating: N/A");
        movieCast.setText(cast != null && !cast.isEmpty() ? "Cast: " + cast : "Cast: N/A");

        // Load poster image using Glide
        if (posterUrl != null && !posterUrl.isEmpty() && Patterns.WEB_URL.matcher(posterUrl).matches()) {
            Glide.with(this)
                    .load(posterUrl)
                    .placeholder(R.drawable.file_placeholder) // Default placeholder
                    .error(R.drawable.file_placeholder) // Error placeholder
                    .into(moviePoster);
        } else {
            moviePoster.setImageResource(R.drawable.file_placeholder); // Default placeholder
        }

        // Set click listener for "Book Tickets" button
        bookTicketsButton.setOnClickListener(v -> {
            // Pass the movie name to the next activity
            Intent intent = new Intent(MovieDetailActivity.this, Timedateandtheatrechoosing.class);
            intent.putExtra("movie_name", name); // Pass movie name
            startActivity(intent); // Start BookingActivity
        });
    }
}
