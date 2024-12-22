package com.example.bookmyshow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapterUser extends RecyclerView.Adapter<MovieAdapterUser.MovieViewHolder> {

    private final Context context;
    private final List<Movie2> movieList;

    // Constructor
    public MovieAdapterUser(Context context, List<Movie2> movieList) {
        this.context = context;
        this.movieList = movieList;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie2, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie2 movie = movieList.get(position);

        // Populate views with movie data
        holder.movieTitle.setText(movie.getName() != null ? movie.getName() : "No Title Available");
        holder.movieRating.setText(movie.getRating() != null ? movie.getRating() : "N/A");

        // Load movie poster
        if (movie.getPosterUrl() != null && !movie.getPosterUrl().isEmpty()) {
            Picasso.get().load(movie.getPosterUrl()).into(holder.moviePoster);
        } else {
            holder.moviePoster.setImageResource(R.drawable.file_placeholder); // Default image
        }

        // OnClickListener for each movie card
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MovieDetailActivity.class);
            intent.putExtra("name", movie.getName());
            intent.putExtra("description", movie.getDescription());
            intent.putExtra("rating", movie.getRating());
            intent.putExtra("cast", movie.getCast());
            intent.putExtra("posterUrl", movie.getPosterUrl());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    // ViewHolder
    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView movieTitle, movieRating;
        ImageView moviePoster;

        @SuppressLint("WrongViewCast")
        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            movieTitle = itemView.findViewById(R.id.movieTitle);
            movieRating = itemView.findViewById(R.id.movieRating);
            moviePoster = itemView.findViewById(R.id.moviePoster);
        }
    }
}
