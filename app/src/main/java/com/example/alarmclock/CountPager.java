package com.example.alarmclock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.alarmclock.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class CountPager extends RelativeLayout {

    private CountDownTimer countDownTimer;
    private Button countButton;
    private Button clearButton;
    private Button hourButton;
    private Button minuteButton;
    private Button secondButton;
    private EditText timeEditText;
    private TextView countTextView;
    private TextView categoryTextView;
    private int hour = 0;
    private int minute = 0;
    private int second = 0;
    private int totalSeconds = 0;
    private Boolean clickHour = false;
    private Boolean clickMinute = false;
    private Boolean clickSecond = false;
    private Boolean countTimeStop = false;
    private String stringHour = "00";
    private String stringMinute = "00";
    private String stringSecond = "00";
    private String takeMain;
    private MediaPlayer mediaPlayer;

    public CountPager(Context context, int pageNumber) {//pageNumber是由ＭainActivity.java那邊傳入頁碼

        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.count_pager, null);//連接頁面
        countButton = view.findViewById(R.id.countButton);
        clearButton = view.findViewById(R.id.clearButton);
        hourButton = view.findViewById(R.id.hourButton);
        minuteButton = view.findViewById(R.id.minuteButton);
        secondButton = view.findViewById(R.id.secondButton);
        timeEditText = view.findViewById(R.id.timeEditText);
        countTextView = view.findViewById(R.id.countTextView);
        categoryTextView = view.findViewById(R.id.categoryTextView3);
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.long_beep);

        setCategory();

        hourButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clickHour){
                    typeTime(v);
                    changeTime();
                    clickHour = true;
                }
                hideKeyboardFrom(getContext(), v);
            }
        });

        minuteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clickMinute){
                    typeTime(v);
                    changeTime();
                    clickMinute = true;
                }
                hideKeyboardFrom(getContext(), v);
            }
        });

        secondButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!clickSecond){
                    typeTime(v);
                    changeTime();
                    clickSecond = true;
                }
                hideKeyboardFrom(getContext(), v);
            }
        });

        countButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!countTimeStop){
                    if (!(hour == 0 & minute == 0 & second == 0)) {
                        ClockPager clockPager = new ClockPager(getContext(), 1);
                        clockPager.saveToSql(takeMain,"count");
                        timeBegin(hour, minute, second);
                        countButton.setText("stop");
                        countTimeStop = true;
                        countAnimate(0, 500);
                    }
                }else if(countTimeStop){
                    countInit();
                }
                hideKeyboardFrom(getContext(), v);
            }
        });

        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                countInit();
                hideKeyboardFrom(getContext(), v);
            }
        });

        addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //將元件放入ViewPager
    }


    public void typeTime(View view){
        try {
            int typeTimeText = Integer.parseInt(String.valueOf(timeEditText.getText()));
            timeEditText.getText().clear();
            if (view == hourButton){
                hour = typeTimeText + hour;
                stringHour = doubleZero(hour);
            }
            if (view == minuteButton){
                if (typeTimeText > 59){
                    int moreHour = typeTimeText / 60;
                    hour += moreHour;
                    stringHour = doubleZero(hour);
                    typeTimeText = typeTimeText % 60;
                }
                minute = typeTimeText + minute;
                stringMinute = doubleZero(minute);
            }
            if (view == secondButton){
                if (typeTimeText > 59){
                    int moreMinute = typeTimeText / 60;
                    minute += moreMinute;
                    stringMinute = doubleZero(minute);
                    typeTimeText = typeTimeText % 60;
                }
                second = typeTimeText;
                stringSecond = doubleZero(second);
            }
            totalSeconds = hour + minute + second;
        }catch (Exception e){
            e.printStackTrace();
        }
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

    public void changeTime(){
        countTextView.setText(stringHour + " : " + stringMinute + " : " + stringSecond);
    }

    public void timeBegin(int hour, int minute, int second){

        countDownTimer = new CountDownTimer(hour*60*60*1000 + minute*60*1000 + second*1000 + 100, 1000){

            @Override
            public void onTick(long millisUntilFinished) {
                if (millisUntilFinished > 0){
                    updateTime(millisUntilFinished / 1000);
                    Log.i("count", String.valueOf(millisUntilFinished/1000));
                }
            }

            @Override
            public void onFinish() {
                countInit();
                clockTimeUp();
                clockVibrate();
            }
        }.start();
    }

    public void updateTime(long secondsLeft){
        int hours = (int)secondsLeft / 60 / 60;
        int minutes = (int)secondsLeft / 60 - hours * 60;
        int seconds = (int)secondsLeft - (hours*60*60 + minutes*60);

        stringHour = doubleZero(hours);
        stringMinute = doubleZero(minutes);
        stringSecond = doubleZero(seconds);

        countTextView.setText(stringHour + " : " + stringMinute + " : " + stringSecond);
    }

    public void countInit(){
        try {
            clickHour = false;
            clickMinute = false;
            clickSecond = false;
            countTimeStop = false;
            stringHour = "00";
            stringMinute = "00";
            stringSecond = "00";
            hour = 0;
            minute = 0;
            second = 0;
            totalSeconds = 0;
            countTextView.setText("00 : 00 : 00");
            countButton.setText("start");
            countAnimate(1, 500);
            countDownTimer.cancel();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void hideKeyboardFrom(Context context, View view) {
        if (view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public void countAnimate(int alpha, int duration){
        hourButton.animate().alpha(alpha).setDuration(duration);
        minuteButton.animate().alpha(alpha).setDuration(duration);
        secondButton.animate().alpha(alpha).setDuration(duration);
        timeEditText.animate().alpha(alpha).setDuration(duration);
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

    public void clockVibrate(){
        Vibrator vibrator = (Vibrator)getContext().getSystemService(getContext().VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
    }

    public void clockTimeUp(){
        MediaPlayer mediaPlayer;
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.long_beep);
        mediaPlayer.start();
    }
}
