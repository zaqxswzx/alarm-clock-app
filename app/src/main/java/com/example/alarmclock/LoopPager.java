package com.example.alarmclock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.alarmclock.R;

public class LoopPager extends RelativeLayout {

    private Button loopOneButton;
    private Button loopTwoButton;
    private Button loopStartButton;
    private Button loopClearButton;
    private Button setTimesButton;
    private Button numberPickerButton;
    private NumberPicker numberPicker1;
    private NumberPicker numberPicker2;
    private NumberPicker numberPicker3;
    private TextView timesTextView;
    private TextView loopOneTextView;
    private TextView loopTwoTextView;
    private TextView categoryTextView2;
    private EditText loopTimesEditText;

    private Boolean loopOver = false;
    private Boolean loopStart = false;
    private Boolean setOneTime;
    private Boolean ifNotClearThenStart = true;

    private int hourNumberPick;
    private int minuteNumberPick;
    private int secondNumberPick;
    private int loopTimes;
    private int saveLoopTimes;
    private int loopOneHour;
    private int loopOneMinute;
    private int loopOneSecond;
    private int loopTwoHour;
    private int loopTwoMinute;
    private int loopTwoSecond;

    private String stringLoopOneHour;
    private String stringLoopOneMinute;
    private String stringLoopOneSecond;
    private String stringLoopTwoHour;
    private String stringLoopTwoMinute;
    private String stringLoopTwoSecond;
    private String takeMain;

    private CountDownTimer countDownTimerOne;
    private CountDownTimer countDownTimerTwo;
    private MainActivity mainActivity;

    public LoopPager(Context context, int pageNumber) {//pageNumber是由ＭainActivity.java那邊傳入頁碼

        super(context);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.loop_pager, null);//連接頁面

        loopOneButton = view.findViewById(R.id.loopOneButton);
        loopTwoButton = view.findViewById(R.id.loopTwoButton);
        loopStartButton = view.findViewById(R.id.loopStartButton);
        loopClearButton = view.findViewById(R.id.loopClearButton);
        setTimesButton = view.findViewById(R.id.setTimesButton);
        timesTextView = view.findViewById(R.id.timesTextView);
        categoryTextView2 = view.findViewById(R.id.categoryTextView2);
        loopTimesEditText = view.findViewById(R.id.loopTimesEditText);
        loopOneTextView = view.findViewById(R.id.loopOneTextView);
        loopTwoTextView = view.findViewById(R.id.loopTwoTextView);
        mainActivity = new MainActivity();

        setCategory();

        /* get data */

//        String getData = mainActivity.sendData;
//        Log.i("getData", getData);

        /* set time */

        loopOneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setOneTime = true;
                windowPopup(v);
//                backgroundAlpha(0.5f);
            }
        });

        loopTwoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setOneTime = false;
                windowPopup(v);
//                backgroundAlpha(0.5f);
            }
        });

        /* time loop */
        // get times
        setTimesButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    loopTimes = Integer.parseInt(String.valueOf(loopTimesEditText.getText()));
                    saveLoopTimes = loopTimes;
                    loopTimesEditText.getText().clear(); // clear loopEditText
                    String stringLoopTimes = String.valueOf(loopTimes);
                    if (loopTimes > 99){
                        timesTextView.setText("99");
                        loopTimes = 99;
                        Toast.makeText(context, "Times need less than 100", Toast.LENGTH_LONG).show();
                    }else{
                        timesTextView.setText(stringLoopTimes);
                    }
                    hideKeyboardFrom(getContext(), v);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        // time begin

        loopStartButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!loopStart & !(loopOneHour == 0 & loopOneMinute == 0 & loopOneSecond == 0)){
                    ClockPager clockPager = new ClockPager(getContext(), 1);
                    clockPager.saveToSql(takeMain,"loop");
                    loopStartButton.setText("stop");
                    theLoopOne(loopOneHour, loopOneMinute, loopOneSecond);
                    Log.i("loopOneSecond", String.valueOf(loopOneSecond));
                    loopStartAndStopAnimate(0, 500);
                    loopStart = true;
                }else{
                    loopStartButton.setText("start");
                    loopStop();
                    loopStart = false;
                    loopStartAndStopAnimate(1, 500);
                }
            }
        });

        loopClearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ifNotClearThenStart = false;
                loopInit();
            }
        });


        addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        //將元件放入ViewPager
    }


    public void windowPopup(View v){

        /* popup window */
        View view = LayoutInflater.from(getContext()).inflate(R.layout.popup_window, null, false);
        int width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        int height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = false; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(view, width, height, focusable);
        numberPicker1 = view.findViewById(R.id.numberPicker1);
        numberPicker2 = view.findViewById(R.id.numberPicker2);
        numberPicker3 = view.findViewById(R.id.numberPicker3);
        numberPickerButton = view.findViewById(R.id.numberPickerButton);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setAnimationStyle(R.style.Animation);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.BLACK));


        // dismiss the popup window when touched
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        /* set numberPicker */
        numberPicker1.setMaxValue(72);
        numberPicker1.setMinValue(0);
        numberPicker2.setMaxValue(59);
        numberPicker2.setMinValue(0);
        numberPicker3.setMaxValue(59);
        numberPicker3.setMinValue(0);

        numberPickerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hourNumberPick = numberPicker1.getValue();
                minuteNumberPick = numberPicker2.getValue();
                secondNumberPick = numberPicker3.getValue();
                if (setOneTime){
                    loopOneHour = hourNumberPick;
                    loopOneMinute = minuteNumberPick;
                    loopOneSecond = secondNumberPick;
                    stringLoopOneHour = doubleZero(loopOneHour);
                    stringLoopOneMinute = doubleZero(loopOneMinute);
                    stringLoopOneSecond = doubleZero(loopOneSecond);
                    loopOneTextView.setText(stringLoopOneHour + " : " + stringLoopOneMinute + " : " + stringLoopOneSecond);
                }else{
                    loopTwoHour = hourNumberPick;
                    loopTwoMinute = minuteNumberPick;
                    loopTwoSecond = secondNumberPick;
                    stringLoopTwoHour = doubleZero(loopTwoHour);
                    stringLoopTwoMinute = doubleZero(loopTwoMinute);
                    stringLoopTwoSecond = doubleZero(loopTwoSecond);
                    loopTwoTextView.setText(stringLoopTwoHour + " : " + stringLoopTwoMinute + " : " + stringLoopTwoSecond);
                }
                popupWindow.dismiss();
                backgroundAlpha(1);
            }
        });

        popupWindow.update();
    }

    public void theLoopOne(int hour, int minute, int second){
        countDownTimerOne = new CountDownTimer(hour*60*60*1000 + minute*60*1000 + second*1000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                loopOneTimeBegin((int)millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                if (loopTimes>0){
                    loopTwoTextView.setText(stringLoopTwoHour + " : " + stringLoopTwoMinute + " : " + stringLoopTwoSecond);
                    theLoopTwo(loopTwoHour, loopTwoMinute, loopTwoSecond);
                    loopTimes--;
                }else if (loopTimes == 0){
                    loopInit();
                    clockVibrate();
                    clockTimeUp();
                }

            }
        }.start();
    }

    public void loopOneTimeBegin(int millisecond){
        int hour = millisecond/60/60;
        int minute = millisecond/60 - hour*60*60;
        int second = millisecond - minute*60 - hour*60*60;

        String stringHour = doubleZero(hour);
        String stringMinute = doubleZero(minute);
        String stringSecond = doubleZero(second);

        loopOneTextView.setText(stringHour + " : " + stringMinute + " : " + stringSecond);
    }

    public void theLoopTwo(int hour, int minute, int second){
        countDownTimerTwo = new CountDownTimer(hour*60*60*1000 + minute*60*1000 + second*1000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                loopTwoTimeBegin((int)millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                if (loopTimes>0){
                    loopOneTextView.setText(stringLoopOneHour + " : " + stringLoopOneMinute + " : " + stringLoopOneSecond);
                    theLoopOne(loopOneHour, loopOneMinute, loopOneSecond);
                    timesTextView.setText(String.valueOf(loopTimes));
                }else if(loopTimes == 0){
                    countDownTimerTwo.cancel();
                    loopStart = false;
                    loopStartButton.setText("start");
                    timesTextView.setText(String.valueOf(loopTimes));
                    loopStartAndStopAnimate(1, 500);
                    clockVibrate();
                    clockTimeUp();
                }
            }
        }.start();
    }

    public void loopTwoTimeBegin(int millisecond){
        int hour = millisecond/60/60;
        int minute = millisecond/60 - hour*60*60;
        int second = millisecond - minute*60 - hour*60*60;

        String stringHour = doubleZero(hour);
        String stringMinute = doubleZero(minute);
        String stringSecond = doubleZero(second);

        loopTwoTextView.setText(stringHour + " : " + stringMinute + " : " + stringSecond);
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

    public static void hideKeyboardFrom(Context context, View view) {
        if (view != null){
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void loopStartAndStopAnimate(int alpha, int duration){
        loopOneButton.animate().alpha(alpha).setDuration(duration);
        loopTwoButton.animate().alpha(alpha).setDuration(duration);
        setTimesButton.animate().alpha(alpha).setDuration(duration);
        loopTimesEditText.animate().alpha(alpha).setDuration(duration);
    }

    public void loopStop(){
        try{
            countDownTimerOne.cancel();
            countDownTimerTwo.cancel();
            timesTextView.setText(String.valueOf(saveLoopTimes));
            loopTimes = saveLoopTimes;
            loopOneTextView.setText(stringLoopOneHour + " : " + stringLoopOneMinute + " : " + stringLoopOneSecond);
            loopTwoTextView.setText(stringLoopTwoHour + " : " + stringLoopTwoMinute + " : " + stringLoopTwoSecond);
            loopStartButton.setText("start");
            loopStart = false;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void loopInit(){
        try {
            loopStartButton.setText("start");
            loopStart = false;
            timesTextView.setText("0");
            loopTimes = 0;
            hourNumberPick = 0;
            minuteNumberPick = 0;
            secondNumberPick = 0;
            saveLoopTimes = 0;
            loopOneHour = 0;
            loopOneMinute = 0;
            loopOneSecond = 0;
            loopTwoHour = 0;
            loopTwoMinute = 0;
            loopTwoSecond = 0;
            loopStartAndStopAnimate(1, 500);
            loopOneTextView.setText("00 : 00 : 00");
            loopTwoTextView.setText("00 : 00 : 00");
            countDownTimerOne.cancel();
            countDownTimerTwo.cancel();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void backgroundAlpha(float bgAlpha)
    {
        WindowManager.LayoutParams lp = ((Activity)getContext()).getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        ((Activity)getContext()).getWindow().setAttributes(lp);
        ((Activity)getContext()).getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    public void setCategory(){
        try{
            Intent intent = ((Activity)getContext()).getIntent();
            takeMain = intent.getStringExtra("sendName");
            if (takeMain == null){
                takeMain = "empty";
            }
            categoryTextView2.setText(takeMain);
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
