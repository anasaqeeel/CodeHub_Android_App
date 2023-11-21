package com.example.myapplication12;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication12.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity2 extends AppCompatActivity {

    private final int gallery_req_code=1000;
    private EditText emailTV, passwordTV;
    private Button regBtn;
    private ProgressBar progressBar;
    private String encodedImage;
    private FirebaseAuth mAuth;
    ImageView img;
    private PreferenceManager preferenceManager;
    @Override

    protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main2);

            mAuth = FirebaseAuth.getInstance();

        preferenceManager=new PreferenceManager(getApplicationContext());

            initializeUI();
        img=findViewById(R.id.imageView);
        Button imgButtom=findViewById(R.id.buttonimg);
        imgButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Igallery = new Intent(Intent.ACTION_PICK);
                Igallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Igallery, gallery_req_code);
            }
        });
        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser();
            }

            private void registerNewUser() {
                progressBar.setVisibility(View.VISIBLE);

                String email, password;
                email = emailTV.getText().toString();
                password = passwordTV.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
                    return;
                }
                if (password.length() < 8) {
                    Toast.makeText(getApplicationContext(), "Password is too short! Minimum 8 characters required", Toast.LENGTH_LONG).show();
                    return;
                }
                if (img.getDrawable() == null) {
                    Toast.makeText(getApplicationContext(), "Please select a profile picture...", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Please enter a password!", Toast.LENGTH_LONG).show();
                    return;
                }

                // Get the selected image from the ImageView and encode it
                img.setDrawingCacheEnabled(true);
                img.buildDrawingCache();
                Bitmap bitmap = img.getDrawingCache();
                String encodedImage = encodedImage(bitmap);

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // You can upload the encoded image to Firestore here
                                    preferenceManager.putString("image",encodedImage);
                                    //uploadImageToFirestore(email, encodedImage.getBytes());
                                    Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);

                                    Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Registration failed! Check your internet connection and try again.", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }


        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
      super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == gallery_req_code) {
                img.setImageURI(data.getData());
                // Get the selected image as a Bitmap
                Bitmap selectedImage = null;
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                // Encode the selected image
                String encodedImage = encodedImage(selectedImage);
                // Pass the encoded image to the upload function
                //uploadImageToFirestore(emailTV.getText().toString(), encodedImage.getBytes());
            }
        }
    }

    private String encodedImage(Bitmap bitmap){
        int width=150;
        int height=bitmap.getHeight()*width/bitmap.getWidth();
        Bitmap previewBitmap=Bitmap.createScaledBitmap(bitmap,width,height,false);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);

        //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //Bitmap imageBitmap = ((BitmapDrawable) img.getDrawable()).getBitmap();
        //imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
    }

    private void initializeUI() {
        emailTV = findViewById(R.id.editTextText);
        passwordTV = findViewById(R.id.editTextTextPassword5);
        regBtn = findViewById(R.id.button2);
        progressBar = findViewById(R.id.progressBar);
    }
}