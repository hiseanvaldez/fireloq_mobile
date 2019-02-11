package com.hiseanvaldez.fireloq;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Fragment_Logs extends Fragment {
    private Activity_Main main;
    private View view;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;

    public Fragment_Logs() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_logs, container, false);
        main = (Activity_Main) getActivity();
        mAuth = main.getMAuth();
        mDatabase = FirebaseFirestore.getInstance();

        return view;
    }
}
