package com.example.myapplication12;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication12.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;


public class main extends AppCompatActivity {

    public static final String KEY_FCM_TOKEN = "fcmToken";
    String updatedimage;
    HashMap<String, Object> data = new HashMap<>();

    public static final String KEY_Collection_Users = "Login_Details";
    private PreferenceManager preferenceManager;
    private Button updatebutton;
    private TextView tt3;
    private Button feedbackbutton;
    private Button view_profileB;

    private Button btn;
    private Button btn1;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        // Initialize SharedPreferences
        preferenceManager = new PreferenceManager(getApplicationContext());
        //sharedPreferences = getSharedPreferences("YourPreferenceName", Context.MODE_PRIVATE);


        gettoken();
        feedbackbutton=findViewById(R.id.feedback);

        updatebutton=findViewById(R.id.update_profile_button1);
        view_profileB=findViewById(R.id.view_profile);
        btn = findViewById(R.id.lgtbtn);
        tt3=findViewById(R.id.textView3);
        tt3.setText(preferenceManager.getString("user_email"));
        btn1 =findViewById(R.id.chatbtn);
        view_profileB.setOnClickListener(v->openviewprofile());
        feedbackbutton.setOnClickListener(v->openfeedbackpage());
        updatebutton.setOnClickListener(v->openupdateprofile());
        btn.setOnClickListener(v -> logout());
        btn1.setOnClickListener(v->chatopen());
    }

    private void openviewprofile(){
        Intent intent=new Intent(main.this,view_profile.class);
        startActivity(intent);
    }
    private void openupdateprofile() {
        Intent intent=new Intent(main.this,update_profile.class);
        startActivity(intent);
    }

    private void openfeedbackpage(){
        Intent intent=new Intent(main.this,Feedback.class);
        startActivity(intent);
    }
    private void showtoast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void gettoken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }
    private void chatopen(){
        Intent intent = new Intent(main.this, ChatFragment.class);
        startActivity(intent);
        //startActivity(new Intent(getApplicationContext(),SearchUser.class));
    }

    private void logout() {
        showtoast("signing out...");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection(KEY_Collection_Users).
                document(preferenceManager.getString("user_email"));
        HashMap<String, Object> updates = new HashMap<>();
        updates.put(KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates).addOnSuccessListener(unused -> {
            preferenceManager.clear();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }).addOnFailureListener(e -> showtoast("unable to sign out"));
    }


    private void updateToken(String token) {
        if (token != null) {

            // Get the user's email from shared preferences
            String userEmail = preferenceManager.getString("user_email");
            String userImage = preferenceManager.getString("image");
            //adding image to skills collection
            //FirebaseFirestore.getInstance().collection("Skills").document(userEmail).update("image",userImage);
            if (!userImage.isEmpty()) {
                FirebaseFirestore DB = FirebaseFirestore.getInstance();
                DocumentReference documentReference = DB.collection(KEY_Collection_Users).document(userEmail);
//                documentReference.update("image", userImage)
//                        .addOnSuccessListener(unused -> showtoast("Image updated successfully!"))
//                        .addOnFailureListener(e -> showtoast("Unable to update image: " + e.getMessage()));
            } else {
                showtoast("User image not found in shared preferences.");
            }

            if (!userEmail.isEmpty()) {
                FirebaseFirestore DB = FirebaseFirestore.getInstance();
                DocumentReference documentReference = DB.collection(KEY_Collection_Users).document(userEmail);
                documentReference.update(KEY_FCM_TOKEN, token)
                        .addOnSuccessListener(unused -> showtoast("Token updated successfully!"))
                        .addOnFailureListener(e -> showtoast("Unable to update token: " + e.getMessage()));
            } else {
                showtoast("User email not found in shared preferences.");
            }

        } else {
            showtoast("FCM token is null or not available.");
        }

    }

}