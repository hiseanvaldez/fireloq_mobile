package com.hiseanvaldez.fireloq;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Fragment_Profile extends Fragment implements View.OnClickListener {
    Activity_Main main;
    FirebaseAuth mAuth;
    TextView status, details;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        main = (Activity_Main)getActivity();

        mAuth = main.getMAuth();
        status = view.findViewById(R.id.tv_status);
        details = view.findViewById(R.id.tv_details);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            startActivity(new Intent(getActivity(), Activity_Login.class));
            main.finish();
        }
        else {
            updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            status.setText(getString(R.string.emailpassword_status_fmt,user.getEmail(), user.isEmailVerified()));
            details.setText(getString(R.string.firebase_status_fmt, user.getUid()));
        }
        else {
            Toast.makeText(getContext(), "Signing out...", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), Activity_Login.class));
            main.finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        }
    }
}
