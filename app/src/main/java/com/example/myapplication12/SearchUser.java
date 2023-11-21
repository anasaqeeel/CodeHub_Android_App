package com.example.myapplication12;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication12.adapters.SearchUserAdapters;
import com.example.myapplication12.models.UserModel;
import com.example.myapplication12.utilities.firebaeUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchUser extends AppCompatActivity {
    private SearchUserAdapters adapters;
    private RecyclerView recyclerView;
    private EditText searchField;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        recyclerView = findViewById(R.id.search_user_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchField = findViewById(R.id.search_field);
        searchButton = findViewById(R.id.search_button);

        // Initialize with no users shown
        showUser("");

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performSearch();
            }
        });
    }



    private void performSearch() {
        String searchText = searchField.getText().toString().trim();
        // Add a log statement to check the search text
        Log.d("SearchUser", "Searching for: " + searchText);
        showUser(searchText);
    }
    private void showUser(String searchEmail) {
        Query query;
        if (searchEmail.isEmpty()) {
            // Start with an empty query if no search text is provided
            // Note that Firestore doesn't support truly empty queries, so we use an impossible condition
            query = firebaeUtil.allUserCollectionReference().whereEqualTo("email", "no_user_should_have_this_email_123");
        } else {
            // Query that searches for a specific email address
            query = firebaeUtil.allUserCollectionReference().whereEqualTo("email", searchEmail);
        }

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();

        if (adapters != null) {
            adapters.stopListening();
        }
        adapters = new SearchUserAdapters(options, this);
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
            adapters.startListening();
        }
    }
}
