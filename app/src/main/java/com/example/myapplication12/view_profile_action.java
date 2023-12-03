package com.example.myapplication12;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication12.models.UserModel;
import com.example.myapplication12.utilities.PreferenceManager;
import com.example.myapplication12.utilities.androidutil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class view_profile_action extends AppCompatActivity {
    private EditText feedbackEditText;
    private Button updateButton;
    private String email;
    UserModel userModel;
    PreferenceManager preferenceManager;
    private float currentRating; // To store the current rating
    private ImageView profileImageView;
    private TextView emailTextView;
    private TextView userIdTextView;
    private TextView skillsTextView;
    private RatingBar ratingBar;
    private TextView feedbackTextView;
    private Button goBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userModel= androidutil.getUserModelFromIntent(getIntent());
        email=userModel.getEmail();
        setContentView(R.layout.view_profile);
        profileImageView = findViewById(R.id.profile_image_view);
        emailTextView = findViewById(R.id.profile_email);
        userIdTextView = findViewById(R.id.profile_user_id);
        skillsTextView = findViewById(R.id.profile_skills);
        ratingBar = findViewById(R.id.profile_rating_bar);

        feedbackTextView = findViewById(R.id.profile_feedback);
        goBackButton = findViewById(R.id.profile_go_back_button);
        fetchUserData();

    }
    private void fetchUserData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Login_Details").document(email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Set email and user ID
                            emailTextView.setText(document.getString("email"));
                            userIdTextView.setText(document.getString("userId"));

                            // Set skills
                            skillsTextView.setText(document.getString("skills"));

//                            // Set profile image
//                            String imageUrl = document.getString("image");
//                            if (imageUrl != null && !imageUrl.isEmpty()) {
//                                Glide.with(view_profile_action.this).load(imageUrl).into(profileImageView);
//                            }

                            // Set rating
                            String ratingString = document.getString("Rating");
                            if (ratingString != null && !ratingString.isEmpty()) {
                                float ratingValue = Float.parseFloat(ratingString.replaceAll("[^\\d.]", ""));
                                ratingBar.setRating(ratingValue);
                            }

                            // Set feedback
                            feedbackTextView.setText(document.getString("Feedback"));
                        } else {
                            Toast.makeText(view_profile_action.this, "User does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(view_profile_action.this, "Failed to fetch user data.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}
