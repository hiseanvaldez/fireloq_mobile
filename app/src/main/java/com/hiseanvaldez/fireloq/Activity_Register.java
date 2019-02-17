package com.hiseanvaldez.fireloq;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class Activity_Register extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Activity_Register";
    private FirebaseAuth mAuth;
    private EditText email, password, fullName;
    private TextView dateOfBirth;
    private Spinner gender;

    DatePickerDialog.OnDateSetListener onDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViewById(R.id.bt_login).setOnClickListener(this);
        findViewById(R.id.bt_register).setOnClickListener(this);
        email = findViewById(R.id.tx_email);
        password = findViewById(R.id.tx_password);
        fullName = findViewById(R.id.tx_fullname);
        dateOfBirth = findViewById(R.id.tx_dateOfBirth);
        dateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(Activity_Register.this, android.R.style.Theme_Material_Light_Dialog, onDateSetListener, year, month, day);
                dialog.show();
            }
        });
        gender = findViewById(R.id.sp_gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);

        onDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = (month + 1) + " / " + dayOfMonth + " / " + year;
                dateOfBirth.setText(date);
            }
        };



        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            startActivity(new Intent(Activity_Register.this, Activity_Main.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.bt_login:
                startActivity(new Intent(Activity_Register.this, Activity_Login.class));
                finish();
                break;
            case R.id.bt_register:
                createAccount(email.getText().toString(), password.getText().toString());
                break;
        }
    }

    private void createAccount(String email, String password){
        if (!validateForm()) {
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:success");
                    Toast.makeText(getApplicationContext(), "Account Creation Success.", Toast.LENGTH_LONG).show();
                    new Firestore_WriteLog(mAuth, "Created Account");
                    startActivity(new Intent(Activity_Register.this, Activity_Main.class));
                    finish();
                }
                else {
                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(getApplicationContext(), "Account Creation failed.", Toast.LENGTH_LONG).show();
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
}
