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
import com.example.myapplication12.models.UserModel;
import com.example.myapplication12.submit_feedback;
import com.example.myapplication12.utilities.androidutil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
// feedback adapter

public class feedbackAdapter extends FirestoreRecyclerAdapter<UserModel,feedbackAdapter.usermodelviewholder> {
    Context context;
    private FirestoreRecyclerOptions<UserModel> options;

    public feedbackAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options,Context context) {
        super(options);
        this.context=context;
    }
    @Override
    protected void onBindViewHolder(@NonNull usermodelviewholder holder, int position, @NonNull UserModel model) {
        FirebaseFirestore.getInstance().collection("Login_Details")
                .document(model.getEmail())
                .get().addOnCompleteListener(innerTask -> {
                    if (innerTask.isSuccessful() && innerTask.getResult() != null) {

                        DocumentSnapshot document = innerTask.getResult();
                        String hourlyRate = document.getString("hourly rate ");

                        String encodedImage = innerTask.getResult().getString("image");
                        if (encodedImage != null && !encodedImage.isEmpty()) {
                            Bitmap decodedBitmap = decodeImage(encodedImage);
                            Bitmap circularBitmap = getCircularBitmap(decodedBitmap);
                            holder.img.setImageBitmap(circularBitmap);
                        }
                        if (hourlyRate != null) {
                            holder.hourly_rate.setText("Hourly Rate: $" + hourlyRate);
                        } else {
                            holder.hourly_rate.setText("Hourly Rate not added!");
                        }

                    }
                    else{
                        Log.e("FirestoreError", "Error fetching document: ", innerTask.getException());
                        holder.hourly_rate.setText("Error fetching hourly rate.");
                    }

                });
        holder.usertext.setText(model.getEmail() != null ? model.getEmail() : "Unknown Email");
        holder.phonetext.setText(model.getSkills()!=null? model.getSkills(): "skills not specified!");
        holder.itemView.setOnClickListener(v->{
            Intent intent=new Intent(context, submit_feedback.class);
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
        TextView hourly_rate;

        ImageView img;


        public usermodelviewholder(@NonNull View itemView) {
            super(itemView);
            usertext = itemView.findViewById(R.id.user_email);
            phonetext = itemView.findViewById(R.id.user_num);
            img = itemView.findViewById(R.id.profile_img);
            hourly_rate = itemView.findViewById(R.id.hourly_rate);


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
