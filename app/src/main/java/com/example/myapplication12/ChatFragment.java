package com.example.myapplication12;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ChatFragment extends AppCompatActivity {
    Button button;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_chat);

        button=findViewById(R.id.s1);
        button.setOnClickListener(v->opensearchuser());

    }
    private void opensearchuser(){
        Intent intent = new Intent(ChatFragment.this, SearchUser.class);
        startActivity(intent);
    }
}
