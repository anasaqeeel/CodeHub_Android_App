package com.example.myapplication12.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication12.R;
import com.example.myapplication12.models.UserModel;
import com.example.myapplication12.utilities.androidutil;
import com.example.myapplication12.view_profile_action;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class view_profileAdapter extends FirestoreRecyclerAdapter<UserModel,view_profileAdapter.usermodelviewholder> {
    Context context;
    private FirestoreRecyclerOptions<UserModel> options;

    public view_profileAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options,Context context) {
        super(options);
        this.context=context;
    }

    @Override
    protected void onBindViewHolder(@NonNull usermodelviewholder holder, int position, @NonNull UserModel model) {
        holder.usertext.setText(model.getEmail() != null ? model.getEmail() : "Unknown Email");
        holder.phonetext.setText(model.getSkills()!=null? model.getSkills(): "skills not specified!");
        holder.ratingtext.setText(model.getRating()!=null? model.getRating(): "Rating not specified!");
        holder.itemView.setOnClickListener(v->{
            Intent intent=new Intent(context, view_profile_action.class);
            androidutil.passUserModelAsIntent(intent,model);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    public void updateOptions(FirestoreRecyclerOptions<UserModel> newOptions) {
        // Stop listening to changes in the Firestore
        stopListening();
        // Update the options
        this.options = newOptions; // Make sure the 'options' field is accessible here.
        // Start listening to changes in Firestore with the new query
        startListening();
    }





    @NonNull
    @Override
    public usermodelviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user, parent, false); // Use the correct layout file here
        return new usermodelviewholder(view);
    }


    class usermodelviewholder extends RecyclerView.ViewHolder {
        TextView usertext;
        TextView phonetext;
        TextView ratingtext;
        ImageView img;


        public usermodelviewholder(@NonNull View itemView) {

            super(itemView);
            usertext = itemView.findViewById(R.id.user_email);
            phonetext = itemView.findViewById(R.id.user_num);
            ratingtext=itemView.findViewById(R.id.rating_text);
            img = itemView.findViewById(R.id.profile_img);

        }
    }
}