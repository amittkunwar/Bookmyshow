package com.example.bookmyshow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Find the Logout button
        view.findViewById(R.id.logout).setOnClickListener(v -> logoutUser());

        return view;
    }

    private void logoutUser() {
        // Clear login state from SharedPreferences
        requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        // Sign out from Firebase Authentication (if used)
        FirebaseAuth.getInstance().signOut();

        // Navigate to LoginActivity
        Intent intent = new Intent(getActivity(), loginpage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear backstack
        startActivity(intent);
    }
}
