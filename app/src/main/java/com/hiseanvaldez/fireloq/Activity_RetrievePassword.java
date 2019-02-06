package com.hiseanvaldez.fireloq;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Activity_RetrievePassword extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "Activity_RetPass";
    private FirebaseAuth mAuth;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retrieve_password);

        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.bt_getPassword).setOnClickListener(this);
        email = findViewById(R.id.tx_email);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(Activity_RetrievePassword.this, Activity_Main.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.bt_getPassword:
                retrievePassword(email.getText().toString());
                finish();
                break;
        }
    }

    private void retrievePassword(final String email_s) {
        if (!validateForm()) {
            return;
        }
        mAuth.sendPasswordResetEmail(email_s).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "Email sent.");
                    Toast.makeText(getApplicationContext(),"Password Reset has been sent to: " + email_s, Toast.LENGTH_LONG).show();
                    email.setText("");
                }
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email_s = email.getText().toString();
        if (TextUtils.isEmpty(email_s)) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }
        return valid;
    }
}
