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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Activity_Login extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Activity_Login";
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;

    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        findViewById(R.id.bt_register).setOnClickListener(this);
        findViewById(R.id.bt_login).setOnClickListener(this);
        findViewById(R.id.bt_forgotPass).setOnClickListener(this);

        email = findViewById(R.id.tx_email);
        password = findViewById(R.id.tx_password);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(Activity_Login.this, Activity_Main.class));
            finish();
        }
    }

    private void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success");
                    new Firestore_WriteLog(mAuth, "Log In");
                    startActivity(new Intent(Activity_Login.this, Activity_Main.class));
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    new Firestore_WriteLog(mAuth, "Log In Attempt");
                    Toast.makeText(getApplicationContext(), "Authentication failed.", Toast.LENGTH_SHORT).show();
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

        String password_s = this.password.getText().toString();
        if (TextUtils.isEmpty(password_s)) {
            this.password.setError("Required.");
            valid = false;
        } else {
            this.password.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_login:
                signIn(email.getText().toString(), password.getText().toString());
                break;
            case R.id.bt_register:
                startActivity(new Intent(Activity_Login.this, Activity_Register.class));
                finish();
                break;
            case R.id.bt_forgotPass:
                startActivity(new Intent(Activity_Login.this, Activity_RetrievePassword.class));
                break;
        }
    }
}
