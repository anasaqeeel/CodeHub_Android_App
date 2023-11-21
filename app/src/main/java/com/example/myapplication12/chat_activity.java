package com.example.myapplication12;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication12.adapters.ChatRecyclerAdapter;
import com.example.myapplication12.models.UserModel;
import com.example.myapplication12.models.chatMessageModel;
import com.example.myapplication12.models.chatRoomModel;
import com.example.myapplication12.utilities.androidutil;
import com.example.myapplication12.utilities.firebaeUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class chat_activity extends AppCompatActivity {
    UserModel otheruser;
    ChatRecyclerAdapter adapter;
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
        } else if(currentUserId==null) {
            // Handle the case where currentUserId or otherUserId is null
            Toast.makeText(this, "Error: current to get user information.", Toast.LENGTH_LONG).show();
            finish(); // Close the current activity and return to the previous one
        }
        else if(otherUserId==null) {
            // Handle the case where currentUserId or otherUserId is null
            Toast.makeText(this, "Error:other user unable to get user information.", Toast.LENGTH_LONG).show();
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
        sendmessagebtn.setOnClickListener(v -> {
            String message = messageinput.getText().toString().trim();
            if (message.isEmpty()) {
                return;
            }

            // Log the message to the console
            Log.d("MessageInput", "Message: " + message);

            // Send the message to the user
            sendmessagetouser(message);
        });
        getOrCreateChatroomModel();
        setupchatrecyclerview();
    }
    void setupchatrecyclerview(){
        Query query;
        query = firebaeUtil.getChatroomMessageRef(chatroomid).orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<chatMessageModel> options = new FirestoreRecyclerOptions.Builder<chatMessageModel>()
                .setQuery(query, chatMessageModel.class)
                .build();

        if (adapter != null) {
            adapter.stopListening();
        }
        adapter = new ChatRecyclerAdapter(options, this);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });


    }
    void sendmessagetouser(String message1){
        chatroommodel.setLastMessageTimestamp(Timestamp.now());
        chatroommodel.setLastMessageSenderId(firebaeUtil.currentUserId());
        firebaeUtil.getchatRoomRefrence(chatroomid).set(chatroommodel);

        chatMessageModel chatmessagemodel =new  chatMessageModel(message1,firebaeUtil.currentUserId(),Timestamp.now());
        firebaeUtil.getChatroomMessageRef(chatroomid).add(chatmessagemodel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            messageinput.setText("");
                        }
                    }
                });


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