package com.casparx.game.jump;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.casparx.game.jump.gameview.JumperView;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends Activity {

    @Bind(R.id.line)
    ImageView line;
    @Bind(R.id.jumper)
    JumperView jumper;
    private long time;
    private float mt;
    private int g = 10;
    private int screenWidth;
    private int screenHeight;
    private int nextTop = 500;

    private float x;

    private boolean isRunning;
    private boolean isEnd;
    private boolean isDown;
    private int isSuccess;

    private static final int END = 0;
    private static final int NEXT = 1;
    private static final int FALL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        jumper = (JumperView) findViewById(R.id.jumper);
        init();
        initGame();
    }

    private void initGame() {
        isDown = false;
        isRunning = false;
        isEnd = false;
        isSuccess = 0;
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

        if (ev.getAction() == MotionEvent.ACTION_UP && !isRunning) {
            time = ev.getEventTime() - ev.getDownTime();
            mt = 0;
            jumper.setY(screenHeight - 500);
            jump(time);
        }

        return super.dispatchTouchEvent(ev);
    }

    private void jump(long time) {
        FallThread2 fallThread = new FallThread2();
        isRunning = true;
        nextTop = line.getTop();
        Log.i("nextTop", nextTop + "");
        fallThread.start();
    }

    class FallThread2 extends Thread {
        float x1 = 0;
        float x2 = 0;
        float s = 0;
        int t = 0;
        @Override
        public void run() {
            while (s>=0) {
                if (isEnd) {
                    endGame();
                    this.interrupt();
                    break;
                } else if (isSuccess == 1) {
                    s -= screenHeight - nextTop - 300;
                    Log.i("isSuccess", "S:" + s);
                }
                t++;
                x2 = x1;
                x1 = time*t/8 - g*t*t/2;
                x = x1-x2;
                if (x < 0 || x == 0) isDown = true;
                else isDown = false;
                s+=x;
                if (s < 0) {
                    x -= s;
                    if (isSuccess > 0) {
                        Message msg2 = new Message();
                        msg2.what = NEXT;
                        handler.sendMessage(msg2);
                    }
                    endGame();
                }
                Message msg = new Message();
                msg.what = FALL;
                handler.sendMessage(msg);
                try {
                    this.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class FallThread extends Thread {
        float tempTime = (time / (g / 2f)) * 2f;
        int t = (int) tempTime / g - 1;
        float s = 0;

        @Override
        public void run() {
            //tempTime = time/100;
            //t=0;
            while (s > 0 || s == 0) {
                if (isEnd) {
                    endGame();
                    this.interrupt();
                    break;
                } else if (isSuccess == 1) {
                    s -= screenHeight - nextTop - 300;
                    Log.i("isSuccess", "S:" + s);
                }
                t++;
                x = (tempTime * t - g * t * t / 2f) / 30f;
                if (x < 0 || x == 0) isDown = true;
                else isDown = false;
                s += x;
                Log.i("FallThread", "time:" + tempTime + " s:" + s + " x:" + x + " t:" + t);
                if (s < 0) {
                    x -= s;
                    if (isSuccess > 0) {
                        Message msg2 = new Message();
                        msg2.what = NEXT;
                        handler.sendMessage(msg2);
                    }
                    endGame();
                }
                Message msg = new Message();
                msg.what = FALL;
                handler.sendMessage(msg);
                try {
                    this.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void nextLevel() {
        nextTop = Math.random() > 0.5 ? (int) (nextTop + Math.random() * 50) : (int) (nextTop - Math.random() * 50);
        line.setY(nextTop);
    }

    private void endGame() {
        isSuccess = 0;
        isRunning = false;
        isEnd = false;
        Message msg = new Message();
        msg.what = END;
        handler.sendMessage(msg);
    }

    private Handler handler = new Handler() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FALL) {
                jumper.setY(jumper.getY() - x);
                if (jumper.getY() < 0) {
                    isEnd = true;
                } else if (isDown && (jumper.getY() + jumper.getHeight() < nextTop)) {
                    isSuccess++;
                    Log.i("handler", "great");
                }
            } else if (msg.what == NEXT) {
                nextLevel();
            } else if (msg.what == END) {
                jumper.setY(screenHeight-500);
            }
        }
    };
}