package com.example.alarmclock.recyclerViewHolder;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.alarmclock.R;

import java.util.ArrayList;

public class JournalRecyclerViewAdapter extends RecyclerView.Adapter<JournalRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<ArrayList<String>> arrayList;

    public JournalRecyclerViewAdapter(ArrayList<ArrayList<String>> arrayList) {
        this.arrayList = arrayList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView journalMethod;
        private TextView journalCategory;
        private TextView journalTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            journalMethod = itemView.findViewById(R.id.journalMethod);
            journalCategory = itemView.findViewById(R.id.journalCategory);
            journalTime = itemView.findViewById(R.id.journalTime);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String method = arrayList.get(position).get(0);
        String category = arrayList.get(position).get(1);
        String time = arrayList.get(position).get(2);

        holder.journalMethod.setText(method);
        holder.journalCategory.setText(category);
        holder.journalTime.setText(time);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

}
