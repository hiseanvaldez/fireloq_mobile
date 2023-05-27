package com.hiseanvaldez.fireloq;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Date;
import java.util.Objects;


public class Fragment_Guns extends Fragment {
    private RecyclerView rv_guns;
    private Query gunQuery;

    public Fragment_Guns() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guns, container, false);


        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

        rv_guns = view.findViewById(R.id.rv_guns);
        rv_guns.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton addGun = view.findViewById(R.id.fb_addGun);
        addGun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_AddGun dialog_addGun = new Dialog_AddGun();
                dialog_addGun.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        loadList();
                    }
                });
                dialog_addGun.show(Objects.requireNonNull(getFragmentManager()), "Dialog_AddGun");

            }
        });

        CollectionReference gunRef = mDatabase.collection("guns");
        gunQuery = gunRef
                .whereEqualTo("user_id", mAuth.getUid())
                .whereEqualTo("status", "pending")
                .orderBy("datetime", Query.Direction.DESCENDING)
                .limit(50);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadList();
    }

    public void loadList(){
        FirestoreRecyclerOptions<Model_Guns> options = new FirestoreRecyclerOptions.Builder<Model_Guns>()
                .setQuery(gunQuery,Model_Guns.class)
                .build();

        FirestoreRecyclerAdapter<Model_Guns, GunsViewHolder> adapter = new FirestoreRecyclerAdapter<Model_Guns, GunsViewHolder>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NonNull GunsViewHolder holder, int position, @NonNull Model_Guns model) {
                holder.tv_makeModel.setText(model.getManufacturer() + " " + model.getModel());
                holder.tv_serial.setText(model.getSerial_number());

                Date reg = model.getRegistration_date().toDate();
                Date exp = model.getExpiry_date().toDate();
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                String sReg = format.format(reg);
                String sExp = format.format(exp);
                holder.tv_startEnd.setText(sReg + " - " + sExp);
            }

            @NonNull
            @Override
            public GunsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_guns, viewGroup, false);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Fragment_GunDetails gunDetails = new Fragment_GunDetails();
//                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                        fragmentTransaction.replace(R.id.fragment_container, gunDetails);
//                        fragmentTransaction.addToBackStack(null);
//                        fragmentTransaction.commit();
                    }
                });
                return new GunsViewHolder(view);
            }
        };

        rv_guns.setAdapter(adapter);
        adapter.startListening();
    }

    public static class GunsViewHolder extends RecyclerView.ViewHolder{
        TextView tv_makeModel, tv_serial, tv_startEnd;

        GunsViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_makeModel = itemView.findViewById(R.id.tv_makeModel);
            tv_serial = itemView.findViewById(R.id.tv_serial);
            tv_startEnd = itemView.findViewById(R.id.tv_startEnd);
        }
    }
}
