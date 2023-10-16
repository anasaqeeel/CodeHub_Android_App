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

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    public TextView textViewObj;

    private EditText emailTV, passwordTV;
    private Button loginBtn;
    private Button regbtn;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        initializeUI();
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
                        Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);

                        Intent intent = new Intent(MainActivity.this, main.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Login failed! Wrong credentials or check your internet connection", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }
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