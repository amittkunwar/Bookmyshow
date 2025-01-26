// loginpage.java
package com.example.bookmyshow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class loginpage extends AppCompatActivity {

    private EditText etPhoneNumber, etOtp;
    private Button btnLoginAdmin, btnSendOtp, btnVerifyOtp;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference adminRef;
    private String verificationId;
    private static final String SHARED_PREFS_NAME = "MyPrefs";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String USER_TYPE = "userType"; // "user" or "admin"

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);

        etPhoneNumber = findViewById(R.id.et_login_phone_number);
        etOtp = findViewById(R.id.et_login_otp);
        btnLoginAdmin = findViewById(R.id.btn_login_admin);
        btnSendOtp = findViewById(R.id.btn_login_send_otp);
        btnVerifyOtp = findViewById(R.id.btn_login_verify);

        firebaseAuth = FirebaseAuth.getInstance();
        adminRef = FirebaseDatabase.getInstance().getReference("Admins");

        // Admin Login Without OTP
        btnLoginAdmin.setOnClickListener(v -> {
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(loginpage.this, "Enter a valid phone number!", Toast.LENGTH_SHORT).show();
                return;
            }

            checkAdminLogin(phoneNumber);
        });

        // OTP-based Login for Users
        btnSendOtp.setOnClickListener(v -> {
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(loginpage.this, "Enter a valid phone number!", Toast.LENGTH_SHORT).show();
                return;
            }

            sendOtpForUser(phoneNumber);
        });

        btnVerifyOtp.setOnClickListener(v -> {
            String otp = etOtp.getText().toString().trim();
            if (TextUtils.isEmpty(otp)) {
                Toast.makeText(loginpage.this, "Enter the OTP!", Toast.LENGTH_SHORT).show();
                return;
            }
            verifyOtp(otp);
        });
    }

    private void checkAdminLogin(String phoneNumber) {
        adminRef.child(phoneNumber).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    // Admin login logic
                    Toast.makeText(loginpage.this, "Admin Login Successful!", Toast.LENGTH_SHORT).show();

                    // Save login state and user type
                    SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(IS_LOGGED_IN, true);
                    editor.putString(USER_TYPE, "admin");
                    editor.apply();

                    Intent intent = new Intent(loginpage.this, AdminActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(loginpage.this, "Not an Admin number!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(loginpage.this, "Failed to check admin status. Try again!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(loginpage.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void sendOtpForUser(String phoneNumber) {
        adminRef.child(phoneNumber).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DataSnapshot snapshot = task.getResult();
                if (snapshot.exists()) {
                    Toast.makeText(loginpage.this, "Admin must use Login button!", Toast.LENGTH_SHORT).show();
                } else {
                    sendVerificationCode(phoneNumber);
                }
            } else {
                Toast.makeText(loginpage.this, "Failed to check admin status. Try again!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                loginUser(credential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(loginpage.this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("loginpage", "Verification Failed", e);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                super.onCodeSent(s, token);
                                verificationId = s;
                                Toast.makeText(loginpage.this, "OTP Sent!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyOtp(String otp) {
        if (verificationId != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
            loginUser(credential);
        } else {
            Toast.makeText(loginpage.this, "Verification ID is null. Try again!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loginUser(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(loginpage.this, "User Login Successful!", Toast.LENGTH_SHORT).show();

                // Save login state and user type
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(IS_LOGGED_IN, true);
                editor.putString(USER_TYPE, "user");
                editor.apply();

                Intent intent = new Intent(loginpage.this, UserActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(loginpage.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}