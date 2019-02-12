package com.hiseanvaldez.fireloq;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Fragment_Logs extends Fragment {
    private View view;
    private RecyclerView rv_logs;

    private CollectionReference logRef;

    public Fragment_Logs() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_logs, container, false);

        rv_logs = view.findViewById(R.id.rv_logs);
        rv_logs.setLayoutManager(new LinearLayoutManager(getContext()));

        logRef = FirebaseFirestore.getInstance().collection("logs");
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        FirestoreRecyclerOptions<Model_Logs> options = new FirestoreRecyclerOptions.Builder<Model_Logs>()
                .setQuery(logRef, Model_Logs.class)
                .build();

        FirestoreRecyclerAdapter<Model_Logs, LogsViewHolder> adapter = new FirestoreRecyclerAdapter<Model_Logs, LogsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull LogsViewHolder holder, int position, @NonNull Model_Logs model) {
                holder.tv_logTitle.setText(model.getAction());
                holder.tv_logDatetime.setText(String.valueOf(model.getDatetime()));
            }

            @NonNull
            @Override
            public LogsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_logs, viewGroup, false);
                LogsViewHolder viewHolder = new LogsViewHolder(view);
                return viewHolder;
            }
        };
        rv_logs.setAdapter(adapter);
        adapter.startListening();
    }

    public static class LogsViewHolder extends RecyclerView.ViewHolder{
        TextView tv_logTitle, tv_logDatetime;

        public LogsViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_logTitle = itemView.findViewById(R.id.tv_logTitle);
            tv_logDatetime = itemView.findViewById(R.id.tv_logDatetime);

        }
    }
}
