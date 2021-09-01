package com.example.alarmclock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.sql.SQLData;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ClockPager extends RelativeLayout {

    private Button clockButton;
    private TextView clockTextView;
    private TextView clockTimeTextView;
    private TextView categoryTextView;
    private TimePicker timePicker;
    private Integer textChange = 0;
    private String showTime = null;
    private Integer clockTextViewShow = 500;
    private Integer timePickerShow = 500;
    private CountDownTimer CountDownTimer;
    private Calendar calendar;
    private HashMap<String, Integer> phoneCurrentTime;
    private HashMap<String, Integer> timePickerTime;
    private int totalTimeMinusMillisecond = 1;
    private String takeMain;
    private boolean reallyStart = false;


    public ClockPager(Context context, int pageNumber) {//pageNumber是由ＭainActivity.java那邊傳入頁碼
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.clock_pager, null);//連接頁面
        clockButton = view.findViewById(R.id.clockButton);
        clockTextView = view.findViewById(R.id.clockTextView);
        clockTimeTextView = view.findViewById(R.id.clockTimeTextView);
        timePicker = view.findViewById(R.id.timePicker);
        categoryTextView = view.findViewById(R.id.categoryTextView);
        timePicker.setTranslationY(-3000);
        timePicker.setIs24HourView(true);
        phoneCurrentTime = new HashMap<>();
        timePickerTime = new HashMap<>();

        setCategory();


        /* button */

        clockButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                int hour = calendar.getTime().getHours();
                int minute = calendar.getTime().getMinutes();
                int second = calendar.getTime().getSeconds();

                int timePickerHour = timePicker.getHour();
                int timePickerMinute = timePicker.getMinute();
                Boolean checkTimePositive = (timePickerHour*60*60 + timePickerMinute*60) > (hour*60*60 + minute*60 + second);

                if (textChange == 0){
                    timeAnimate(3000, timePickerShow, 0f, clockTextViewShow, "save");
                    textChange = 1;
                }else if (textChange == 1){
                    timeAnimate(-3000, timePickerShow, 1f, clockTextViewShow, "start");
                    timePickerTime.put("hour", timePickerHour);
                    timePickerTime.put("minute", timePickerMinute);
                    showTime = doubleZero(timePicker.getHour()) + " : " + doubleZero(timePicker.getMinute());
                    clockTimeTextView.setText(showTime);
                    saveToSql(takeMain, "time up");
                    textChange = 2;
                }else if(textChange == 2){
                    phoneCurrentTime.put("hour", hour);
                    phoneCurrentTime.put("minute", minute);
                    phoneCurrentTime.put("second", second);
                    if (checkTimePositive){
                        timeBegin(timePicker.getHour(), timePicker.getMinute());
                    }else{
                        timeBegin(timePicker.getHour() + 24, timePicker.getMinute());
                    }
                    clockButton.setText("stop");
                    textChange = 3;
                }else if (textChange == 3){
                    clockInit();
                }
            }
        });
        addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //將元件放入ViewPager
    }

    public void timeAnimate(Integer translationY, Integer timePickerShow, float textVisible, Integer clockTextViewShow, String setButtonText){
        timePicker.animate().translationYBy(translationY).setDuration(timePickerShow);
        clockTextView.animate().alpha(textVisible).setDuration(clockTextViewShow);
        clockTimeTextView.animate().alpha(textVisible).setDuration(clockTextViewShow);
        clockButton.setText(setButtonText);
    }

    public void timeBegin(int hour, int minute){
        CountDownTimer = new CountDownTimer(hour*60*60*1000 + minute*60*1000 + 100, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                if (totalTimeMinusMillisecond > 0){
                    updateTime(millisUntilFinished / 1000, phoneCurrentTime);
                }else{
                    /* onFinish*/
                    clockInit();
                    clockTimeUp();
                    clockVibrate();
                }
            }
            @Override
            public void onFinish() {
            }
        }.start();
    }

    public void updateTime(long millisecond, HashMap<String, Integer> currentTime){

        /* current time */

        int currentHour = currentTime.get("hour");
        int currentMinute = currentTime.get("minute");
        int currentSecond = currentTime.get("second");
        int currentTotalTime = currentHour*60*60 + currentMinute*60 + currentSecond;;

        /* left time */

        totalTimeMinusMillisecond = (int)millisecond - currentTotalTime;
        int newHour = totalTimeMinusMillisecond / 60 / 60;
        int newMinute = (totalTimeMinusMillisecond - newHour*60*60) / 60;
        int newSecond = totalTimeMinusMillisecond - newMinute*60 - newHour *60*60;

        String stringHour = doubleZero(newHour);
        String stringMin = doubleZero(newMinute);
        String stringSec = doubleZero(newSecond);

        clockTextView.setText(stringHour + " : " + stringMin + " : " + stringSec);
    }

    public String doubleZero(int time){
        String stringTime = String.valueOf(time);
        if (time == 0){
            stringTime = "00";
        }else if(time < 10 & time > 0){
            stringTime = "0" + time;
        }
        return stringTime;
    }

    public void clockInit(){
        clockButton.setText("set time");
        CountDownTimer.cancel();
        textChange = 0;
        totalTimeMinusMillisecond = 1;
        phoneCurrentTime.clear();
        timePickerTime.clear();
    }

    public void clockTimeUp(){
        MediaPlayer mediaPlayer;
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.long_beep);
        mediaPlayer.start();
    }

    public void clockVibrate(){
        Vibrator vibrator = (Vibrator)getContext().getSystemService(getContext().VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
    }

    public void setCategory(){
        try{
            Intent intent = ((Activity)getContext()).getIntent();
            takeMain = intent.getStringExtra("sendName");
            if (takeMain == null){
                takeMain = "empty";
            }
            categoryTextView.setText(takeMain);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void saveToSql(String category, String method){
        SQLiteDatabase sqLiteDatabase = this.getContext().openOrCreateDatabase("TimeMaster", Context.MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS timeMaster (year VARCHAR(4), month VARCHAR(2), day VARCHAR(2), hour VARCHAR(2), minute VARCHAR(2), category VARCHAR(10), method VARCHAR(10))");
        LocalDate today = LocalDate.now();
        LocalTime time = LocalTime.now();
        String year = String.valueOf(today.getYear());
        String month = String.valueOf(today.getMonthValue());
        String day = String.valueOf(today.getDayOfMonth());
        String hour = String.valueOf(time.getHour());
        String minute = String.valueOf(time.getMinute());
        String sql = "INSERT INTO timeMaster (year, month, day, hour, minute, category, method) VALUES (?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = sqLiteDatabase.compileStatement(sql);
        statement.bindString(1, year);
        statement.bindString(2, month);
        statement.bindString(3, day);
        statement.bindString(4, hour);
        statement.bindString(5, minute);
        statement.bindString(6, category);
        statement.bindString(7, method);
        statement.execute();
    }
}
