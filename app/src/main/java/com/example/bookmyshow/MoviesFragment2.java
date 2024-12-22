package com.example.bookmyshow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class MoviesFragment2 extends Fragment {

    private RecyclerView recyclerViewMoviesGrid;
    private MovieAdapterUser movieAdapter;
    private List<Movie2> movieList;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies2, container, false);

        recyclerViewMoviesGrid = view.findViewById(R.id.recyclerViewMovies);
        recyclerViewMoviesGrid.setLayoutManager(new GridLayoutManager(getContext(), 2)); // 2 columns

        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapterUser(getContext(), movieList);
        recyclerViewMoviesGrid.setAdapter(movieAdapter);

        loadMoviesFromFirestore();

        return view;
    }

    private void loadMoviesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("movies").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Movie2> movies = queryDocumentSnapshots.toObjects(Movie2.class);
                    movieList.clear();
                    movieList.addAll(movies);
                    movieAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading movies: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}


