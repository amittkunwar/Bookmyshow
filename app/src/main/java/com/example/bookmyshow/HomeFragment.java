package com.example.bookmyshow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {


    private RecyclerView recyclerViewMovies;
    private MovieAdapterUser movieAdapter;
    private List<Movie2> movieList; // Update to Movie2 class
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private CardView locationCard;

    private TextView currentLocation ;

    private FusedLocationProviderClient fusedLocationClient;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the FusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // Initialize the ViewPager for ad banner
        locationCard = view.findViewById(R.id.location);

        List<Integer> bannerImages = new ArrayList<>();
        bannerImages.add(R.drawable.banner1);
        bannerImages.add(R.drawable.banner2);

        currentLocation = view.findViewById(R.id.current_location);








        recyclerViewMovies = view.findViewById(R.id.recyclerViewMovies);
        recyclerViewMovies.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)); // Horizontal layout manager

        movieList = new ArrayList<>();
        movieAdapter = new MovieAdapterUser(getContext(), movieList);
        recyclerViewMovies.setAdapter(movieAdapter);

        // Load movies from Firestore
        loadMoviesFromFirestore();

        // Set click listener for location card
        locationCard.setOnClickListener(v -> {
            // Fetch current location (optional)
            fetchCurrentLocation();

        });

        return view;
    }

    private void fetchCurrentLocation() {
        // Check if permission is granted
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Request location permissions
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            return;
        }

        // Get the last known location
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), location -> {
                    if (location != null) {
                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        // Use Geocoder to fetch the address from latitude and longitude
                        Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(userLocation.latitude, userLocation.longitude, 1);
                            if (addresses != null && !addresses.isEmpty()) {
                                Address address = addresses.get(0);
                                String addressLine = address.getAddressLine(0); // Full address
                                String city = address.getLocality(); // City
                                String country = address.getCountryName(); // Country

                                // Display or use the address details
                                Toast.makeText(getContext(), "Current Location: " + addressLine, Toast.LENGTH_SHORT).show();
                                currentLocation.setText("Current Location :"  + addressLine);
                                // You can update the map camera or other UI elements with the address here
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
