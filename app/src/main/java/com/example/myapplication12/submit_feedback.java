package com.example.myapplication12;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication12.models.UserModel;
import com.example.myapplication12.utilities.PreferenceManager;
import com.example.myapplication12.utilities.androidutil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class submit_feedback extends AppCompatActivity {
    private RatingBar ratingBar;
    private EditText feedbackEditText;
    private Button updateButton;
    private String email;
    UserModel userModel;
    PreferenceManager preferenceManager;
    private float currentRating; // To store the current rating

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userModel= androidutil.getUserModelFromIntent(getIntent());
        email=userModel.getEmail();
        setContentView(R.layout.feedback);
        preferenceManager = new PreferenceManager(getApplicationContext());

//        preferenceManager=
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        feedbackEditText = (EditText) findViewById(R.id.editTextText2);
        updateButton = findViewById(R.id.profle_update_btn_newF);

        updateButton.setOnClickListener(v -> updateFeedback());

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(submit_feedback.this, "Rating: " + rating, Toast.LENGTH_SHORT).show();
                currentRating = rating;
            }
        });
    }

    private void updateFeedback() {
        String profileEmail = preferenceManager.getString("user_email");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Login_Details").document(email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Retrieve current feedback and rating
                            String currentFeedback = document.contains("Feedback") ? document.getString("Feedback") : "";
                            String currentRatingString = document.contains("Rating") ? document.getString("Rating") : "Rating:0";

                            // Extract the numeric part of the rating
                            float oldRatingValue = Float.parseFloat(currentRatingString.replaceAll("[^\\d.]", ""));

                            // Calculate new average rating (assuming every new rating should be averaged with the old one)
                            float newAverageRating = (oldRatingValue + currentRating) / 2;

                            // Update feedback
                            String feedbackText = feedbackEditText.getText().toString();
                            feedbackText = "' " + feedbackText + " '" + " ~By " + profileEmail;
                            String combinedFeedback = currentFeedback.isEmpty() ? feedbackText : currentFeedback + "\n" + feedbackText;

                            // Prepare update data
                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("Rating", "Rating:" + newAverageRating); // Update the average rating
                            updateData.put("Feedback", combinedFeedback);

                            // Update Firestore
                            db.collection("Login_Details").document(email).update(updateData)
                                    .addOnCompleteListener(innerTask -> {
                                        if (innerTask.isSuccessful()) {
                                            Toast.makeText(submit_feedback.this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Handle error
                                            if (innerTask.getException() != null) {
                                                Log.e("submit_feedback", "Error submitting feedback", innerTask.getException());
                                                Toast.makeText(submit_feedback.this, "Error: " + innerTask.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                        } else {
                            Toast.makeText(submit_feedback.this, "Document does not exist", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // Handle task failure
                        Log.e("submit_feedback", "Error fetching document", task.getException());
                        Toast.makeText(submit_feedback.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }


}
