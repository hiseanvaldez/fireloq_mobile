package com.hiseanvaldez.fireloq;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Fragment_Logs extends Fragment {
    private Activity_Main main;
    private View view;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private CollectionReference logReference;
    private Adapter_Logs adapter;

    public Fragment_Logs() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_logs, container, false);
        main = (Activity_Main)getActivity();
        mAuth = main.getMAuth();
        mDatabase = FirebaseFirestore.getInstance();

        setupRecyclerView();
        logReference = mDatabase.collection("logs");

        return view;
    }

    private void setupRecyclerView(){
        Query query = logReference;

        FirestoreRecyclerOptions<Model_Logs> options = new FirestoreRecyclerOptions.Builder<Model_Logs>()
                .setQuery(query, Model_Logs.class)
                .build();
        adapter = new Adapter_Logs(options);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerVIew_logs);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}
