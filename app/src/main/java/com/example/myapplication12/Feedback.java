package com.example.myapplication12;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication12.adapters.feedbackAdapter;
import com.example.myapplication12.models.UserModel;
import com.example.myapplication12.utilities.PreferenceManager;
import com.example.myapplication12.utilities.firebaeUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class Feedback extends AppCompatActivity {
    private feedbackAdapter adapters;
    private PreferenceManager preferenceManager;
    private RecyclerView recyclerView;

    private EditText searchField;
    private Button searchButton;
    String email,skill1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferenceManager = new PreferenceManager(getApplicationContext());

        //iski base py searching
        email = preferenceManager.getString("user_email");
        //skill1=preferenceManager.getString("skills");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        recyclerView = findViewById(R.id.search_user_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //recyclerView1.findViewById(R.id.recyler_view_new);
        //setupRecyclerViewNew();

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

    void showUser(String searchskill) {
        Query query;
        if (searchskill.isEmpty()) {
            // Start with an empty query if no search text is provided
            // Note that Firestore doesn't support truly empty queries, so we use an impossible condition
            query = firebaeUtil.allUserCollectionReference().whereEqualTo("email", "no_user_should_have_this_skill_123");
        }
        if(searchskill.equals(email)) {
            showtoast("Cannot chat yourself!.");
            query = firebaeUtil.allUserCollectionReference().whereEqualTo("email", "no_user_should_have_this_email_123");
        }
        else {
            // Query that searches for a specific email address
            query = firebaeUtil.allUserCollectionReference().whereEqualTo("skills", searchskill);
        }
        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query, UserModel.class)
                .build();


        if (adapters != null) {
            adapters.stopListening();
        }
        adapters = new feedbackAdapter(options, this);
        recyclerView.setAdapter(adapters);
        adapters.startListening();
    }
    private void showtoast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
