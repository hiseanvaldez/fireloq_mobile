package com.hiseanvaldez.fireloq;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class Fragment_Profile extends Fragment implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private TextView fullname, email, address, dateofbirth, mobileno, landlineno, licenseno, licenseexpiry;
    private ImageView edit;
    private String userDocID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mDatabase = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        edit = view.findViewById(R.id.iv_edit);
        edit.setOnClickListener(this);

        fullname = view.findViewById(R.id.tv_fullname);
        email = view.findViewById(R.id.tv_email);
        address = view.findViewById(R.id.tv_address);
        dateofbirth = view.findViewById(R.id.tv_dateofbirth);
        mobileno = view.findViewById(R.id.tv_mobileno);
        landlineno = view.findViewById(R.id.tv_landlineno);
        licenseno = view.findViewById(R.id.tv_licenseno);
        licenseexpiry = view.findViewById(R.id.tv_licenseexpiry);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadUserInfo();
    }

    public void loadUserInfo(){
        CollectionReference userColRef = mDatabase.collection("users");
        Query userQuery = userColRef.whereEqualTo("user_id", mAuth.getUid());
        userQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                userDocID = queryDocumentSnapshots.getDocuments().get(0).getId();
                Model_Users user = queryDocumentSnapshots.getDocuments().get(0).toObject(Model_Users.class);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                fullname.setText(user.getFirst_name() + " " + user.getLast_name());
                email.setText(user.getEmail());
                address.setText(user.getAddress());
                dateofbirth.setText(sdf.format(user.getBday().toDate()));
                mobileno.setText(user.getMobile_number());
                landlineno.setText(user.getLandline_number());
                licenseno.setText(user.getLicense_number());
                licenseexpiry.setText(sdf.format(user.getLicense_expiry().toDate()));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_edit:
                Bundle args = new Bundle();
                args.putString("userDocID", userDocID);
                Dialog_EditProfile dialog_editProfile = new Dialog_EditProfile();
                dialog_editProfile.setArguments(args);
                dialog_editProfile.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        loadUserInfo();
                    }
                });
                dialog_editProfile.show(Objects.requireNonNull(getFragmentManager()), "Dialog_EditProfile");
        }
    }
}
