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
import com.example.myapplication12.utilities.androidutil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SearchUserAdapters extends FirestoreRecyclerAdapter<UserModel, SearchUserAdapters.usermodelviewholder> {
    Context context;
    private FirestoreRecyclerOptions<UserModel> options;
    public  String hourly_rate_f;

    public SearchUserAdapters(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull usermodelviewholder holder, int position, @NonNull UserModel model) {
        FirebaseFirestore.getInstance().collection("Login_Details")
                .document(model.getEmail())
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        String encodedImage = document.getString("image");
                        String hourlyRate = document.getString("hourly rate ");  // Checking the exact field name

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
                    } else {
                        Log.e("FirestoreError", "Error fetching document: ", task.getException());
                        holder.hourly_rate.setText("Error fetching hourly rate.");
                    }
                });

        holder.usertext.setText(model.getEmail() != null ? model.getEmail() : "Unknown Email");
        holder.phonetext.setText(model.getSkills() != null ? model.getSkills() : "Skills not specified!");
        holder.ratingtext.setText(model.getRating() != null ? model.getRating() : "Rating not specified!");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, chat_activity.class);
            androidutil.passUserModelAsIntent(intent, model);
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
        TextView hourly_rate;
        TextView phonetext;
        TextView ratingtext;
        ImageView img;

        public usermodelviewholder(@NonNull View itemView) {
            super(itemView);
            usertext = itemView.findViewById(R.id.user_email);
            hourly_rate = itemView.findViewById(R.id.hourly_rate);
            phonetext = itemView.findViewById(R.id.user_num);
            ratingtext = itemView.findViewById(R.id.rating_text);
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
