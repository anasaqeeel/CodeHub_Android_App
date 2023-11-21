package com.example.myapplication12;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication12.utilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public String show1,show2;
    public TextView textViewObj;
    private EditText emailTV, passwordTV;
    private Button loginBtn;
    private Button regbtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    public String token;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        initializeUI();
        preferenceManager = new PreferenceManager(getApplicationContext());


        //for testind data insertion to firestorm db
        //loginBtn.setOnClickListener(v -> addDataToFirestore());

        loginBtn.setOnClickListener(v -> loginUserAccount());
        regbtn=findViewById(R.id.buttonn);
        regbtn.setOnClickListener(v->openactivity3());
    }
    private void loginUserAccount() {
        progressBar.setVisibility(View.VISIBLE);

        String email, password;
        email = emailTV.getText().toString();
        password = passwordTV.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter email...", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter password!", Toast.LENGTH_LONG).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Firebase authentication successful
                        Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);

                        // Set show1 after successful login
                        preferenceManager.putString("user_email", email);

                        login_to_db(email, password);
                        // FirebaseMessaging.getInstance().getToken();

                        Intent intent = new Intent(MainActivity.this, main.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Login failed! Wrong credentials or check your internet connection", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }


    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }
    private void gettoken() {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                token = task.getResult();
                currentUserDetails().update("fcmToken",token);

            }
            else{
                Toast.makeText(getApplicationContext(),"failed!", Toast.LENGTH_LONG).show();
            }

        });
    }

    private void login_to_db(String email1, String password1) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        HashMap<String, Object> data = new HashMap<>();
        data.put("email", email1);
        data.put("password", password1);
        // Add user ID to the data map
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        data.put("userId", userId);
        String documentId = email1;

        db.collection("Login_Details")
                .document(documentId)
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "User data inserted!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openactivity3() {
        Intent intent=new Intent(this,MainActivity2.class);
        startActivity(intent);
    }
    private void initializeUI() {
        emailTV = findViewById(R.id.editTextText);
        passwordTV = findViewById(R.id.editTextTextPassword5);
        loginBtn = findViewById(R.id.button1);
        progressBar = findViewById(R.id.progressBar2);
    }
}