package com.example.bookmyshow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText etAdminPhone;
    private Button btnAdminLogin;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        etAdminPhone = findViewById(R.id.et_admin_phone);
        btnAdminLogin = findViewById(R.id.btn_admin_login_submit);
        firebaseAuth = FirebaseAuth.getInstance();

        // Reference to the "Admins" node in the Firebase Realtime Database
        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference("Admins");

        btnAdminLogin.setOnClickListener(v -> {
            String adminPhone = etAdminPhone.getText().toString().trim();

            if (adminPhone.isEmpty()) {
                Toast.makeText(this, "Please enter the phone number", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the phone number exists in the "Admins" node
            adminRef.child(adminPhone).get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    // Admin login successful
                    Toast.makeText(this, "Admin Login Successful!", Toast.LENGTH_SHORT).show();

                    // Navigate to AdminActivity
                    Intent intent = new Intent(AdminLoginActivity.this, AdminActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // If not an admin, check Firebase Authentication for normal user
                    firebaseAuth.fetchSignInMethodsForEmail(adminPhone + "@example.com")
                            .addOnCompleteListener(authTask -> {
                                if (authTask.isSuccessful() && authTask.getResult().getSignInMethods() != null
                                        && !authTask.getResult().getSignInMethods().isEmpty()) {
                                    // Normal user login successful
                                    Toast.makeText(this, "User Login Successful!", Toast.LENGTH_SHORT).show();

                                    // Navigate to UserActivity
                                    Intent intent = new Intent(AdminLoginActivity.this, UserActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Phone number not found in either database
                                    Toast.makeText(this, "This phone number is not registered!", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }).addOnFailureListener(e -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }
}
