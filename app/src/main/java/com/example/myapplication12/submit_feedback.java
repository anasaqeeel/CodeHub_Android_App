package com.example.myapplication12;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication12.models.UserModel;
import com.example.myapplication12.utilities.androidutil;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class submit_feedback extends AppCompatActivity {
    private RatingBar ratingBar;
    private EditText feedbackEditText;
    private Button updateButton;
    private String email;
    UserModel userModel;
    private float currentRating; // To store the current rating

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userModel= androidutil.getUserModelFromIntent(getIntent());
        email=userModel.getEmail();
        setContentView(R.layout.feedback);

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
        String ratingString = "Rating: " + currentRating;
        String feedbackText = feedbackEditText.getText().toString(); // Get feedback text from EditText
        Log.d("ustad", ratingString + " | Feedback: " + feedbackText);

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("Rating", ratingString);
        updateData.put("Feedback", feedbackText); // Add feedback text to the update data
//        updateData.put("email",email);
//        updateData.put("image",userModel.)

        FirebaseFirestore.getInstance().collection("Login_Details").document(email).update(updateData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(submit_feedback.this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        // Log the error or show a more descriptive error message
                        if (task.getException() != null) {
                            Log.e("submit_feedback", "Error submitting feedback", task.getException());
                            Toast.makeText(submit_feedback.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
