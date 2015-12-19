package com.casparx.game.jump.gameview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by root on 15-12-18.
 */
public class RailView extends View {
    public int getDefaultTop() {
        return defaultTop;
    }

    public void setDefaultTop(int defaultTop) {
        this.defaultTop = defaultTop;
    }

    private int defaultTop = 300;

    public int getDefaultNextTop() {
        return defaultNextTop;
    }

    private int defaultNextTop = 500;
    private int x = 300;
    public RailView(Context context) {
        super(context);
    }

    public RailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RailView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RailView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public float getX() {
        return x;
    }

    public void setX(int top) {
        this.x = top;
    }
}
