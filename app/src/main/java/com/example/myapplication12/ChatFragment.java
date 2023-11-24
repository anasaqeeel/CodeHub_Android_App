package com.example.myapplication12;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication12.adapters.RecentChatRecylcerAdapter;
import com.example.myapplication12.models.chatRoomModel;
import com.example.myapplication12.utilities.firebaeUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ChatFragment extends AppCompatActivity {
    Button button;
    RecentChatRecylcerAdapter adapters;
    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_chat);


       button=findViewById(R.id.S1);
       button.setOnClickListener(v->opensearchuser());
       recyclerView=findViewById(R.id.recyler_view_new);
       showrecentuser();

    }
    private void opensearchuser(){
        Intent intent = new Intent(ChatFragment.this, SearchUser.class);
        startActivity(intent);
    }
    void showrecentuser() {
        Query query;

            // Query that searches for a specific email address
            query = firebaeUtil.allChatRoomCollectionref()
                    .whereArrayContains("userIds",firebaeUtil.currentUserId())
                    .orderBy("lastMessageTimestamp",Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<chatRoomModel> options = new FirestoreRecyclerOptions.Builder<chatRoomModel>()
                .setQuery(query, chatRoomModel.class)
                .build();

        adapters = new RecentChatRecylcerAdapter(options,getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapters);
        adapters.startListening();
    }



    @Override
    protected void onStart() {
        super.onStart();
        if (adapters != null) {
            adapters.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop the adapter listening when the activity is no longer visible
        if (adapters != null) {
            adapters.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapters != null) {
            adapters.notifyDataSetChanged();
        }
    }

}
