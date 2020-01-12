package com.example.my_pj01;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class CustomView extends View {

    public CustomView(Context context) {
        super(context);
        init(null);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }
    private void init(@Nullable AttributeSet set) {

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();

        //int radius = (int)(Math.random()*900);
        int whichColor = (int)(Math.random()*4);
        if(whichColor == 0) paint.setColor(Color.parseColor("#90008577"));
        else if (whichColor == 1) paint.setColor(Color.parseColor("#92175700"));
        else if (whichColor == 2) paint.setColor(Color.parseColor("#80D88C1B"));
        else  paint.setColor(Color.parseColor("#9AD81B1B"));

        canvas.drawCircle(1300,1300,(int)(Math.random()*900),paint);
        canvas.drawCircle(700,100,(int)(Math.random()*900),paint);
        canvas.drawCircle(100,1400,(int)(Math.random()*900),paint);
        postInvalidateDelayed(300);
        //invalidate();
    }
}