package com.example.bookmyshow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MoviesFragment extends Fragment {

    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;
    private List<Movie> movieList;

    public MoviesFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapter(getContext(), movieList, this::showDeleteDialog);
        recyclerView.setAdapter(movieAdapter);

        loadMoviesFromFirestore();

        return view;
    }

    private void loadMoviesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("movies").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Movie> movies = queryDocumentSnapshots.toObjects(Movie.class);
                    movieList.clear();
                    movieList.addAll(movies);
                    movieAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading movies: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDeleteDialog(Movie movie) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Movie")
                .setMessage("Are you sure you want to delete " + movie.getName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> deleteMovieFromFirestore(movie))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteMovieFromFirestore(Movie movie) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("movies")
                .whereEqualTo("name", movie.getName()) // Match the movie by name
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        queryDocumentSnapshots.getDocuments().forEach(document -> {
                            db.collection("movies").document(document.getId()).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(getContext(), movie.getName() + " deleted", Toast.LENGTH_SHORT).show();
                                        movieList.remove(movie);
                                        movieAdapter.notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getContext(), "Error deleting movie: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error finding movie: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
