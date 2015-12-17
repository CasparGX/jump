package com.casparx.game.jump;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.jumper)
    ImageView jumper;
    private long time;
    private float mt;
    private int g = 10;
    private int screenWidth;
    private int screenHeight;

    private boolean isDown;
    private boolean isRunning;
    private static final int JUMP = 0;
    private static final int DOWN = 1;

    private Thread jumpThread;
    private Thread downThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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
            jumper.setY(screenHeight - 500);
            jump(time);
        }

        return super.dispatchTouchEvent(ev);
    }

    private void jump(long time) {
        jumpThread = new JumpThread();
        downThread = new DownThread();
        isRunning = true;
        jumpThread.start();
    }

    class JumpThread extends Thread {
        @Override
        public void run() {
            while (time > 0 && isRunning) {
                time -= g * mt;
                mt += 1.5f;
                Message msg = new Message();
                msg.what = JUMP;
                handler.sendMessage(msg);
                try {
                    this.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            downThread.start();
        }
    }

    class DownThread extends Thread {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void run() {
            while (jumper.getY() < (screenHeight - 500)) {
                time += g * mt;
                mt -= 1.5f;
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
            float temp = time / 10 > mt ? time / 10 - mt : time / 15;
            if (msg.what == JUMP) {
                jumper.setY(jumper.getY() - temp);
                if (jumper.getY() < 0) {
                    Log.i("handler", "over top");
                    isRunning = false;
                    jumpThread.interrupt();
                } else if (jumper.getY() + jumper.getHeight() < 300) {

                    Log.i("handler", "great");
                }
            } else if (msg.what == DOWN) {
                jumper.setY(jumper.getY() + temp);
                if (jumper.getY() < 0) {
                    Log.i("handler", "over top");
                } else if (jumper.getY() + jumper.getHeight() < 300) {

                    Log.i("handler", "great");
                }
            }
        }
    };
}
