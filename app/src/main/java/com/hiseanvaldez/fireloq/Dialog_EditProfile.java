package com.hiseanvaldez.fireloq;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.support.constraint.Constraints.TAG;

public class Dialog_EditProfile extends DialogFragment {
    Button submit;
    TextView head;
    EditText expiry, licenseno;
    FirebaseFirestore mDatabase;
    String userDocID;

    private DatePickerDialog.OnDateSetListener dobListener, expListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_editprofile, container, false);
        head = view.findViewById(R.id.tv_editProfileHeading);

        mDatabase = FirebaseFirestore.getInstance();

        Bundle mArgs = getArguments();
        userDocID = mArgs.getString("userDocID");
        head.setText(userDocID);
        submit = view.findViewById(R.id.bt_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDoc();
                getDialog().dismiss();
            }
        });

        licenseno = view.findViewById(R.id.tx_eLicenseNumber);
        expiry = view.findViewById(R.id.tx_eExpDate);
        expiry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Material_Light_Dialog, expListener, year, month, day);
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

        return view;
    }

    public void updateDoc(){
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("license_number", licenseno.getText().toString());

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        try{
            Date date = format.parse(expiry.getText().toString());
            userMap.put("license_expiry", new Timestamp(date));
        }
        catch (Exception e){
            Log.e(TAG, "requestAccount: Error Parsing Date", e);
        }

        DocumentReference userRef = mDatabase.collection("users").document(userDocID);
        userRef.update(userMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
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
