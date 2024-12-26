package com.example.bookmyshow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatSelectionActivity extends AppCompatActivity {

    private static final int ROWS = 10;
    private static final int COLUMNS = 10;

    private int pricePerSeat = 150; // Default seat price
    private List<String> selectedSeats = new ArrayList<>();
    private TextView selectedSeatsText, totalPriceText;
    private GridLayout seatGrid;

    private String movieName, theatre, date, time;

    // Firebase Firestore instance
    private FirebaseFirestore firestore;

    // Local price data for movies and theaters
    private static final Map<String, Map<String, Integer>> MOVIE_PRICES = new HashMap<>();

    static {
        Map<String, Integer> theaterPrices1 = new HashMap<>();
        theaterPrices1.put("PVR Cinemas", 200);
        theaterPrices1.put("Inox", 180);
        theaterPrices1.put("Carnival Cinemas", 210);
        MOVIE_PRICES.put("Transformers", theaterPrices1);

        Map<String, Integer> theaterPrices2 = new HashMap<>();
        theaterPrices2.put("PVR Cinemas", 250);
        theaterPrices2.put("Inox", 230);
        theaterPrices2.put("Carnival Cinemas", 250);
        MOVIE_PRICES.put("Inception", theaterPrices2);

        Map<String, Integer> theaterPrices3 = new HashMap<>();
        theaterPrices3.put("PVR Cinemas", 300);
        theaterPrices3.put("Inox", 280);
        theaterPrices3.put("Carnival Cinemas", 270);
        MOVIE_PRICES.put("Dangal", theaterPrices3);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Retrieve data from intent
        movieName = getIntent().getStringExtra("movie_name");
        theatre = getIntent().getStringExtra("theatre");
        date = getIntent().getStringExtra("date");
        time = getIntent().getStringExtra("time");

        // Initialize UI components
        selectedSeatsText = findViewById(R.id.selected_seats);
        totalPriceText = findViewById(R.id.total_price);
        seatGrid = findViewById(R.id.seat_grid);

        // Set default text
        selectedSeatsText.setText("Selected Seats: ");
        totalPriceText.setText("Total Price: ₹0");

        // Set price based on movie and theater
        setPriceLocally();

        // Dynamically create seat buttons
        createSeatButtons();

        // Handle confirm button click
        Button confirmButton = findViewById(R.id.confirm_seats);
        confirmButton.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(this, "Please select at least one seat!", Toast.LENGTH_SHORT).show();
                return;
            }
            saveBookingDataToFirestore();
        });
    }

    private void createSeatButtons() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                Button seatButton = new Button(this);
                seatButton.setText(String.valueOf((row * COLUMNS) + col + 1));
                seatButton.setTag((row * COLUMNS) + col + 1);
                seatButton.setOnClickListener(this::onSeatSelected);
                seatButton.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));

                seatGrid.addView(seatButton);
            }
        }
    }

    private void setPriceLocally() {
        if (MOVIE_PRICES.containsKey(movieName)) {
            Map<String, Integer> theaterPrices = MOVIE_PRICES.get(movieName);
            if (theaterPrices != null && theaterPrices.containsKey(theatre)) {
                pricePerSeat = theaterPrices.get(theatre);
                Toast.makeText(this, "Price per seat: ₹" + pricePerSeat, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Theatre not found for the selected movie.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Movie not found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void onSeatSelected(View view) {
        Button seatButton = (Button) view;
        String seatNumber = seatButton.getText().toString();

        if (selectedSeats.contains(seatNumber)) {
            selectedSeats.remove(seatNumber);
            seatButton.setBackgroundColor(getResources().getColor(android.R.color.darker_gray));
        } else {
            selectedSeats.add(seatNumber);
            seatButton.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        }

        // Update selected seats and total price
        selectedSeatsText.setText("Selected Seats: " + String.join(", ", selectedSeats));
        int totalPrice = selectedSeats.size() * pricePerSeat;
        totalPriceText.setText("Total Price: ₹" + totalPrice);
    }

    private void saveBookingDataToFirestore() {
        // Prepare booking data
        Map<String, Object> bookingData = new HashMap<>();
        bookingData.put("movie_name", movieName);
        bookingData.put("theatre", theatre);
        bookingData.put("date", date);
        bookingData.put("time", time);
        bookingData.put("seats", new ArrayList<>(selectedSeats));
        bookingData.put("total_price", selectedSeats.size() * pricePerSeat);

        // Save to Firestore
        firestore.collection("bookings")
                .add(bookingData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Booking confirmed!", Toast.LENGTH_SHORT).show();
                    navigateToConfirmationPage();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to confirm booking: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToConfirmationPage() {
        Intent confirmationIntent = new Intent(this, Confirmpage.class);
        confirmationIntent.putExtra("movie_name", movieName);
        confirmationIntent.putExtra("theatre", theatre);
        confirmationIntent.putExtra("date", date);
        confirmationIntent.putExtra("time", time);
        confirmationIntent.putExtra("seats", new ArrayList<>(selectedSeats));
        confirmationIntent.putExtra("total_price", selectedSeats.size() * pricePerSeat);
        startActivity(confirmationIntent);
    }
}
