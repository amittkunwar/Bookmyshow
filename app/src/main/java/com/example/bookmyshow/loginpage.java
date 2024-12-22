package com.example.bookmyshow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class loginpage extends AppCompatActivity {

    private EditText phoneNumberEditText;
    private Button verifyButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check login state
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);
        String userRole = preferences.getString("userRole", "");

        if (isLoggedIn) {
            if ("Admin".equals(userRole)) {
                navigateToAdminActivity();
            } else if ("User".equals(userRole)) {
                navigateToUserActivity();
            }
            return; // Exit onCreate if already logged in
        }

        // If not logged in, proceed with the login page
        setContentView(R.layout.activity_loginpage);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference();

        // Find the UI elements
        phoneNumberEditText = findViewById(R.id.phone_number);
        verifyButton = findViewById(R.id.verify_button);

        // Set the onClickListener for the Verify button
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneNumberEditText.getText().toString().trim();

                if (phoneNumber.isEmpty() || phoneNumber.length() < 10) {
                    phoneNumberEditText.setError("Enter a valid phone number");
                    phoneNumberEditText.requestFocus();
                    return;
                }

                // Check if the phone number exists in the Admins or Users database
                checkUserRole(phoneNumber);
            }
        });
    }

    private void checkUserRole(String phoneNumber) {
        databaseReference.child("Admins").child(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Save login state and navigate to AdminActivity
                    saveLoginState("Admin");
                    navigateToAdminActivity();
                } else {
                    checkIfUserExists(phoneNumber);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(loginpage.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfUserExists(String phoneNumber) {
        databaseReference.child("Users").child(phoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Save login state and navigate to UserActivity
                    saveLoginState("User");
                    navigateToUserActivity();
                } else {
                    Toast.makeText(loginpage.this, "Phone number not registered", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(loginpage.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLoginState(String role) {
        SharedPreferences preferences = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        preferences.edit()
                .putBoolean("isLoggedIn", true)
                .putString("userRole", role)
                .apply();
    }

    private void navigateToAdminActivity() {
        Intent intent = new Intent(loginpage.this, AdminActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToUserActivity() {
        Intent intent = new Intent(loginpage.this, UserActivity.class);
        startActivity(intent);
        finish();
    }
}
