package com.tianran.trweatherapp;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeThread extends Thread{
    public TextView  date_tv;
    private int msgkey=2;

    public TimeThread(TextView date_tv){
        this.date_tv=date_tv;
    }

    @Override
    public void run() {
        super.run();
        do{
            try {
                Thread.sleep(1000);
                Message msg=new Message();
                msg.what=msgkey;
                mHandler.sendMessage(msg);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }while (true);
    }

    private Handler mHandler=new Handler(){
        public void handleMessage(android.os.Message msg){
            switch (msg.what){
                case 2:
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date=sdf.format(new Date());
                    date_tv.setText(date);
                    break;
                default:
                    break;
            }
        }
    };
}
