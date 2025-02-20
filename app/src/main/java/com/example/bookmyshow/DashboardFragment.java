package com.example.bookmyshow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DashboardFragment extends Fragment {

    private Button addMovieButton;
    private Button adminBookingsListButton;
    private Button addDetailsButton; // Add reference for the new button

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize Buttons
        addMovieButton = view.findViewById(R.id.add_movie_button);
        adminBookingsListButton = view.findViewById(R.id.admin_bookings_list);
        addDetailsButton = view.findViewById(R.id.add_details_button); // Initialize new button

        // Handle Add Movie button click
        addMovieButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddMovieActivity.class);
            startActivity(intent);
        });

        // Handle Admin Bookings List button click
        adminBookingsListButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), BookedMoviesActivity.class);
            startActivity(intent);
        });

        // Handle Add Movie Detail button click
        addDetailsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddMovieDetailsActivity.class);
            startActivity(intent);
        });

        return view;
    }
}
