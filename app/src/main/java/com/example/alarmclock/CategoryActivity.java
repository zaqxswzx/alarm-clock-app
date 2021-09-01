package com.example.alarmclock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.alarmclock.recyclerViewHolder.MyRecyclerViewAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategoryActivity extends AppCompatActivity {

    RecyclerView categoryRecyclerView;
    ArrayList<String> categoryNames;
    private MyRecyclerViewAdapter myRecyclerViewAdapter;
    private EditText categoryEditText;
    private Button checkAddButton;
    private boolean popupWindowShow = false;
    private PopupWindow popupWindow;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        sharedPreferences = this.getSharedPreferences("com.example.alarmclock", Context.MODE_PRIVATE);

        categoryNames = new ArrayList<String>();
        categoryRecyclerView = findViewById(R.id.categoryRecyclerView );
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        categoryNames.add("ADD CATEGORY");
        categoryNames.add("push up");
        categoryNames.add("English");
        categoryNames.add("take a break");

        outOfGson();

        myRecyclerViewAdapter = new MyRecyclerViewAdapter(categoryNames);
        categoryRecyclerView.setAdapter(myRecyclerViewAdapter);
        myRecyclerViewAdapter.setOnTransButtonClick(new MyRecyclerViewAdapter.OnTransClick() {
            @Override
            public void OnTransButtonClick(View v, String s) {
                if (s.equals("add")){
                        popupWindowAddCategory(v);
                        popupWindowShow = true;
                }else{
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.putExtra("sendName", s);
                        startActivity(intent);
                }
            }
        });

        hideNavigationBarAndStatusBar();

    }


    public void backMain(View v){
        Intent intent = new Intent(CategoryActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void selectAdd(String name){
        categoryNames.add(name);
        saveData(categoryNames);
        myRecyclerViewAdapter.notifyDataSetChanged();
    }

    public void saveData(ArrayList<String> arrayList){

        Gson gson = new Gson();
        // implementation 'com.google.code.gson:gson:2.8.7'

        String jsonText = gson.toJson(arrayList);
        sharedPreferences.edit().putString("category", jsonText).apply();
    }

    public void outOfGson(){
        Gson gson = new Gson();
        try {
            String getCategory = sharedPreferences.getString("category", null);
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            if (getCategory != null){
                categoryNames = gson.fromJson(getCategory, type);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void hideNavigationBarAndStatusBar(){
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    public void popupWindowAddCategory(View v){
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.category_add_popupwindow, null, false);
        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false; // lets taps outside the popup also dismiss it
        popupWindow = new PopupWindow(view, width, height, focusable);

        categoryEditText = view.findViewById(R.id.categoryEditText);
        checkAddButton = view.findViewById(R.id.checkAddButton);

        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);

        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setAnimationStyle(R.style.Animation);
//        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupWindow.update(); // it will open soft keyboard when click edittext

        // dismiss the popup window when touched
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });


        checkAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String name = categoryEditText.getText().toString();
                    Log.i("add category", name);

                    Pattern pattern = Pattern.compile("(?i)add");
                    Matcher matcher = pattern.matcher(name);
                    boolean addOrNot = matcher.matches();
                    if (!addOrNot){
                        selectAdd(name);
                        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                    }else if(addOrNot){
                        Toast.makeText(getApplicationContext(), "Can't increase add.", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Not Add Success.", Toast.LENGTH_SHORT).show();
                }
                popupWindow.dismiss();
                popupWindowShow = false;
            }
        });
    }
}