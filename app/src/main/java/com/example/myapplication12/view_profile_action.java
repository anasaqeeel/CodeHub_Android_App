package com.example.myapplication12;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.Base64;
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
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userModel= androidutil.getUserModelFromIntent(getIntent());
        email=userModel.getEmail();
        setContentView(R.layout.view_profile);
        profileImageView = findViewById(R.id.profile_image_viewvp);
        emailTextView = findViewById(R.id.profile_email);
        userIdTextView = findViewById(R.id.profile_user_id);
        skillsTextView = findViewById(R.id.profile_skills);
        ratingBar = findViewById(R.id.profile_rating_bar);
        back=findViewById(R.id.profile_go_back_button);
        back.setOnClickListener(v->goback());

        feedbackTextView = findViewById(R.id.profile_feedback);
        goBackButton = findViewById(R.id.profile_go_back_button);
        fetchUserData();

    }
    private void goback(){
        Intent intent=new Intent(view_profile_action.this,main.class);
        startActivity(intent);
    }
    private void fetchUserData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Login_Details").document(email).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null && document.exists()) {
                            // Set email and user ID

                            String encodedImage = task.getResult().getString("image");
                            if (encodedImage != null && !encodedImage.isEmpty()) {
                                Bitmap decodedBitmap = decodeImage(encodedImage);
                                Bitmap circularBitmap = getCircularBitmap(decodedBitmap);
                                profileImageView.setImageBitmap(circularBitmap);
                            }

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

    private Bitmap decodeImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    public Bitmap getCircularBitmap(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int min = Math.min(width, height);
        Bitmap circularBitmap = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(circularBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, min, min);
        RectF rectF = new RectF(rect);

        canvas.drawOval(rectF, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return circularBitmap;
    }

}
