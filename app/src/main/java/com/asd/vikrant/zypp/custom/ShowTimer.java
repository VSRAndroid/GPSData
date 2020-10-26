package com.asd.vikrant.zypp.custom;

import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

public class ShowTimer {
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    TextView timeCountTextView;
    Boolean checkClick = false;

    public void StartTimer(TextView timeCountTextView)
     {
        this.timeCountTextView = timeCountTextView;
    //    checkClick = true;
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

    }

    public void StopTimer(TextView timeCountTextView) {
          this.timeCountTextView = timeCountTextView;
          timeSwapBuff += timeInMilliseconds;
          customHandler.removeCallbacks(updateTimerThread);
          timeCountTextView.setText("Count Time: 00:00:00");

         /*try {
            customHandler.removeCallbacks(updateTimerThread);
           *//* customHandler.removeCallbacksAndMessages(updateTimerThread);
            customHandler = null;
            updateTimerThread = null;
          *//*
           }catch (Exception e){
            Log.e("ThreadUtil:","Error:"+e.toString());*/

    }

    private Runnable updateTimerThread = new Runnable()
       {
        public void run()
        {
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                updatedTime = timeSwapBuff + timeInMilliseconds;
                int secs = (int) (timeInMilliseconds / 1000);
                int mins = secs / 60;
                secs = secs % 60;
                int hours = mins / 60;
                mins = mins % 60;
                //int milliseconds = (int) (updatedTime % 1000);
                //+ ":" + String.format("%03d", milliseconds)
                String timer = "Count Time: "  + String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs);
                timeCountTextView.setText(timer);
                //set your textView to the String timer here
                customHandler.postDelayed(this, 1000);

            }
       /* }
            else{
            timeCountTextView.setText("00:00:00");
        }*/
    };
}