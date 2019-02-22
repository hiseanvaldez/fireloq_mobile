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
import java.util.Objects;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

import static android.support.constraint.Constraints.TAG;

public class Fragment_Home extends Fragment implements View.OnClickListener {
    private Activity_Main main;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    BluetoothSPP bluetoothSPP;
    Button btn_on;
    SwipeButton swipeButton;
    IntentFilter filter;
    Boolean bypass = false;

    private long timer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        main = (Activity_Main) getActivity();

        mAuth = main.getMAuth();
        mDatabase = FirebaseFirestore.getInstance();
        bluetoothSPP = new BluetoothSPP(getContext());
        btn_on = view.findViewById(R.id.btn_turnOnBT);
        btn_on.setOnClickListener(this);
        swipeButton = view.findViewById(R.id.swipe_btn);
        swipeButton.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
            bypass = active;

            long time = System.nanoTime();
            parseMessage(String.valueOf((Math.random() * ((90 - (-90)) + 1)) + (-90)) + "," + String.valueOf((Math.random() * ((180 - (-180)) + 1)) + (-180))+ "," +time);
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
        if (currentUser == null) {
            startActivity(new Intent(getActivity(), Activity_Login.class));
            main.finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!bluetoothSPP.isBluetoothEnabled()) {
            btn_on.setVisibility(View.VISIBLE);
        } else {
            if (!bluetoothSPP.isServiceAvailable()) {
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

    private void setup() {
        btn_on.setVisibility(View.GONE);
        try {
            bluetoothSPP.autoConnect("FIRELOQ");
        } catch (Exception e) {
            Log.e(TAG, "onReceive: ", e);
        }
        bluetoothSPP.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            @Override
            public void onDataReceived(byte[] data, String message) {
                parseMessage(message);
            }
        });
    }

    private void parseMessage(String message) {
        String[] coordinates = message.split(",");
        if (Long.parseLong(coordinates[2]) - timer > 500) {
            writeNotification(coordinates[0], coordinates[1], coordinates[2]);
            timer = Long.parseLong(coordinates[2]);
        }
    }

    private void writeNotification(String latitude, String longitude, String timer) {
        Map<String, Object> notification = new HashMap<>();
        notification.put("datetime", new Timestamp(new Date()));
        notification.put("user_id", Objects.requireNonNull(mAuth.getUid()));
        notification.put("status", "sent");
        notification.put("timer", Long.parseLong(timer));
        notification.put("latitude", Double.parseDouble(latitude));
        notification.put("longitude", Double.parseDouble(longitude));
        if (bypass) {
            notification.put("type", "quiet");
        } else {
            notification.put("type", "loud");
        }

        mDatabase.collection("notifications")
                .add(notification)
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

        new Firestore_WriteLog(mAuth, "Gun Fired");
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (Objects.equals(action, BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                switch (state) {
                    case BluetoothAdapter.STATE_OFF: {
                        btn_on.setVisibility(View.VISIBLE);
                        bluetoothSPP.stopService();
                    }
                    case BluetoothAdapter.STATE_ON: {
                        try {
                            bluetoothSPP.autoConnect("FIRELOQ");
                        } catch (Exception e) {
                            Log.e(TAG, "onReceive: ", e);
                        }
                        btn_on.setVisibility(View.GONE);
                    }
                }
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_turnOnBT: {
                bluetoothSPP.enable();
            }
        }
    }
}
