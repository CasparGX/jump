package com.casparx.game.jump.gameview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by root on 15-12-19.
 */
public class JumperView extends ImageView {

    private float v;

    public JumperView(Context context) {
        super(context);
    }

    public JumperView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JumperView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public JumperView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
