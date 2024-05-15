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
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.example.myapplication12.utilities.PreferenceManager;
import com.example.myapplication12.utilities.androidutil;
import com.example.myapplication12.utilities.firebaeUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Arrays;

public class chat_activity extends AppCompatActivity {
    UserModel otheruser;
    ChatRecyclerAdapter adapter;
    private PreferenceManager preferenceManager;
    private ImageView ppp;

    chatRoomModel chatroommodel;
    String chatroomid;

    EditText messageinput;
    ImageButton sendmessagebtn;
    ImageButton backbtn;
    TextView otherusername;
    RecyclerView recyclerView;
    Button tick_btn;
String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        otheruser = androidutil.getUserModelFromIntent(getIntent());
        ppp=findViewById(R.id.profile_img);
        preferenceManager = new PreferenceManager(getApplicationContext());

        // Check if the currentUserId and otherUserId are not null
        String currentUserId = firebaeUtil.currentUserId();
        String otherUserId = (otheruser != null) ? otheruser.getUserId() : null;
        email = preferenceManager.getString("user_email");
        //email= firebaeUtil.currentUserId();
        Log.d("emaill",email);

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
        tick_btn=findViewById(R.id.tick);
        tick_btn.setOnClickListener((v)->{
            issue_resolved();
        });
        otherusername = findViewById(R.id.other_user_name);
        recyclerView = findViewById(R.id.chat_recycler_view);
        backbtn.setOnClickListener((v) -> {
            onBackPressed(); // Corrected to call onBackPressed method
        });
        otherusername.setText(otheruser.getEmail());
        FirebaseFirestore.getInstance().collection("Login_Details")
                .document(otheruser.getEmail())
                .get().addOnCompleteListener(innerTask -> {
                    String encodedImage = innerTask.getResult().getString("image");
                    if (encodedImage != null && !encodedImage.isEmpty()) {
                        Bitmap decodedBitmap = decodeImage(encodedImage);
                        Bitmap circularBitmap = getCircularBitmap(decodedBitmap);
                        ppp.setImageBitmap(circularBitmap);
                    }
                });

        sendmessagebtn.setOnClickListener(v -> {
            String message = messageinput.getText().toString().trim();
            if (message.isEmpty()) {
                return;
            }

            // Log the message to the console
            Log.d("MessageInput", "Message: " + message);

            // Send the message to the user
            sendmessagetouser(message,email);
        });
        getOrCreateChatroomModel();
        setupchatrecyclerview();
    }
    private void issue_resolved(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Login_Details")
                .document(email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Retrieve the 'coins' field as a String
                        String getCoins = task.getResult().getString("coins");

                        // Check if 'coins' is not null
                        if (getCoins != null) {
                            try {
                                // Convert 'coins' from String to Integer
                                int currentCoins = Integer.parseInt(getCoins);

                                // Subtract 10 coins
                                int updatedCoins = currentCoins - 10;

                                // Update the document with the new coin value
                                db.collection("Login_Details")
                                        .document(email)
                                        .update("coins", String.valueOf(updatedCoins))
                                        .addOnSuccessListener(aVoid -> System.out.println("Coins updated successfully!"))
                                        .addOnFailureListener(e -> System.err.println("Error updating coins: " + e.getMessage()));
                            } catch (NumberFormatException e) {
                                System.err.println("Error parsing coins: " + e.getMessage());
                            }
                        } else {
                            System.out.println("Coins field is missing or null.");
                        }
                    } else {
                        System.err.println("Error fetching document: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                    }
                });

        Intent intent = new Intent(chat_activity.this,Feedback.class);
        startActivity(intent);
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
    void sendmessagetouser(String message1,String email){
        chatroommodel.setLastMessageTimestamp(Timestamp.now());
        chatroommodel.setLastMessageSenderId(firebaeUtil.currentUserId());
        chatroommodel.setLastMessage(message1);
        chatroommodel.setChatemail(email);
        //Overwrites the document referred to by this DocumentReference. If the document does not yet exist, it will be created. If a document already exists, it will be overwritten.
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

    //inserting chatroom id doc to db
    void getOrCreateChatroomModel() {
        if (chatroomid != null) {
            firebaeUtil.getchatRoomRefrence(chatroomid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    chatroommodel = task.getResult().toObject(chatRoomModel.class);
                    if (chatroommodel == null) {
                        chatroommodel = new chatRoomModel(
                                chatroomid,
                                email,
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
}