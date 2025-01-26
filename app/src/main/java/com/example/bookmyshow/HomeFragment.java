package com.example.bookmyshow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerViewMovies;
    private MovieAdapterUser movieAdapter;
    private List<Movie2> movieList;
    private CardView locationCard;
    private TextView currentLocation;
    private TextView detectLocationText; // TextView for detecting location
    private FusedLocationProviderClient fusedLocationClient;
    private SharedPreferences sharedPreferences;
    private Handler handler = new Handler(Looper.getMainLooper());
    private ImageView movieIcon; // Add movieicon reference

    private static final String PREFS_NAME = "UserLocationPrefs";
    private static final String KEY_LOCATION = "currentLocation";

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize SharedPreferences for storing location
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Initialize the FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Initialize views
        locationCard = view.findViewById(R.id.location);
        currentLocation = view.findViewById(R.id.current_location);
        detectLocationText = view.findViewById(R.id.detectLocationText); // Initialize the TextView
        movieIcon = view.findViewById(R.id.movieicon); // Initialize movieicon

        // Set up click listener for the "Detect my Location" TextView
        detectLocationText.setOnClickListener(v -> fetchCurrentLocation());

        // Set up click listener for the "Current Location" text to edit location
        currentLocation.setOnClickListener(v -> editLocationDialog());

        // Navigate to FragmentMovies2 when movieIcon is clicked
        movieIcon.setOnClickListener(v -> navigateToMoviesFragment());

        // Initialize RecyclerView for movies
        recyclerViewMovies = view.findViewById(R.id.recyclerViewMovies);
        recyclerViewMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapterUser(getContext(), movieList);
        recyclerViewMovies.setAdapter(movieAdapter);

        // Load movies from Firestore
        loadMoviesFromFirestore();

        // Detect location only when first loaded
        if (sharedPreferences.getString(KEY_LOCATION, null) == null) {
            fetchCurrentLocation();
        } else {
            currentLocation.setText(sharedPreferences.getString(KEY_LOCATION, "Unknown Location"));
        }

        return view;
    }

    // Navigate to FragmentMovies2
    private void navigateToMoviesFragment() {
        MoviesFragment2 fragmentMovies2 = new MoviesFragment2();
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace the current fragment with FragmentMovies2
        transaction.replace(R.id.fragment_container, fragmentMovies2);
        transaction.addToBackStack(null); // Add to back stack for navigation
        transaction.commit();
    }

    // Fetch the current location
    private void fetchCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(userLocation.latitude, userLocation.longitude, 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                String city = address.getLocality();

                                if (city != null && !city.isEmpty()) {
                                    saveLocation(city);
                                    currentLocation.setText(city);
                                } else {
                                    Toast.makeText(getContext(), "Unable to fetch city name", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Unable to get address", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Geocoder error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Location not available", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Save location in SharedPreferences
    private void saveLocation(String location) {
        sharedPreferences.edit().putString(KEY_LOCATION, location).apply();
    }

    // Display a dialog to edit the location
    private void editLocationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Edit Location");

        // Input field for location
        final EditText input = new EditText(requireContext());
        input.setText(currentLocation.getText().toString()); // Pre-fill with current location
        builder.setView(input);

        // Save button
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newLocation = input.getText().toString().trim();
            if (!newLocation.isEmpty()) {
                saveLocation(newLocation);
                currentLocation.setText(newLocation);
                Toast.makeText(getContext(), "Location updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Location cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });

        // Cancel button
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}
