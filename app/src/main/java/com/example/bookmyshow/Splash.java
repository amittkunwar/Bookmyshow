// Splash.java
package com.example.bookmyshow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Splash extends AppCompatActivity {

    private static final String SHARED_PREFS_NAME = "MyPrefs";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String USER_TYPE = "userType"; // "user" or "admin"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        // Apply window insets if needed
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Check if user is already logged in
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(IS_LOGGED_IN, false);
        String userType = sharedPreferences.getString(USER_TYPE, null);

        new Handler().postDelayed(() -> {
            if (isLoggedIn) {
                if (userType.equals("user")) {
                    Intent intent = new Intent(Splash.this, UserActivity.class);
                    startActivity(intent);
                } else if (userType.equals("admin")) {
                    Intent intent = new Intent(Splash.this, AdminActivity.class);
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(Splash.this, SignupActivity.class);
                startActivity(intent);
            }
            finish();
        }, 2000); // 2000 ms delay
    }
}