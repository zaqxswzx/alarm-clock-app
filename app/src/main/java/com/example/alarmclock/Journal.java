package com.example.alarmclock;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.alarmclock.recyclerViewHolder.JournalRecyclerViewAdapter;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Journal extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView journalRecyclerView;
    private JournalRecyclerViewAdapter journalRecyclerViewAdapter;
    private ArrayList<ArrayList<String>> dataOfCategory;
    private ArrayList<ArrayList<String>> keepDataOfCategory;
    private boolean calendarAlreadyClick = false;
    private boolean noDataThatDay = false;
    private String calendarYear;
    private String calendarMonth;
    private String calendarDay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        calendarView = findViewById(R.id.calendarView);
        journalRecyclerView = findViewById(R.id.journalRecyclerView);
        dataOfCategory = new ArrayList<>();
        keepDataOfCategory = new ArrayList<>();

        calendarView.setTranslationY(-3000);
        LocalDate localDate = LocalDate.now();
        calendarYear = String.valueOf(localDate.getYear());
        calendarMonth = String.valueOf(localDate.getMonthValue());
        calendarDay = String .valueOf(localDate.getDayOfMonth());

        takeSqlOut();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        journalRecyclerView.setLayoutManager(linearLayoutManager);
        journalRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        journalRecyclerViewAdapter = new JournalRecyclerViewAdapter(keepDataOfCategory);
        journalRecyclerView.setAdapter(journalRecyclerViewAdapter);
        useCalendarView();

        hideNavigationBarAndStatusBar();
    }

    public void useCalendarView(){
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                calendarYear = String.valueOf(year);
                calendarMonth = String.valueOf(month + 1);
                calendarDay = String.valueOf(dayOfMonth);
                Log.i("calendar year&month&day", calendarYear + "/" + calendarMonth + "/" + calendarDay);

                hideCalendar();
                takeSqlOut();
                journalRecyclerViewAdapter.notifyDataSetChanged();
            }
        });
    }

    public void takeSqlOut(){
        SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("TimeMaster", MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS timeMaster (year INT(4), month INT(2), day INT(2), hour INT(2), minute INT(2), category VARCHAR(10), method VARCHAR(10))");
        Cursor c = sqLiteDatabase.rawQuery("SELECT * FROM timeMaster", null);
        int year = c.getColumnIndex("year");
        int month = c.getColumnIndex("month");
        int day = c.getColumnIndex("day");
        int hour = c.getColumnIndex("hour");
        int minute = c.getColumnIndex("minute");
        int category = c.getColumnIndex("category");
        int method = c.getColumnIndex("method");

        c.moveToFirst();
        while (!c.isAfterLast()){
            Log.i("year & month & day", c.getString(year) + "/" + c.getString(month) + "/" + c.getString(day));
            if (c.getString(year).equals(calendarYear) && c.getString(month).equals(calendarMonth) && c.getString(day).equals(calendarDay)){
                ArrayList<String> putInArrayList = new ArrayList<>();
                String hourAndMinute = c.getString(hour) + ":" + c.getString(minute);
                putInArrayList.add(c.getString(method));
                putInArrayList.add(c.getString(category));
                putInArrayList.add(hourAndMinute);
                dataOfCategory.add(putInArrayList);
                Log.i("take out", "sql");
            }
            c.moveToNext();
        }
        if (dataOfCategory.isEmpty()){
            keepDataOfCategory.clear();
        }else{
            keepDataOfCategory.addAll(dataOfCategory);
            dataOfCategory.clear();
        }
    }

    public void showCalendar(View view) {
        if (!calendarAlreadyClick){
            calendarView.animate().translationYBy(3000).setDuration(900);
            journalRecyclerView.animate().alpha(0f).setDuration(900);
            calendarAlreadyClick = true;
        }
    }

    public void hideCalendar(){
        calendarView.animate().translationYBy(-3000).setDuration(900);
        journalRecyclerView.animate().alpha(1f).setDuration(900);
        calendarAlreadyClick = false;
    }

    public void backMain(View v){
        Intent intent = new Intent(Journal.this, MainActivity.class);
        startActivity(intent);
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
}