package com.example.myapplication12;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication12.models.UserModel;
import com.example.myapplication12.utilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public String show1,show2,newimage;
    public TextView textViewObj;
    private EditText emailTV, passwordTV;
    private Button loginBtn;
    private Button regbtn;
    private ProgressBar progressBar;

    UserModel currentUserModel;

    private FirebaseAuth mAuth;
    public String skillbefore, email, password, Hrate;
    public String token;
    private Button fbtn;
    private PreferenceManager preferenceManager;
    HashMap<String, Object> data = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        initializeUI();
        email = emailTV.getText().toString();

        preferenceManager = new PreferenceManager(getApplicationContext());

//        skillbefore=preferenceManager.getString("skills");

        //for testind data  
        //loginBtn.setOnClickListener(v -> addDataToFirestore());

        loginBtn.setOnClickListener(v -> loginUserAccount());
        fbtn=findViewById(R.id.fpass);
        fbtn.setOnClickListener((v)->{
           Intent intent = new Intent(MainActivity.this, fpass.class);
            startActivity(intent);
        });
        regbtn=findViewById(R.id.buttonn);
        regbtn.setOnClickListener(v->openactivity3());

        

        
    }
    private void loginUserAccount() {
        progressBar.setVisibility(View.VISIBLE);


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
//                        Query query =firebaeUtil.allUserCollectionReference().whereEqualTo("email",email);
                        // Set show1 after successful login
                        preferenceManager.putString("user_email", email);
//                        preferenceManager.putString("skills",);

                        login_to_db(email, password);
                        FirebaseMessaging.getInstance().getToken();

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


    public static DocumentReference currentUserDetails(String email2){
        return FirebaseFirestore.getInstance().collection("Login_Details").document(email2);
    }
    private void gettoken(String email2) {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                token = task.getResult();
                Log.d("ye ha token ",token);
                currentUserDetails(email2).update("fcmToken",token);

            }
            else{
                Toast.makeText(getApplicationContext(),"failed!", Toast.LENGTH_LONG).show();
            }

        });
    }

    private void login_to_db(String email1, String password1) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        //login krty wy sab ki skills ko null kr rha ha

       // String SKills=preferenceManager.getString("skills");
        // FirebaseFirestore db = FirebaseFirestore.getInstance();

        Query query;
        Log.d("email ha",email1);
//        Log.d("yar",preferenceManager.getString())
        db.collection("Skills").document(email1).get()
                .addOnCompleteListener(task->{
        skillbefore =task.getResult().get("skills").toString();
        newimage=task.getResult().get("image").toString();

                    Object hourlyRateObj = task.getResult().get("hourly rate "); // Safely retrieve the object
                    String Hrate = (hourlyRateObj != null) ? hourlyRateObj.toString() : "0"; // Check for null and use "0" if null


                    Log.d("anas",skillbefore);
                    data.put("email", email1);
                    data.put("password", password1);
                    Log.d("sana",skillbefore);

                    data.put("skills",skillbefore);
                    data.put("image",newimage);
//                    data.put("fcmToken","");
                    // Add user ID to the data maap
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    data.put("userId", userId);
                    String documentId = email1;

                    db.collection("Login_Details")
                            .document(documentId)
                            .update(data)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getApplicationContext(), "User data inserted!", Toast.LENGTH_SHORT).show();
                                gettoken(email1);
                            })
                            .addOnFailureListener(exception -> {
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                            });
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