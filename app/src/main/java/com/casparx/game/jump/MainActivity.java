package com.casparx.game.jump;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity{

    private ImageView jumper;
    private long time;
    private float mt;
    private int g = 10;
    private int screenWidth;
    private int screenHeight;

    private static final int JUMP = 0;
    private static final int DOWN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        jumper = (ImageView) findViewById(R.id.jumper);
        init();
    }

    private void init() {
        //get screen's width 1/4
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetris = new DisplayMetrics();
        display.getMetrics(outMetris);
        screenWidth = outMetris.widthPixels;
        screenHeight = outMetris.heightPixels;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        //long time;

        if (ev.getAction() == MotionEvent.ACTION_UP) {
            time = ev.getEventTime() - ev.getDownTime();
            mt = 0;
            jumper.setY(screenHeight-500);
            jump(time);
        }

        return super.dispatchTouchEvent(ev);
    }

    private void jump(long time) {
        Thread t = new JumpThread();
        t.start();
    }

    class JumpThread extends Thread {
        @Override
        public void run() {
            while (time>0) {
                time -= g*mt;
                mt+=1.5f;
                Message msg = new Message();
                msg.what = JUMP;
                handler.sendMessage(msg);
                try {
                    this.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Thread t = new DownThread();
            t.start();
        }
    }

    class DownThread extends Thread {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void run() {
            while (jumper.getY()<(screenHeight-500)) {
                time += g*mt;
                mt-=1.5f;
                Message msg = new Message();
                msg.what = DOWN;
                handler.sendMessage(msg);
                try {
                    this.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Handler handler = new Handler() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void handleMessage(Message msg) {
            float temp = time/10>mt ? time/10-mt : time/15;
            if (msg.what == JUMP) {
                jumper.setY(jumper.getY() - temp);
                if (jumper.getY()<0) {
                    Log.i("handler","over top");
                } else if (jumper.getY()+jumper.getHeight()<300) {

                    Log.i("handler","great");
                }
            } else if (msg.what == DOWN) {
                jumper.setY(jumper.getY() + temp);
                if (jumper.getY()<0) {
                    Log.i("handler","over top");
                } else if (jumper.getY()+jumper.getHeight()<300) {

                    Log.i("handler","great");
                }
            }
        }
    };
}