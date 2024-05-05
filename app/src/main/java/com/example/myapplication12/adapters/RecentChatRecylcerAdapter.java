package com.example.myapplication12.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication12.R;
import com.example.myapplication12.chat_activity;
import com.example.myapplication12.models.UserModel;
import com.example.myapplication12.models.chatRoomModel;
import com.example.myapplication12.utilities.PreferenceManager;
import com.example.myapplication12.utilities.androidutil;
import com.example.myapplication12.utilities.firebaeUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.List;

public class RecentChatRecylcerAdapter extends FirestoreRecyclerAdapter<chatRoomModel, RecentChatRecylcerAdapter.ChatRoomModelViewHolder> {
    Context context;
    UserModel otherUser;
    private ImageView chatimage;
    PreferenceManager preferenceManager;
    private FirestoreRecyclerOptions<chatRoomModel> options;

    public RecentChatRecylcerAdapter(@NonNull FirestoreRecyclerOptions<chatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull chatRoomModel model) {
        String otherUserId;
        // ... your existing code to set otherUserId ...
        // Log the user IDs from the userIds array
        Query query;

        Log.d("RecentChatRecycler", "User IDs in chat room: " + model.getUserIds());

            int check = firebaeUtil.getOtherUserFromChatroom(model.getUserIds());
            //current user is at 0 index (dosry ki id chahehy at 1 index  )
            List<String> list = model.getUserIds();
            if (check == 1) {
                Log.d("1","1");
                otherUserId = list.get(1);
            } else {
                Log.d("p",check+"");
                otherUserId = list.get(0);
            }
            Log.d("1st:", otherUserId);

        // Assuming you have the otherUserId, proceed with Firestore query to get the email
        FirebaseFirestore.getInstance().collection("Login_Details").whereEqualTo("userId", otherUserId)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        // Assume only one user will be returned for the ID
                        UserModel userModel = task.getResult().toObjects(UserModel.class).get(0);
                        // The email address is the document ID based on your Firestore structure
                        String email = userModel.getEmail();
                        Log.d("laiba",email);

                        // Use the email to fetch the document with that ID
                        FirebaseFirestore.getInstance().collection("Login_Details").document(email)
                                .get().addOnCompleteListener(innerTask -> {
                                    if (innerTask.isSuccessful()) {
                                        UserModel fetchedUser = innerTask.getResult().toObject(UserModel.class);
                                        if (fetchedUser != null) {

                                            String encodedImage = innerTask.getResult().getString("image");
                                            if (encodedImage != null && !encodedImage.isEmpty()) {
                                                Bitmap decodedBitmap = decodeImage(encodedImage);
                                                Bitmap circularBitmap = getCircularBitmap(decodedBitmap);
                                                holder.img.setImageBitmap(circularBitmap);
                                            }

                                            // Now you have the fetched user model, update your holder views
                                            holder.usertext.setText(fetchedUser.getEmail());
                                            boolean isme=model.getLastMessageSenderId().equals(firebaeUtil.currentUserId());
                                            if(isme){
                                                holder.LastMessageText.setText("YOU :"+model.getLastMessage());
                                            }else {
                                                holder.LastMessageText.setText(model.getLastMessage());
                                            }
                                            holder.LastMessageTime.setText(firebaeUtil.timestampToString(model.getLastMessageTimestamp()));
                                            holder.itemView.setOnClickListener(v->{
                                                Intent intent=new Intent(context, chat_activity.class);
                                                androidutil.passUserModelAsIntent(intent,fetchedUser);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                context.startActivity(intent);
                                            });
                                        }

                                        else  {
                                            Log.d("RecentChatRecycler", "UserModel object is null after conversion.");
                                        }
                                    } else {
                                        Log.d("Error", "Failed to fetch user by document ID");
                                    }
                                });
                    } else {
                        // Handle the case where the user with the given userId does not exist or there was an error in the query
                        Log.d("Firestore", "Error getting documents: ", task.getException());
                    }
                });

    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatRoomModelViewHolder(view);
    }

    class ChatRoomModelViewHolder extends RecyclerView.ViewHolder {
        TextView usertext;
        TextView LastMessageText;
        TextView LastMessageTime;
        ImageView img;

        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            usertext = itemView.findViewById(R.id.user_email1);
            LastMessageText = itemView.findViewById(R.id.last_chat_text);
            LastMessageTime = itemView.findViewById(R.id.last_chat_time_text);
            img = itemView.findViewById(R.id.profile_img);
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
