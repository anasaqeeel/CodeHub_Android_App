package com.example.myapplication12;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication12.models.UserModel;
import com.example.myapplication12.models.chatRoomModel;
import com.example.myapplication12.utilities.androidutil;
import com.example.myapplication12.utilities.firebaeUtil;
import com.google.firebase.Timestamp;

import java.util.Arrays;

public class chat_activity extends AppCompatActivity {
    UserModel otheruser;
    chatRoomModel chatroommodel;
    String chatroomid;

    EditText messageinput;
    ImageButton sendmessagebtn;
    ImageButton backbtn;
    TextView otherusername;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        otheruser = androidutil.getUserModelFromIntent(getIntent());

        // Check if the currentUserId and otherUserId are not null
        String currentUserId = firebaeUtil.currentUserId();
        String otherUserId = (otheruser != null) ? otheruser.getUserId() : null;

        if (currentUserId != null && otherUserId != null) {
            chatroomid = firebaeUtil.getchatroomid(currentUserId, otherUserId);

            if (chatroomid != null) {
                Toast.makeText(this, "Chatroom ID: " + chatroomid, Toast.LENGTH_LONG).show();

                // Proceed with the rest of your onCreate logic...
                // Initialize views, getOrCreateChatroomModel(), etc.
            } else {
                // Handle the case where chatroomid is null
                Toast.makeText(this, "Error: chatroomid is null.", Toast.LENGTH_LONG).show();
                finish(); // Close the current activity and return to the previous one
            }
        } else {
            // Handle the case where currentUserId or otherUserId is null
            Toast.makeText(this, "Error: unable to get user information.", Toast.LENGTH_LONG).show();
            finish(); // Close the current activity and return to the previous one
        }

        // Initialize the views correctly
        messageinput = findViewById(R.id.chat_message_input);
        sendmessagebtn = findViewById(R.id.message_send_btn);
        backbtn = findViewById(R.id.back_btn);
        otherusername = findViewById(R.id.other_user_name);
        recyclerView = findViewById(R.id.chat_recycler_view);
        backbtn.setOnClickListener((v) -> {
            onBackPressed(); // Corrected to call onBackPressed method
        });
        otherusername.setText(otheruser.getEmail());
        sendmessagebtn.setOnClickListener(v ->{
            String message=messageinput.getText().toString().trim();
            if(message.isEmpty())
                return;
                sendmessagetouser(message);

        });
        getOrCreateChatroomModel();
    }
    void sendmessagetouser(String message1){

    }

    void getOrCreateChatroomModel() {
        if (chatroomid != null) {
            firebaeUtil.getchatRoomRefrence(chatroomid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    chatroommodel = task.getResult().toObject(chatRoomModel.class);
                    if (chatroommodel == null) {
                        chatroommodel = new chatRoomModel(
                                chatroomid,
                                Arrays.asList(firebaeUtil.currentUserId(), otheruser.getUserId()),
                                Timestamp.now(),
                                ""
                        );
                        firebaeUtil.getchatRoomRefrence(chatroomid).set(chatroommodel);
                    }
                }
            });
        } else {
            // Handle the case where chatroomid is null, for example, show an error toast message
            Toast.makeText(this, "Error: chatroomid is null.", Toast.LENGTH_LONG).show();
        }
    }


}