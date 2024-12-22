package com.example.bookmyshow;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private ViewPager2 viewPager2;
    private RecyclerView recyclerViewMovies;
    private MovieAdapterUser movieAdapter;
    private List<Movie2> movieList; // Update to Movie2 class
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the ViewPager for ad banner
        viewPager2 = view.findViewById(R.id.viewPagerAdBanner);
        List<Integer> bannerImages = new ArrayList<>();
        bannerImages.add(R.drawable.banner1);
        bannerImages.add(R.drawable.banner2);

        BannerAdapter adapter = new BannerAdapter(requireContext(), bannerImages);
        viewPager2.setAdapter(adapter);

        // Automatic sliding logic for banners
        runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPager2.getCurrentItem();
                int nextItem = (currentItem + 1) % bannerImages.size();
                viewPager2.setCurrentItem(nextItem, true);
                handler.postDelayed(this, 3000); // Slide every 3 seconds
            }
        };
        handler.postDelayed(runnable, 2000);

        // Initialize RecyclerView for displaying movies
        recyclerViewMovies = view.findViewById(R.id.recyclerViewMovies);
        recyclerViewMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)); // Horizontal layout manager

        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapterUser(getContext(), movieList);
        recyclerViewMovies.setAdapter(movieAdapter);

        // Load movies from Firestore
        loadMoviesFromFirestore();

        return view;
    }

    private void loadMoviesFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("movies").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Mapping Firestore document to Movie2 object
                    List<Movie2> movies = queryDocumentSnapshots.toObjects(Movie2.class);
                    movieList.clear();
                    movieList.addAll(movies);
                    movieAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading movies: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(runnable); // Stop sliding when the view is destroyed
    }
}

