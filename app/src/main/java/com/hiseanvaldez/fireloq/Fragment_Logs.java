package com.hiseanvaldez.fireloq;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

public class Fragment_Logs extends Fragment {
    private RecyclerView rv_logs;
    private Query logQuery;

    public Fragment_Logs() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logs, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();

        rv_logs = view.findViewById(R.id.rv_logs);
        rv_logs.setLayoutManager(new LinearLayoutManager(getContext()));

        CollectionReference logRef = mDatabase.collection("logs");
        logQuery = logRef
                .whereEqualTo("user_id", mAuth.getUid())
                .orderBy("datetime", Query.Direction.DESCENDING)
                .limit(50);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirestoreRecyclerOptions<Model_Logs> options = new FirestoreRecyclerOptions.Builder<Model_Logs>()
                .setQuery(logQuery, Model_Logs.class)
                .build();

        FirestoreRecyclerAdapter<Model_Logs, LogsViewHolder> adapter = new FirestoreRecyclerAdapter<Model_Logs, LogsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LogsViewHolder holder, int position, @NonNull Model_Logs model) {
                holder.tv_logTitle.setText(model.getAction());

                Date date = model.getDatetime().toDate();
                SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
                String stringDate = format.format(date);

                holder.tv_logDatetime.setText(stringDate);
                holder.tv_logUserId.setText(model.getUser_id());
            }

            @NonNull
            @Override
            public LogsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_logs, viewGroup, false);
                return new LogsViewHolder(view);
            }
        };
        rv_logs.setAdapter(adapter);
        adapter.startListening();
    }

    public static class LogsViewHolder extends RecyclerView.ViewHolder {
        TextView tv_logTitle, tv_logDatetime, tv_logUserId;

        LogsViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_logTitle = itemView.findViewById(R.id.tv_logTitle);
            tv_logDatetime = itemView.findViewById(R.id.tv_logDatetime);
            tv_logUserId = itemView.findViewById(R.id.tv_logUserId);
        }
    }
}
