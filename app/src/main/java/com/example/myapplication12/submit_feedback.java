package com.example.myapplication12;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class submit_feedback extends AppCompatActivity {
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback); // replace with your layout file name

        ratingBar = (RatingBar) findViewById(R.id.rating_bar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                Toast.makeText(submit_feedback.this, "Rating: " + rating, Toast.LENGTH_SHORT).show();
                // You can also store this rating value in a variable or use it as needed
            }
        });
    }

}
