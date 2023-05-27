package com.hiseanvaldez.fireloq;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

class Firestore_WriteLog {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private String action;

    Firestore_WriteLog(FirebaseAuth mAuth, String action) {
        this.mAuth = mAuth;
        this.action = action;
        mDatabase = FirebaseFirestore.getInstance();

        writeLog();
    }

    private void writeLog(){
        Map<String, Object> log = new HashMap<>();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        Date now = new Date();
        log.put("datetime", format.format(now));
        log.put("user_id", Objects.requireNonNull(mAuth.getUid()));
        log.put("status", true);
        log.put("action", action);

        mDatabase.collection("mobile_logs")
                .add(log)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}
