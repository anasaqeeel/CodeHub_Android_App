package com.example.myapplication12.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication12.R;
import com.example.myapplication12.models.chatRoomModel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecentChatRecylcerAdapter extends FirestoreRecyclerAdapter<chatRoomModel,RecentChatRecylcerAdapter.chatRoomModelviewholder> {
    Context context;
    private FirestoreRecyclerOptions<chatRoomModel> options;

    public RecentChatRecylcerAdapter(@NonNull FirestoreRecyclerOptions<chatRoomModel> options,Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull chatRoomModelviewholder holder, int position, @NonNull chatRoomModel model) {

    }

    public void updateOptions(FirestoreRecyclerOptions<chatRoomModel> newOptions) {
        // Stop listening to changes in the Firestore
        stopListening();
        // Update the options
        this.options = newOptions; // Make sure the 'options' field is accessible here.
        // Start listening to changes in Firestore with the new query
        startListening();
    }

    @NonNull
    @Override
    public chatRoomModelviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false); // Use the correct layout file here
        return new chatRoomModelviewholder(view);
    }


    class chatRoomModelviewholder extends RecyclerView.ViewHolder {
        TextView usertext;
        TextView LastMessageText;
        TextView LastMessageTime;
        ImageView img;


        public chatRoomModelviewholder(@NonNull View itemView) {

            super(itemView);
            usertext = itemView.findViewById(R.id.user_name);
            LastMessageText = itemView.findViewById(R.id.last_chat_text);
            LastMessageTime = itemView.findViewById(R.id.last_chat_time_text);
            img = itemView.findViewById(R.id.profile_img);

        }
    }
}