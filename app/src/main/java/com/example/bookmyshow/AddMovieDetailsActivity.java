package com.example.bookmyshow;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddMovieDetailsActivity extends AppCompatActivity {

    private EditText inputTheaterName, inputDate, inputTime;
    private Button addDateButton, addTimeButton, submitButton;
    private TextView datesListText, timesListText;

    private List<String> datesList = new ArrayList<>();
    private List<String> timesList = new ArrayList<>();

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie_details);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Initialize UI elements
        inputTheaterName = findViewById(R.id.input_theater_name);
        inputDate = findViewById(R.id.input_date);
        inputTime = findViewById(R.id.input_time);
        addDateButton = findViewById(R.id.add_date_button);
        addTimeButton = findViewById(R.id.add_time_button);
        submitButton = findViewById(R.id.submit_button);
        datesListText = findViewById(R.id.dates_list_text);
        timesListText = findViewById(R.id.times_list_text);

        // Add date to the list
        addDateButton.setOnClickListener(v -> {
            String date = inputDate.getText().toString().trim();
            if (!date.isEmpty()) {
                datesList.add(date);
                updateDatesList();
                inputDate.setText("");
            } else {
                Toast.makeText(this, "Please enter a date", Toast.LENGTH_SHORT).show();
            }
        });

        // Add time to the list
        addTimeButton.setOnClickListener(v -> {
            String time = inputTime.getText().toString().trim();
            if (!time.isEmpty()) {
                timesList.add(time);
                updateTimesList();
                inputTime.setText("");
            } else {
                Toast.makeText(this, "Please enter a time", Toast.LENGTH_SHORT).show();
            }
        });

        // Submit data to Firestore
        submitButton.setOnClickListener(v -> submitDataToFirestore());
    }

    private void updateDatesList() {
        datesListText.setText("Dates: " + datesList.toString());
    }

    private void updateTimesList() {
        timesListText.setText("Times: " + timesList.toString());
    }

    private void submitDataToFirestore() {
        String theaterName = inputTheaterName.getText().toString().trim();

        if (theaterName.isEmpty() || datesList.isEmpty() || timesList.isEmpty()) {
            Toast.makeText(this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data
        Map<String, Object> theaterData = new HashMap<>();
        theaterData.put("name", theaterName);
        theaterData.put("dates", datesList);
        theaterData.put("times", timesList);

        // Save to Firestore
        firestore.collection("theaters")
                .document(theaterName)
                .set(theaterData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Data added successfully!", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Error adding data: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void clearFields() {
        inputTheaterName.setText("");
        datesList.clear();
        timesList.clear();
        updateDatesList();
        updateTimesList();
    }
}
