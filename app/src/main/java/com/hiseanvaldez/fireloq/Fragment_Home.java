package com.hiseanvaldez.fireloq;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;

import static android.support.constraint.Constraints.TAG;

public class Fragment_Home extends Fragment implements View.OnClickListener {
    private Activity_Main main;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDatabase;
    private BluetoothSPP bluetoothSPP;
    private Button btn_on;
    private Boolean bypass = false;

    private long timer;

    Animation show, hide;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        main = (Activity_Main) getActivity();

        mAuth = Objects.requireNonNull(main).getMAuth();
        mDatabase = FirebaseFirestore.getInstance();
        bluetoothSPP = new BluetoothSPP(getContext());
        show = AnimationUtils.loadAnimation(getContext(), R.anim.show);
        hide = AnimationUtils.loadAnimation(getContext(), R.anim.hide);
        btn_on = view.findViewById(R.id.btn_turnOnBT);
        btn_on.setOnClickListener(this);
        SwipeButton swipeButton = view.findViewById(R.id.swipe_btn);
        swipeButton.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                bypass = active;

//                long time = System.nanoTime();
//                parseMessage(String.valueOf((Math.random() * ((90 - (-90)) + 1)) + (-90)) + "," + String.valueOf((Math.random() * ((180 - (-180)) + 1)) + (-180)) + "," + time);
            }
        });

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        main.registerReceiver(mReceiver, filter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(getActivity(), Activity_Login.class));
            main.finish();
        }

        CollectionReference userColRef = mDatabase.collection("users");
        Query userQuery = userColRef.whereEqualTo("user_id", mAuth.getUid());
        userQuery.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Model_Users user = queryDocumentSnapshots.getDocuments().get(0).toObject(Model_Users.class);
                Date firstDate = new Date();
                Date secondDate = user.getLicense_expiry().toDate();
                long diffInMillis = Math.abs(secondDate.getTime() - firstDate.getTime());
                long diff = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);

                Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), "LTOPF ID nearing expiry, " + String.valueOf(diff) + " days left.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                if (diff < 30 && diff > 0) {
                    snack.setText("LTOPF ID nearing expiry, " + String.valueOf(diff) + " days left.");
                }
                else if(diff < 1){
                    snack.setText("LTOPF is expired.");
                }
                snack.show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed to retrieve user data.", Toast.LENGTH_LONG).show();
            }
        });
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        main.unregisterReceiver(mReceiver);
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
            writeNotification(coordinates[0], coordinates[1]);
            timer = Long.parseLong(coordinates[2]);
        }
    }

    private void writeNotification(String latitude, String longitude) {
        Map<String, Object> notification = new HashMap<>();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a");
        Date now = new Date();
        notification.put("datetime", format.format(now));
        notification.put("user_id", Objects.requireNonNull(mAuth.getUid()));
        notification.put("status", "sent");
        notification.put("latitude", Double.parseDouble(latitude));
        notification.put("longitude", Double.parseDouble(longitude));
        notification.put("source", "bluetooth");
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
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        btn_on.startAnimation(show);
                        btn_on.setClickable(true);
                        btn_on.setVisibility(View.VISIBLE);
                        bluetoothSPP.stopService();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        try {
                            bluetoothSPP.autoConnect("FIRELOQ");
                        } catch (Exception e) {
                            Log.e(TAG, "onReceive: ", e);
                        }
                        btn_on.startAnimation(hide);
                        btn_on.setClickable(false);
                        btn_on.setVisibility(View.GONE);
                        break;
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
