package com.example.myapplication12;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.ui.AppBarConfiguration;

import com.example.myapplication12.models.UserModel;
import com.example.myapplication12.utilities.PreferenceManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class update_profile extends AppCompatActivity {


    ImageView profilePic;
    EditText useremailInput;
    EditText passInput;
    UserModel currentUserModel;
    private FirebaseAuth mAuth;

    String newUseremail;
    Button updateProfileBtn;
    TextView logoutBtn;
    PreferenceManager preferenceManager;
    String email2;
   EditText expertise;
   String Expertise;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        preferenceManager = new PreferenceManager(getApplicationContext());
        email2 = preferenceManager.getString("user_email");
//        profilePic=findViewById(R.id.profile_image_view);
//        passInput=findViewById(R.id.profile_pass);
//        useremailInput=findViewById(R.id.profile_email);
        updateProfileBtn=findViewById(R.id.profle_update_btn_new);
//        logoutBtn=findViewById(R.id.logout_btn);
        expertise=findViewById(R.id.profile_expertise);

        mAuth = FirebaseAuth.getInstance();
        getUserData();
        updateProfileBtn.setOnClickListener(v->updateBtnClick());
    }

    void updateBtnClick() {
        Expertise=expertise.getText().toString();

        //newUseremail = useremailInput.getText().toString();
//        if (Expertise.isEmpty() ) {
//           expertise.setError("Username length should be at least 3 chars");
//            return;
//        }
//        else{

            currentUserModel.setSkills(Expertise);
            showtoast("updated !");
            updateToFirestore();
//        }

//        if (selectedImageUri != null) {
//            FirebaseUtil.getCurrentProfilePicStorageRef().putFile(selectedImageUri)
//                    .addOnCompleteListener(task -> {
//                        updateToFirestore();
//                    });
//        } else {
//            updateToFirestore();
//        }

    }
    void getUserData(){
//        setInProgress(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Login_Details")
                .document(email2).get().addOnCompleteListener(task -> {
                    currentUserModel=task.getResult().toObject(UserModel.class);
                    if(currentUserModel!=null) {
//                        useremailInput.setText(currentUserModel.getEmail());
//                        passInput.setText(currentUserModel.getPassword());
                        //expertin update krna
                        expertise.setText(currentUserModel.getSkills());
                    }
                    else{
                        Log.d("currentusermodel","null ha ");
                    }

                });

//        firebaeUtil.currentUserDetails().get().addOnCompleteListener(task -> {
//            setInProgress(false);
//            //currentUserModel;
//            String test = task.getResult().toString();
//            Log.d("raw",test);
//
//        });

//        firebaeUtil.getCurrentProfilePicStorageRef().getDownloadUrl()
//                .addOnCompleteListener(task -> {
//                    if(task.isSuccessful()){
//                        Uri uri  = task.getResult();
//                        AndroidUtil.setProfilePic(getContext(),uri,profilePic);
//                    }
//                });


    }

    void updateToFirestore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        db.collection("Login_Details")
//                .document(email2).set(currentUserModel)
//                .addOnCompleteListener(task -> {
////                    setInProgress(false);
//                    if(task.isSuccessful()){
//                        androidutil.showToast(getApplicationContext(),"Updated successfully");
//                    }else{
//                        androidutil.showToast(getApplicationContext(),"Updated failed");
//                    }
//                });
        HashMap<String, Object> data = new HashMap<>();
        data.put("email", email2);
        data.put("password",currentUserModel.getPassword());
        data.put("fcmToken",currentUserModel.getFcmToken());
        String newimage1=update_image(email2);

        //data.put("image",newimage1);
        //Log.d("perl",newimage1);
        // Add user ID to the data map
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        data.put("userId", userId);
        //inserting new to db
        //new skills
        data.put("skills",Expertise);
        //idhr Skills mn bhi update krna
        db.collection("Login_Details").document(email2).get()
                .addOnCompleteListener(task-> {
                    currentUserModel=task.getResult().toObject(UserModel.class);
//                    db.collection("Login_Details").document(email2).delete();
                    db.collection("Login_Details")
                            .document(email2).update(data)
                            .addOnSuccessListener(aVoid -> {
                                //updateAuthEmail(newUseremail);
                                Toast.makeText(getApplicationContext(), "User data inserted!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(exception -> {
                                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                            });
        }) ;
        FirebaseFirestore.getInstance().collection("Skills").document(email2).update("skills",Expertise);


    }
    String newimage;
    private String update_image(String email2){
        FirebaseFirestore.getInstance().collection("Skills").document(email2).get()
                .addOnCompleteListener(task-> {
                  newimage = task.getResult().get("image").toString();

                });
       return newimage;
    }

    private void updateAuthEmail(String newEmail) {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.updateEmail(newEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Only if the Auth email is updated successfully, delete the old document
                            FirebaseFirestore.getInstance().collection("Login_Details").document(email2).delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(update_profile.this, "Email updated in Auth and old document deleted", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
//                                        Log.e("UpdateEmailError", "Failed to update email", task.getException());
//                                        Toast.makeText(update_profile.this, "Failed to update email in Auth: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    });

                            // Update the email in PreferenceManager
                            preferenceManager.putString("user_email", newEmail);
                        } else {
                            Log.e("abhi ", "Failed to update email", task.getException());

                            Toast.makeText(update_profile.this, "Failed to update email in Auth", Toast.LENGTH_SHORT).show();
                            // Consider reverting the Firestore update if Auth update fails
                        }
                    });
        } else {
            // User is not signed in
            Toast.makeText(update_profile.this, "No authenticated user found", Toast.LENGTH_SHORT).show();
        }
    }
    private void showtoast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}