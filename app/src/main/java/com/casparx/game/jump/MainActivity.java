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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.casparx.game.jump.gameview.JumperView;
import com.casparx.game.jump.gameview.RailView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

    @Bind(R.id.jumper)
    JumperView jumper;
    @Bind(R.id.line_start)
    RailView lineStart;
    @Bind(R.id.line_next)
    RailView lineNext;
    @Bind(R.id.start_game)
    TextView startGame;
    @Bind(R.id.score)
    TextView tvScore;
    private long time;
    private float mt;
    private int g = 10;
    private int screenWidth;
    private int screenHeight;
    private float nextTop;
    private float nextRailY;
    private float railsDistance;

    private float x;

    private boolean isRunning;
    private boolean isEnd;
    private boolean isDown;
    private int isSuccess;

    private int score;

    private static final int END = 0;
    private static final int NEXT = 1;
    private static final int FALL = 2;
    private static final int NEXT_RAIL = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        init();
        initGame();
    }

    @OnClick(R.id.start_game)
    void startGame() {
        lineStart.setY(screenHeight - lineStart.getDefaultTop());
        jumper.setY(lineStart.getY() - jumper.getHeight());
        lineNext.setY(lineNext.getDefaultNextTop());
        nextTop = lineNext.getY();
        isRunning = false;
        startGame.setVisibility(View.GONE);
    }

    private void initGame() {
        isDown = false;
        isRunning = true;
        isEnd = false;
        isSuccess = 0;
        score = 0;
        startGame.setVisibility(View.VISIBLE);
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
            //jumper.setY(screenHeight - 500);
            jump(time);
            Log.i("123", "==============");
        }

        return super.dispatchTouchEvent(ev);
    }

    private void jump(long time) {
        FallThread2 fallThread = new FallThread2();
        isRunning = true;
        nextTop = lineNext.getY();
        Log.i("nextTop", nextTop + "");
        fallThread.start();
    }

    class FallThread2 extends Thread {
        float x1 = 0;
        float x2 = 0;
        float s = lineStart.getDefaultTop();
        int t = 0;

        @Override
        public void run() {
            while (s >= 0) {

                if (isEnd) {
                    endGame();
                    this.interrupt();
                    break;
                } else if (isSuccess == 1) {
                    s -= screenHeight - nextTop /*- lineStart.getDefaultTop()*2*/;
                    Log.i("isSuccess", "S:" + s);
                }

                t++;
                x2 = x1;
                x1 = time * t / 8 - g * t * t / 2;
                x = x1 - x2;
                if (x < 0 || x == 0) isDown = true;
                else isDown = false;

                s += x;
                if (s < 0) {
                    x -= s;
                    if (isSuccess > 0) {
                        nextRail();
                    }
                    endGame();
                } else {
                    Message msg = new Message();
                    msg.what = FALL;
                    handler.sendMessage(msg);
                }

                try {
                    this.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void nextRail() {
            Log.i("debug", "nextRail");
            nextRailY = lineNext.getY();
            railsDistance = lineStart.getY() - lineNext.getY();
            while (nextRailY < screenHeight - lineStart.getDefaultTop()) {
                nextRailY += 20;
                Log.i("nextRail", nextRailY + "");
                if (nextRailY > screenHeight - lineStart.getDefaultTop()) {
                    nextRailY = screenHeight - lineStart.getDefaultTop();
                    Log.i("nextRail---", nextRailY + "");
                }
                Message msg = new Message();
                msg.what = NEXT_RAIL;
                handler.sendMessage(msg);

                try {
                    this.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Message msg2 = new Message();
            msg2.what = NEXT;
            handler.sendMessage(msg2);
        }
    }

    /*class FallThread extends Thread {
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
    }*/

    private void nextLevel() {
        Log.i("debug", "nextLevel");
        nextTop = Math.random() > 0.5 ? (int) (lineNext.getDefaultNextTop() + Math.random() * 50) : (int) (lineNext.getDefaultNextTop() - Math.random() * 50);
        lineStart.setY(screenHeight - lineStart.getDefaultTop());
        lineNext.setY(nextTop);
        score++;
        endGame();
    }

    private void endGame() {
        Log.i("debug", "endGame");
        isSuccess = 0;
        isRunning = false;
        if (isEnd) {
        } else {
            Message msg = new Message();
            msg.what = END;
            handler.sendMessage(msg);
        }
        isEnd = false;
    }

    private Handler handler = new Handler() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FALL) {
                jumper.setY(jumper.getY() - x);
                if (jumper.getY() < 0) {
                    initGame();
                    isEnd = true;
                } else if (jumper.getY() >= screenHeight - jumper.getHeight() - lineStart.getDefaultTop()) {
                    initGame();
                    isEnd = true;
                } else if (isDown && (jumper.getY() + jumper.getHeight() < nextTop)) {
                    isSuccess++;
                    Log.i("handler", "great" + nextTop + " " + lineNext.getY());
                }
            } else if (msg.what == NEXT) {
                nextLevel();
            } else if (msg.what == END) {
                tvScore.setText(score+"");
                jumper.setY(screenHeight - lineStart.getDefaultTop() - jumper.getHeight());
            } else if (msg.what == NEXT_RAIL) {
                lineNext.setY(nextRailY);
                lineStart.setY(nextRailY + railsDistance);
                jumper.setY(lineNext.getY() - jumper.getHeight());
            }
        }
    };
}