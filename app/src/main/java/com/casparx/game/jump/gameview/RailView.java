package com.casparx.game.jump.gameview;

import android.content.Context;
import android.view.View;

/**
 * Created by root on 15-12-18.
 */
public class RailView extends View {
    private int defaultTop = 300;
    private int x = 300;
    public RailView(Context context) {
        super(context);
    }

    public float getX() {
        return x;
    }

    public void setX(int top) {
        this.x = top;
    }
}
