// SignupActivity.java
package com.example.bookmyshow;

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

import java.util.concurrent.TimeUnit;

public class SignupActivity extends AppCompatActivity {

    private EditText etPhoneNumber, etOtp;
    private Button btnSendOtp, btnVerifyOtp, btnAdminLogin;
    private FirebaseAuth mAuth;
    private String verificationId;
    private static final String SHARED_PREFS_NAME = "MyPrefs";
    private static final String IS_LOGGED_IN = "isLoggedIn";
    private static final String USER_TYPE = "userType"; // "user" or "admin"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        etPhoneNumber = findViewById(R.id.et_signup_phone_number);
        etOtp = findViewById(R.id.et_signup_otp);
        btnSendOtp = findViewById(R.id.btn_signup_send_otp);
        btnVerifyOtp = findViewById(R.id.btn_signup_verify);
        btnAdminLogin = findViewById(R.id.btn_admin_login); // Admin login button

        mAuth = FirebaseAuth.getInstance();

        // Send OTP button functionality
        btnSendOtp.setOnClickListener(v -> {
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            if (TextUtils.isEmpty(phoneNumber)) {
                Toast.makeText(SignupActivity.this, "Enter a valid phone number!", Toast.LENGTH_SHORT).show();
                return;
            }

            sendVerificationCode(phoneNumber);
        });

        // Verify OTP button functionality
        btnVerifyOtp.setOnClickListener(v -> {
            String otp = etOtp.getText().toString().trim();
            if (TextUtils.isEmpty(otp)) {
                Toast.makeText(SignupActivity.this, "Enter the OTP!", Toast.LENGTH_SHORT).show();
                return;
            }

            verifyCode(otp);
        });

        // Admin login button functionality
        btnAdminLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignupActivity.this, loginpage.class);
            startActivity(intent);
        });
    }

    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                                signUpUser(credential, "user"); // Pass "user" as userType
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(SignupActivity.this, "Verification Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.e("SignUp", "Verification Failed", e);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                                super.onCodeSent(s, token);
                                verificationId = s;
                                Toast.makeText(SignupActivity.this, "OTP Sent!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signUpUser(credential, "user"); // Pass "user" as userType
    }

    private void signUpUser(PhoneAuthCredential credential, String userType) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(SignupActivity.this, "Sign-Up Successful!", Toast.LENGTH_SHORT).show();

                // Save login state and user type
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(IS_LOGGED_IN, true);
                editor.putString(USER_TYPE, userType);
                editor.apply();

                Intent intent = new Intent(SignupActivity.this, UserActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(SignupActivity.this, "Sign-Up Failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}