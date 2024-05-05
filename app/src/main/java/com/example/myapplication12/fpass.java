package com.example.myapplication12;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication12.utilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;

public class fpass extends AppCompatActivity {
    private Button fbtn;
    private EditText email;
    private EditText new_pass;
    private FirebaseAuth mAuth;
    private String n_password;
    private String n_email;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fpass);

        email = findViewById(R.id.femail);
        new_pass = findViewById(R.id.new_f_pass);
        mAuth = FirebaseAuth.getInstance();
        preferenceManager = new PreferenceManager(getApplicationContext());
        fbtn = findViewById(R.id.fbutton1);

        fbtn.setOnClickListener((v) -> {
            n_email = email.getText().toString();
            n_password = new_pass.getText().toString();
            Log.d("DebugTag", "Email: " + n_email);
            Log.d("DebugTag", "New Password: " + n_password);
            update();
        });
    }

    private void update() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, Object> data = new HashMap<>();
        data.put("password", n_password);

        db.collection("Login_Details").document(n_email).update(data)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getApplicationContext(), "User data updated!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(exception -> {
                    Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                });
//        Intent intent = new Intent(fpass.this, main.class);
//        startActivity(intent);
    }
}
