package com.example.bookmyshow;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AddMovieActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText movieNameEditText, movieDescriptionEditText, movieRatingEditText, movieCastEditText;
    private Button addMovieButton, uploadPosterButton;
    private ImageView moviePosterImageView;

    private Uri imageUri; // To store the selected image URI
    private String posterUrl = ""; // To store the uploaded image URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_movie);

        // Initialize Views
        movieNameEditText = findViewById(R.id.movie_name_edit_text);
        movieDescriptionEditText = findViewById(R.id.movie_description_edit_text);
        movieRatingEditText = findViewById(R.id.movie_rating_edit_text);
        movieCastEditText = findViewById(R.id.movie_cast_edit_text);
        moviePosterImageView = findViewById(R.id.movie_poster_image_view);
        addMovieButton = findViewById(R.id.add_movie_button);
        uploadPosterButton = findViewById(R.id.upload_poster_button);

        // Handle upload poster button click
        uploadPosterButton.setOnClickListener(v -> openFileChooser());

        // Handle add movie button click
        addMovieButton.setOnClickListener(v -> {
            String movieName = movieNameEditText.getText().toString().trim();
            String description = movieDescriptionEditText.getText().toString().trim();
            String rating = movieRatingEditText.getText().toString().trim();
            String cast = movieCastEditText.getText().toString().trim();

            if (movieName.isEmpty() || description.isEmpty() || rating.isEmpty() || cast.isEmpty() || posterUrl.isEmpty()) {
                Toast.makeText(AddMovieActivity.this, "Please fill all fields and upload a poster", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a Movie object
            Movie movie = new Movie(movieName, description, rating, cast, posterUrl);

            // Save the movie to Firebase Firestore
            saveMovieToFirebase(movie);
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            moviePosterImageView.setImageURI(imageUri); // Display selected image
            uploadPosterToFirebase();
        }
    }

    private void uploadPosterToFirebase() {
        if (imageUri != null) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageReference = storage.getReference("movie_posters/" + System.currentTimeMillis() + ".jpg");

            storageReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                posterUrl = uri.toString(); // Save the URL for later use
                                Toast.makeText(AddMovieActivity.this, "Poster Uploaded", Toast.LENGTH_SHORT).show();
                            }))
                    .addOnFailureListener(e -> Toast.makeText(AddMovieActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMovieToFirebase(Movie movie) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("movies")
                .add(movie) // Save the movie object directly
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddMovieActivity.this, "Movie Added", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> Toast.makeText(AddMovieActivity.this, "Error adding movie: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
