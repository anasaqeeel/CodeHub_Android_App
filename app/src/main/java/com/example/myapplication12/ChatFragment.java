package com.example.myapplication12;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class ChatFragment extends Fragment {

    private Button button1;

    public ChatFragment() {
        // Default constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_chat, container, false);
        button1 = view.findViewById(R.id.s1);
        button1.setOnClickListener(v -> openSearchUser());
        return view;
    }

    private void openSearchUser() {
        Intent intent = new Intent(getActivity(), SearchUser.class);
        startActivity(intent);
    }
}



