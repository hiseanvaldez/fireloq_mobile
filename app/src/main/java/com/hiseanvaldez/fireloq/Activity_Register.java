package com.hiseanvaldez.fireloq;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Activity_Register extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Activity_Register";
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private EditText email, password, fullName, street, city, province, mobile, landline, license;
    private EditText dateOfBirth, expiry;
    private Spinner gender;

    private DatePickerDialog.OnDateSetListener dobListener, expListener;

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
                DatePickerDialog dialog = new DatePickerDialog(Activity_Register.this, android.R.style.Theme_Material_Light_Dialog, dobListener, year, month, day);
                dialog.show();
            }
        });
        dobListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                dateOfBirth.setText(date);
            }
        };

        gender = findViewById(R.id.sp_gender);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);

        street = findViewById(R.id.tx_street);
        city = findViewById(R.id.tx_city);
        province = findViewById(R.id.tx_province);
        mobile = findViewById(R.id.tx_mobile);
        landline = findViewById(R.id.tx_landline);
        license = findViewById(R.id.tx_licenseNumber);

        expiry = findViewById(R.id.tx_expDate);
        expiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(Activity_Register.this, android.R.style.Theme_Material_Light_Dialog, expListener, year, month, day);
                dialog.show();
            }
        });
        expListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                expiry.setText(date);
            }
        };

        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(Activity_Register.this, Activity_Main.class));
            finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                startActivity(new Intent(Activity_Register.this, Activity_Login.class));
                finish();
                break;
            case R.id.bt_register:
                if (validateForm()) {
                    requestAccount();
                }
                break;
        }
    }

    private void requestAccount() {
        Map<String, Object> user_request = new HashMap<>();
        user_request.put("datetime", new Timestamp(new Date()));
        user_request.put("status", "pending");
        user_request.put("request_type", "user");

        user_request.put("email", email.getText().toString());
        user_request.put("password", password.getText().toString());

        user_request.put("gender", gender.getSelectedItem().toString());
        user_request.put("address", street.getText().toString() + " " + city.getText().toString() + " " + province.getText().toString());
        user_request.put("mobile_number", mobile.getText().toString());
        user_request.put("landline_number", landline.getText().toString());
        user_request.put("license_number", license.getText().toString());

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try{
            Date date = format.parse(dateOfBirth.getText().toString());
            user_request.put("bday", new Timestamp(date));
            date = format.parse(expiry.getText().toString());
            user_request.put("license_expiry", new Timestamp(date));

        }
        catch (Exception e){
            Log.e(TAG, "requestAccount: Error Parsing Date", e);
        }

        String[] name = fullName.getText().toString().split(" ");
        user_request.put("first_name", name[0]);
        if (name.length == 2) {
            user_request.put("last_name", name[1]);
        } else if (name.length == 3) {
            user_request.put("middle_name", name[1]);
            user_request.put("last_name", name[2]);
        }

        mDatabase.collection("users")
                .add(user_request)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getApplicationContext(), "Request for new account apporval has been sent.", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Activity_Register.this, Activity_Login.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    private boolean validateForm() {
        boolean valid = true;
        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError("Required.");
            valid = false;
        } else {
            email.setError(null);
        }

        if (TextUtils.isEmpty(password.getText().toString())) {
            password.setError("Required.");
            valid = false;
        } else {
            this.password.setError(null);
        }

        if (TextUtils.isEmpty(street.getText().toString())) {
            street.setError("Required.");
            valid = false;
        } else {
            street.setError(null);
        }
        if (TextUtils.isEmpty(city.getText().toString())) {
            city.setError("Required.");
            valid = false;
        } else {
            city.setError(null);
        }
        if (TextUtils.isEmpty(province.getText().toString())) {
            province.setError("Required.");
            valid = false;
        } else {
            province.setError(null);
        }
        if (TextUtils.isEmpty(fullName.getText().toString())) {
            fullName.setError("Required.");
            valid = false;
        } else {
            fullName.setError(null);
        }
        if (TextUtils.isEmpty(license.getText().toString())) {
            license.setError("Required.");
            valid = false;
        } else {
            license.setError(null);
        }

        if (TextUtils.isEmpty(mobile.getText().toString())) {
            mobile.setError("Required.");
            valid = false;
        } else {
            mobile.setError(null);
        }

        if (TextUtils.isEmpty(dateOfBirth.getText().toString())) {
            dateOfBirth.setError("Required.");
            valid = false;
        } else {
            dateOfBirth.setError(null);
        }
        if (TextUtils.isEmpty(expiry.getText().toString())) {
            expiry.setError("Required.");
            valid = false;
        } else {
            expiry.setError(null);
        }

        return valid;
    }
}
