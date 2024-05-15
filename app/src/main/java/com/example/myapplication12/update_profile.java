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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication12.models.UserModel;
import com.example.myapplication12.utilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class update_profile extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView profilePic;
    EditText expertise;
    EditText hrateUpdated;
    Button updateProfileBtn, changePictureBtn, forgotPasswordBtn;
    UserModel currentUserModel;
    private FirebaseAuth mAuth;
    String email2;
    String Expertise, newrate,coins;
    EditText coins1;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        mAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        email2 = preferenceManager.getString("user_email");

        hrateUpdated = findViewById(R.id.updatehrate);
        profilePic = findViewById(R.id.profile_image_view);
        coins1=findViewById(R.id.coins_view);
        expertise = findViewById(R.id.profile_expertise);
        updateProfileBtn = findViewById(R.id.profle_update_btn_new);
        changePictureBtn = findViewById(R.id.button_change_pictureB);
        forgotPasswordBtn = findViewById(R.id.button_forgot_password);

        getUserData();
        updateProfileBtn.setOnClickListener(v -> updateBtnClick());
        changePictureBtn.setOnClickListener(v -> selectImageFromGallery());
        forgotPasswordBtn.setOnClickListener(v -> sendPasswordResetEmail(email2));
    }

    void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    void updateBtnClick() {
        Expertise = expertise.getText().toString();
        newrate = hrateUpdated.getText().toString();
        currentUserModel.setHourly_Rate(newrate);
        currentUserModel.setSkills(Expertise);
        showtoast("updated!");
        updateToFirestore();
    }

    void getUserData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Login_Details")
                .document(email2).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        currentUserModel = task.getResult().toObject(UserModel.class);
                        if (currentUserModel != null) {
                            expertise.setText(currentUserModel.getSkills());

                            String encodedImage = task.getResult().getString("image");
                            if (encodedImage != null && !encodedImage.isEmpty()) {
                                Bitmap decodedBitmap = decodeImage(encodedImage);
                                Bitmap circularBitmap = getCircularBitmap(decodedBitmap);
                                profilePic.setImageBitmap(circularBitmap);
                            }
                            coins=task.getResult().getString("coins");
                            coins1.setText(coins);
                        }
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

    void updateToFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put("skills", Expertise);
        data.put("hourly rate ", newrate);

        String encodedImage = preferenceManager.getString("image");
        if (encodedImage != null && !encodedImage.isEmpty()) {
            data.put("image", encodedImage);
        }

        db.collection("Login_Details").document(email2).update(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "User data updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
        db.collection("Skills").document(email2).update(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "User data updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                Bitmap circularBitmap = getCircularBitmap(bitmap);
                profilePic.setImageBitmap(circularBitmap);
                uploadImageToFirestore(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirestore(Bitmap bitmap) {
        String encodedImage = encodedImage(bitmap);
        preferenceManager.putString("image", encodedImage);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put("image", encodedImage);
        db.collection("Login_Details")
                .document(email2).update(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Image updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
        db.collection("Skills")
                .document(email2).update(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "Image updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String encodedImage(Bitmap bitmap) {
        int width = 150;
        int height = bitmap.getHeight() * width / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void showtoast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void sendPasswordResetEmail(String email) {
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(update_profile.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(update_profile.this, "Error in sending password reset email.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
