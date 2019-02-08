package com.hiseanvaldez.fireloq;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

import static android.support.constraint.Constraints.TAG;

public class Fragment_Home extends Fragment implements View.OnClickListener{
    Activity_Main main;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;

    TextView tv_stream;
    BluetoothSPP bluetoothSPP;
    Button btn_on;
    SwipeButton swipeButton;
    IntentFilter filter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        main = (Activity_Main)getActivity();

        mAuth = main.getMAuth();
        mDatabase = FirebaseFirestore.getInstance();
        bluetoothSPP = new BluetoothSPP(getContext());
        tv_stream = view.findViewById(R.id.tv_stream);
        btn_on = view.findViewById(R.id.btn_turnOnBT);
        btn_on.setOnClickListener(this);
        swipeButton = view.findViewById(R.id.swipe_btn);
        swipeButton.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                Toast.makeText(getContext(), "Active : "+active, Toast.LENGTH_SHORT).show();
                parseMessage("10.343246,123.913942");
            }
        });

        filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        main.registerReceiver(mReceiver, filter);
        if (currentUser == null){
            startActivity(new Intent(getActivity(), Activity_Login.class));
            main.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!bluetoothSPP.isBluetoothEnabled()) {
            btn_on.setVisibility(View.VISIBLE);
        }
        else {
            if(!bluetoothSPP.isServiceAvailable()){
                bluetoothSPP.setupService();
                bluetoothSPP.startService(BluetoothState.DEVICE_OTHER);
                setup();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        main.unregisterReceiver(mReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void setup(){
        btn_on.setVisibility(View.GONE);
        try {
            bluetoothSPP.autoConnect("FIRELOQ");
        }
        catch (Exception e){
            Log.e(TAG, "onReceive: ", e );
        }
        bluetoothSPP.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                String currentText = tv_stream.getText().toString();
                tv_stream.setText(message + "\n" + currentText);
            }
        });
    }

    private void parseMessage(String message){
        String[] coordinates = message.split(",");
        insertToFirestore(coordinates[0], coordinates[1]);
    }

    private void insertToFirestore(String longitude, String latitude){
        Map<String, Object> notif = new HashMap<>();
        notif.put("datetime", new Timestamp(new Date()));
        notif.put("user_id", mAuth.getUid());
        notif.put("status", "sent");
        notif.put("coordinates", new GeoPoint(Double.parseDouble(longitude), Double.parseDouble(latitude)));

        // Add a new document with a generated ID
        mDatabase.collection("notifications")
                .add(notif)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(getContext(),"added " + documentReference.getId(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getContext(),"not added", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state){
                    case BluetoothAdapter.STATE_OFF: {
                        btn_on.setVisibility(View.VISIBLE);
                        bluetoothSPP.stopService();
                    }
                    case BluetoothAdapter.STATE_ON: {
                        try {
                            bluetoothSPP.autoConnect("FIRELOQ");
                        }
                        catch (Exception e){
                            Log.e(TAG, "onReceive: ", e );
                        }
                        btn_on.setVisibility(View.GONE);
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_turnOnBT:
            {
                bluetoothSPP.enable();
            }
        }
    }
}
