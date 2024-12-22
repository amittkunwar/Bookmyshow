package com.example.bookmyshow;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {
    private Context context;
    private List<Movie> movieList;
    private OnMovieLongClickListener onMovieLongClickListener;

    // Long-click listener interface
    public interface OnMovieLongClickListener {
        void onMovieLongClick(Movie movie);
    }

    // Constructor
    public MovieAdapter(Context context, List<Movie> movieList, OnMovieLongClickListener onMovieLongClickListener) {
        this.context = context;
        this.movieList = movieList;
        this.onMovieLongClickListener = onMovieLongClickListener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        // Set movie details
        holder.movieNameTextView.setText(movie.getName());
        holder.movieDescriptionTextView.setText(movie.getDescription());

        // Apply bold formatting to "Rating:" label
        String ratingLabel = "Rating: ";
        SpannableStringBuilder ratingBuilder = new SpannableStringBuilder();
        ratingBuilder.append(ratingLabel).append(movie.getRating());
        ratingBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                0, ratingLabel.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.movieRatingTextView.setText(ratingBuilder);

        // Apply bold formatting to "Cast:" label
        String castLabel = "Cast: ";
        SpannableStringBuilder castBuilder = new SpannableStringBuilder();
        castBuilder.append(castLabel).append(movie.getCast());
        castBuilder.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
                0, castLabel.length(), SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.movieCastTextView.setText(castBuilder);

        // Load poster using Glide
        Glide.with(context)
                .load(movie.getPosterUrl())
                .placeholder(R.drawable.file_placeholder) // Optional placeholder image
                .into(holder.moviePosterImageView);

        // Handle long-click
        holder.itemView.setOnLongClickListener(v -> {
            if (onMovieLongClickListener != null) {
                onMovieLongClickListener.onMovieLongClick(movie);
            }
            return true; // Indicates the long-click was consumed
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    // ViewHolder for movie items
    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView movieNameTextView, movieDescriptionTextView, movieRatingTextView, movieCastTextView;
        ImageView moviePosterImageView;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            movieNameTextView = itemView.findViewById(R.id.movie_name_text_view);
            movieDescriptionTextView = itemView.findViewById(R.id.movie_description_text_view);
            movieRatingTextView = itemView.findViewById(R.id.movie_rating_text_view);
            movieCastTextView = itemView.findViewById(R.id.movie_cast_text_view);
            moviePosterImageView = itemView.findViewById(R.id.movie_poster_image_view);
        }
    }
}
