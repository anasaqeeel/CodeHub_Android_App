package com.example.myapplication12.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication12.R;
import com.example.myapplication12.models.chatMessageModel;
import com.example.myapplication12.utilities.firebaeUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<chatMessageModel,ChatRecyclerAdapter.ChatModelViewHolder> {
    Context context;
    private FirestoreRecyclerOptions<chatMessageModel> options;
// recycler view is here 
    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<chatMessageModel> options,Context context) {
        super(options);
        this.context=context;
    }
    // chat recycler adapter this.context

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull chatMessageModel model) {
        if(model.getSenderId().equals(firebaeUtil.currentUserId())){
        holder.leftchatlayout.setVisibility(View.GONE);
        holder.rightchatlayout.setVisibility(View.VISIBLE);
        holder.rightchattextview.setText(model.getMessage());
        }
        else{
            holder.rightchatlayout.setVisibility(View.GONE);
            holder.leftchatlayout.setVisibility(View.VISIBLE);
            holder.leftchattextview.setText(model.getMessage());

        }
    }

    public void updateOptions(FirestoreRecyclerOptions<chatMessageModel> newOptions) {
        // Stop listening to changes in the fire store
        // stop listening
        stopListening();
        // Update the options
        this.options = newOptions; // Make sure the 'options' field is accessible here.
        // Start listening to changes in Firestore with the new query
        startListening();
    }


// chat model view holder


    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recylcer_row, parent, false); // Use the correct layout file here
        return new ChatModelViewHolder(view);
    }


    class ChatModelViewHolder extends RecyclerView.ViewHolder {

LinearLayout leftchatlayout, rightchatlayout;
TextView leftchattextview, rightchattextview;

        public ChatModelViewHolder(@NonNull View itemView) {

            super(itemView);
            leftchatlayout=itemView.findViewById(R.id.left_chat_layout);
            rightchatlayout=itemView.findViewById(R.id.right_chat_layout);
            leftchattextview=itemView.findViewById(R.id.left_chat_textview);
            rightchattextview=itemView.findViewById(R.id.right_chat_textview);

// find views by id
        }
    }
}
