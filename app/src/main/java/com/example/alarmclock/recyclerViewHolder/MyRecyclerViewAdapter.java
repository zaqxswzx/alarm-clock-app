package com.example.alarmclock.recyclerViewHolder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.alarmclock.CategoryActivity;
import com.example.alarmclock.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;


public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    private ArrayList<String> arrayList;
    public OnTransClick onItemClick;
    Context context;

    public MyRecyclerViewAdapter(ArrayList<String> arrayList) {
        this.arrayList = arrayList;
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView categoryTextView;
        private Button switchCategoryButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Log.i("position", String.valueOf(getAdapterPosition()));
                        if (getAdapterPosition() != 0){
                            arrayList.remove(getAdapterPosition());
                            notifyItemRemoved(getAdapterPosition());
                            saveArrayList();
                        }
                        return true;
                    }
                });
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            switchCategoryButton = itemView.findViewById(R.id.switchCategoryButton);
        }
    }

    /**設置將資料傳回Activity的接口*/
    public void setOnTransButtonClick(OnTransClick onItemClick){
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // 連接layout，return一個view
        /**在上面的"getItemViewType"中取得的
         * @see viewType
         * 為基準，判斷每個item需使用哪個介面*/
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        context = parent.getContext();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        // 取得元件控制
        holder.categoryTextView.setText(arrayList.get(position));
        holder.switchCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.OnTransButtonClick(v, arrayList.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
    }
    // 取得顯示數量

    /**設置點擊方法，使點擊後取得到的內容能傳回MainActivity*/
    public interface OnTransClick{

        void OnTransButtonClick(View v, String s);
    }

    public void saveArrayList(){
        SharedPreferences sharedPreferences = context.getSharedPreferences("com.example.alarmclock", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        // implementation 'com.google.code.gson:gson:2.8.7'

        String jsonText = gson.toJson(arrayList);
        sharedPreferences.edit().putString("category", jsonText).apply();

    }



}
