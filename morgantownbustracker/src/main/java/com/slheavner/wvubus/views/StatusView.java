package com.slheavner.wvubus.views;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.slheavner.wvubus.R;
import com.slheavner.wvubus.models.Bus;

import java.util.Locale;

/**
 * Created by Sam on 12/14/2015.
 */
public class StatusView extends RelativeLayout{

    private TextView number, time, status;
    private View divider;
    private int index = -1;
    protected TimeHandler timeHandler;
    boolean calculate = true;
    protected TimeThread timeThread;

    public StatusView(Context context){
        super(context);
        init();
    }

    public StatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StatusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.status, this);
        this.number = (TextView)findViewById(R.id.status_number);
        this.time = (TextView)findViewById(R.id.status_time);
        this.status = (TextView)findViewById(R.id.status_status);
        this.divider = findViewById(R.id.status_divider);
        this.timeHandler = new TimeHandler(this);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int i){
        this.index = i;
        if(i == 3){
            this.hideDivider();
        }
    }

    public void showDivider(){
        this.divider.setVisibility(View.VISIBLE);
    }

    public void hideDivider(){
        this.divider.setVisibility(View.GONE);
    }

    public String getNumber() {
        return number.getText().toString();
    }

    public void setNumber(String number) {
        this.number.setText(number);
    }

    public String getTime() {
        return time.getText().toString();
    }

    private void setTime(long time, String id) {

        if(id.equals("prt")){
            calculate = false;
            if(time == 0){
                this.time.setText("Running");
                this.time.setBackgroundResource(R.drawable.time_background_ok);
            }else{
                this.time.setText("Down");
                this.time.setBackgroundResource(R.drawable.time_background_bad);
            }
        }else{
            calculate = true;
            if(timeThread == null) {
                timeThread = new TimeThread(time);
                timeThread.start();
            }else{
                timeThread.setTime(time);
                Message msg = timeThread.getTimeString();
                this.time.setText(msg.getData().getString("timeString"));
                this.time.setBackgroundResource(msg.getData().getInt("colorId"));
            }
        }
    }

    public String getStatus() {
        return status.getText().toString();
    }

    public void setStatus(String status) {
        this.status.setText(status);
    }

    static class TimeHandler extends Handler{
        StatusView sv;
        public TimeHandler(StatusView sv) {
            this.sv = sv;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            sv.time.setText(data.getString("timeString"));
            sv.time.setBackgroundResource(data.getInt("colorId"));
        }
    }

    class TimeThread extends Thread{
        int time;
        public TimeThread(long time) {
            this.time = (int) ((System.currentTimeMillis() - time) / 1000);
            Log.d("mbt", "time diff = " + this.time);
            Log.d("mbt", "currenttime = " + System.currentTimeMillis());
        }

        public void setTime(long time){
            this.time = (int) ((System.currentTimeMillis() - time) / 1000);
        }

        public Message getTimeString(){
            Message msg = new Message();
            String timeString;
            int colorId;
            if(time<60){
                timeString = time + " sec";
                colorId = R.drawable.time_background_ok;
            }else if(time < 300){
                int min = time / 60;
                int sec = time - (60 * min);
                timeString = String.format(Locale.ENGLISH, "%d:%02d min", min, sec);
                colorId = R.drawable.time_background_good;
            }else if(time < 2700){
                timeString = (time/60) + " min";
                colorId = R.drawable.time_background_caution;
            }else{
                timeString = "> 45 min";
                colorId = R.drawable.time_background_bad;
            }
            Bundle b = new Bundle();
            b.putString("timeString", timeString);
            b.putInt("colorId", colorId);
            msg.setData(b);
            return msg;
        }

        public void run(){
            while(StatusView.this.getVisibility() == View.VISIBLE && calculate){
                StatusView.this.timeHandler.sendMessage(this.getTimeString());
                this.time ++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void setLocation(Bus.Location location, String id){
        this.setStatus(location.getDesc());
        if(id.equals("prt")){
            this.setNumber("");
            this.setTime(location.getBus(), id);
        }else{
            this.setNumber("Bus " + location.getBus());
            this.setTime(location.getTime(), id);
        }
    }
}
