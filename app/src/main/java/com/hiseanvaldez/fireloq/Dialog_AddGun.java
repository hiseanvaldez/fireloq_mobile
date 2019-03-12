package com.hiseanvaldez.fireloq;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static android.support.constraint.Constraints.TAG;

public class Dialog_AddGun extends DialogFragment {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;

    private EditText model, make, regNo, serNo;
    private TextView regDate, expDate;

    private DatePickerDialog.OnDateSetListener regDateListener, expDateListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_addgun, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseFirestore.getInstance();

        model = view.findViewById(R.id.tx_model);
        make = view.findViewById(R.id.tx_make);
        regNo = view.findViewById(R.id.tx_regNumber);
        serNo = view.findViewById(R.id.tx_serial);

        regDate = view.findViewById(R.id.tx_regDate);
        regDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(Objects.requireNonNull(getContext()), android.R.style.Theme_Material_Light_Dialog, regDateListener, year, month, day);
                dialog.show();
            }
        });
        regDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                regDate.setText(date);
            }
        };
        expDate = view.findViewById(R.id.tx_expDate);
        expDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(Objects.requireNonNull(getContext()), android.R.style.Theme_Material_Light_Dialog, expDateListener, year, month, day);
                dialog.show();
            }
        });
        expDateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                expDate.setText(date);
            }
        };

        Button add = view.findViewById(R.id.bt_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateForm()){
                    addGun();
                    getDialog().dismiss();
                }
            }
        });

        return view;
    }

    private void addGun() {
        Map<String, Object> gun_request = new HashMap<>();
        gun_request.put("datetime", new Timestamp(new Date()));
        gun_request.put("status", "pending");
        gun_request.put("request_type", "gun");

        gun_request.put("user_id", Objects.requireNonNull(mAuth.getUid()));
        gun_request.put("manufacturer", make.getText().toString());
        gun_request.put("model", model.getText().toString());
        gun_request.put("serial_number", serNo.getText().toString());
        gun_request.put("registration_number", regNo.getText().toString());
        try {
            gun_request.put("registration_date", new Timestamp(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(regDate.getText().toString())));
            gun_request.put("expiry_date", new Timestamp(new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(expDate.getText().toString())));
        } catch (Exception e) {
            Log.e(TAG, "addGun: Parse error.", e);
        }
        mDatabase.collection("guns")
                .add(gun_request)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        new Firestore_WriteLog(mAuth, "Added Gun");
                        dismiss();
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

        if (TextUtils.isEmpty(make.getText().toString())) {
            make.setError("Required.");
            valid = false;
        } else {
            make.setError(null);
        }

        if (TextUtils.isEmpty(model.getText().toString())) {
            model.setError("Required.");
            valid = false;
        } else {
            model.setError(null);
        }

        if (TextUtils.isEmpty(serNo.getText().toString())) {
            serNo.setError("Required.");
            valid = false;
        } else {
            serNo.setError(null);
        }

        if (TextUtils.isEmpty(regNo.getText().toString())) {
            regNo.setError("Required.");
            valid = false;
        } else {
            regNo.setError(null);
        }

        if (TextUtils.isEmpty(expDate.getText().toString())) {
            expDate.setError("Required.");
            valid = false;
        } else {
            expDate.setError(null);
        }

        return valid;
    }

    private DialogInterface.OnDismissListener onDismissListener;
    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialog);
        }
    }
}
