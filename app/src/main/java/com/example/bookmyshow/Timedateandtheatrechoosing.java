package com.example.bookmyshow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Timedateandtheatrechoosing extends AppCompatActivity {

    private String selectedTheatre, selectedDate, selectedTime, selectedLanguage;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timedateandtheatrechoosing);

        // Get the movie name passed from the previous activity
        String movieName = getIntent().getStringExtra("movie_name");

        // Find views
        TextView selectedMovie = findViewById(R.id.selected_movie);
        Spinner theatreSpinner = findViewById(R.id.spinner_theatre);
        Spinner dateSpinner = findViewById(R.id.spinner_date);
        Spinner timeSpinner = findViewById(R.id.spinner_time);
        Spinner langSpinner = findViewById(R.id.spinner_language);
        Button confirmButton = findViewById(R.id.confirm_booking);

        // Set the movie name
        selectedMovie.setText("Selected Movie: " + (movieName != null ? movieName : "N/A"));

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();

        // Initialize language options
        List<String> languages = new ArrayList<>();
        languages.add("Hindi - 2D");
        languages.add("English - 2D");
        languages.add("Hindi - 3D");
        languages.add("English - 3D");

        // Set language spinner
        updateSpinnerAdapter(langSpinner, languages);

        // Fetch and populate theater spinner
        db.collection("theaters").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<String> theaters = new ArrayList<>();
                List<String> theaterIds = new ArrayList<>();

                for (DocumentSnapshot document : task.getResult()) {
                    theaters.add(document.getString("name"));
                    theaterIds.add(document.getId());
                }

                // Update theater spinner
                updateSpinnerAdapter(theatreSpinner, theaters);

                theatreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedTheatre = theaters.get(position);
                        String theaterId = theaterIds.get(position);

                        // Fetch dates and times for the selected theater
                        db.collection("theaters").document(theaterId)
                                .get().addOnSuccessListener(documentSnapshot -> {
                                    List<String> dates = (List<String>) documentSnapshot.get("dates");
                                    List<String> times = (List<String>) documentSnapshot.get("times");

                                    if (dates != null) {
                                        updateSpinnerAdapter(dateSpinner, dates);
                                    }

                                    if (times != null) {
                                        updateSpinnerAdapter(timeSpinner, times);
                                    }
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(
                                            Timedateandtheatrechoosing.this,
                                            "Failed to fetch details for the selected theater.",
                                            Toast.LENGTH_SHORT
                                    ).show();
                                });
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedTheatre = null;
                    }
                });

                dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedDate = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedDate = null;
                    }
                });

                timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedTime = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedTime = null;
                    }
                });

                langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        selectedLanguage = languages.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedLanguage = null;
                    }
                });
            } else {
                Toast.makeText(this, "Failed to load theaters.", Toast.LENGTH_SHORT).show();
            }
        });

        // Confirm button click
        confirmButton.setOnClickListener(v -> {
            if (selectedTheatre == null || selectedDate == null || selectedTime == null || selectedLanguage == null) {
                Toast.makeText(this, "Please select all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Display confirmation
            Toast.makeText(
                    this,
                    "Booking Details:\nMovie: " + movieName +
                            "\nTheatre: " + selectedTheatre +
                            "\nDate: " + selectedDate +
                            "\nTime: " + selectedTime +
                            "\nFormat: " + selectedLanguage,
                    Toast.LENGTH_SHORT
            ).show();

            // Navigate to SeatSelectionActivity with selected data
            Intent intent = new Intent(Timedateandtheatrechoosing.this, SeatSelectionActivity.class);
            intent.putExtra("movie_name", movieName);
            intent.putExtra("theatre", selectedTheatre);
            intent.putExtra("date", selectedDate);
            intent.putExtra("time", selectedTime);
            intent.putExtra("format", selectedLanguage);
            startActivity(intent);
        });
    }

    /**
     * Helper method to update spinner data.
     */
    private void updateSpinnerAdapter(Spinner spinner, List<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, data);
        spinner.setAdapter(adapter);
    }
}
