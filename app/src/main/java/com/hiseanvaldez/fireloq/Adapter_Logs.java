package com.hiseanvaldez.fireloq;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class Adapter_Logs extends FirestoreRecyclerAdapter<Model_Logs, Adapter_Logs.LogsHolder> {

    public Adapter_Logs(@NonNull FirestoreRecyclerOptions<Model_Logs> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull LogsHolder holder, int position, @NonNull Model_Logs model) {
        holder.tv_title.setText(model.getLogTitle());
        holder.tv_datetime.setText(model.getLogDatetime().toString());
    }

    @NonNull
    @Override
    public LogsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_logs, viewGroup, false);
        return new LogsHolder(view);
    }

    class LogsHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        TextView tv_datetime;

        public LogsHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_logTitle);
            tv_datetime = itemView.findViewById(R.id.tv_logDatetime);
        }
    }
}
