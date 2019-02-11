package com.hiseanvaldez.fireloq;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class Firestore_WriteLog {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private String action;

    public Firestore_WriteLog(FirebaseAuth mAuth, String action) {
        this.mAuth = mAuth;
        this.action = action;
        mDatabase = FirebaseFirestore.getInstance();

        writeLog();
    }

    private void writeLog(){
        Map<String, Object> log = new HashMap<>();
        log.put("datetime", new Timestamp(new Date()));
        log.put("user_id", mAuth.getUid());
        log.put("status", true);
        log.put("log_type", "Mobile");
        log.put("action", action);

        mDatabase.collection("logs")
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

    public FirebaseAuth getmAuth() {
        return mAuth;
    }

    public String getAction() {
        return action;
    }
}
