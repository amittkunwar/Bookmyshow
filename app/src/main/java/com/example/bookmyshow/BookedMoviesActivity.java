package com.example.bookmyshow;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class BookedMoviesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_movies);

        // Initialize the LinearLayout where the bookings will be displayed
        LinearLayout bookingsList = findViewById(R.id.admin_bookings_list);

        // Get Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Fetch bookings from Firestore
        db.collection("bookings").get().addOnSuccessListener(queryDocumentSnapshots -> {
            // Loop through each document in the bookings collection
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                // Construct a string with booking information
                String bookingInfo = "Movie: " + document.getString("movie_name") +
                        "\nTheatre: " + document.getString("theatre") +
                        "\nDate: " + document.getString("date") +
                        "\nTime: " + document.getString("time") +
                        "\nSeats: " + document.get("seats").toString() +  // Assuming seats is a List or Array
                        "\nTotal Price: " + document.get("total_price") + "₹\n";

                // Create a new TextView to display this booking info
                TextView bookingText = new TextView(this);
                bookingText.setText(bookingInfo);
                bookingText.setPadding(8, 8, 8, 8);
                bookingText.setTextSize(16);

                // Add the TextView to the bookings list
                bookingsList.addView(bookingText);
            }
        }).addOnFailureListener(e -> {
            // Handle errors if any
            e.printStackTrace();
        });
    }
}
