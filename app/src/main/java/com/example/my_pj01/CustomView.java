package com.example.my_pj01;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.my_pj01.Models.BTLE_Device;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class CustomView extends View {
    private int numColumns, numRows;
    private int cellWidth, cellHeight;
    private Paint blackPaint = new Paint();
    private boolean[][] cellChecked;

    private Rect mRectsquare;
    private Paint mPaintsquare;
    float R1, R2, R3 = 1;
    Handler h;
    public CustomView(Context context) {
        super(context);
        h = new Handler();
        init(null);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        init(attrs);

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    public void setNumColumns(int numColumns) {
        this.numColumns = numColumns;
        calculateDimensions();
    }

    public int getNumColumns() {
        return numColumns;
    }

    public void setNumRows(int numRows) {
        this.numRows = numRows;
        calculateDimensions();
    }

    public int getNumRows() {
        return numRows;
    }

    private void init(@Nullable AttributeSet set) {
        mRectsquare = new Rect();
        mPaintsquare = new Paint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateDimensions();
    }

    private void calculateDimensions() {
        if (numColumns < 1 || numRows < 1) {
            return;
        }

        cellWidth = getWidth() / numColumns;
        cellHeight = getHeight() / numRows;

        cellChecked = new boolean[numColumns][numRows];

       // invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int column = (int)(event.getX() / cellWidth);
            int row = (int)(event.getY() / cellHeight);

            cellChecked[column][row] = !cellChecked[column][row];
            //invalidate();
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Set set = MapActivity.mBTDevicesHashMap.entrySet();
        // Get an iterator
        Iterator i = set.iterator();

        // Display elements
        while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();
            if(me.getKey().equals("Beacon_1")) {
                int rssi1 = ((BTLE_Device)me.getValue()).getRSSI();
                String name = ((BTLE_Device)me.getValue()).getName();
                String addr = ((BTLE_Device)me.getValue()).getAddress();
                R1 = DistanceCalculate(rssi1,-70);
                Log.d("BAS_TEST" , "---------->" + name + "  Distance" +  DistanceCalculate(rssi1,-70) + "  RSSI------>" + rssi1 + "  " +addr);
                //rssi = 0;
            }
            else if(me.getKey().equals("Beacon_2")){
                int rssi2 = ((BTLE_Device)me.getValue()).getRSSI();
                String name = ((BTLE_Device)me.getValue()).getName();
                String addr = ((BTLE_Device)me.getValue()).getAddress();
                R2 = DistanceCalculate(rssi2,-70);
                Log.d("BAS_TEST" , "---------->" + name + "  Distance" +  DistanceCalculate(rssi2,-70) + "  RSSI------>" + rssi2 + "  " +addr);
                //rssi = 0;
            }
            else if(me.getKey().equals("Beacon_3")) {
                int rssi3 = ((BTLE_Device)me.getValue()).getRSSI();
                String name = ((BTLE_Device)me.getValue()).getName();
                String addr = ((BTLE_Device)me.getValue()).getAddress();
                R3 = DistanceCalculate(rssi3,-70);
                Log.d("BAS_TEST" , "---------->" + name + "  Distance" +  DistanceCalculate(rssi3,-70) + "  RSSI------>" + rssi3 + "  " +addr);
                //rssi = 0;
            }
        }

        DrawGrid(canvas);
        DrawMap(canvas);
        DrawBeacon(canvas);
        DrawPosition(canvas);

        mPaintsquare.reset();

        postInvalidateDelayed(100);
        //invalidate();
        //h.postDelayed(runnable,100);
    }

    private void DrawMap(Canvas canvas){
        mPaintsquare.reset();
        mPaintsquare.setColor(getResources().getColor(android.R.color.darker_gray));
        mPaintsquare.setStrokeWidth(5);
        mPaintsquare.setStyle(Paint.Style.STROKE);
        mPaintsquare.setAntiAlias(true);

        mRectsquare.left = 20;
        mRectsquare.top = 20;
        mRectsquare.right = mRectsquare.left + 950;
        mRectsquare.bottom = mRectsquare.top + 1920;

        canvas.drawRect(mRectsquare,mPaintsquare);
    }

    private void DrawGrid(Canvas canvas){
        if (numColumns == 0 || numRows == 0) {
            return;
        }

        int width = getWidth();
        int height = getHeight();

        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                if (cellChecked[i][j]) {

                    canvas.drawRect(i * cellWidth, j * cellHeight,
                            (i + 1) * cellWidth, (j + 1) * cellHeight,
                            blackPaint);
                }
            }
        }

        for (int i = 1; i < numColumns; i++) {
            canvas.drawLine(i * cellWidth, 0, i * cellWidth, height, blackPaint);
        }

        for (int i = 1; i < numRows; i++) {
            canvas.drawLine(0, i * cellHeight, width, i * cellHeight, blackPaint);
        }
    }

    private void DrawBeacon(Canvas canvas){
        mPaintsquare.reset();
        mPaintsquare.setColor(Color.parseColor("#DD4F84FD"));
        mPaintsquare.setStrokeWidth(5);
        mPaintsquare.setTextSize(30);
        mPaintsquare.setStyle(Paint.Style.FILL);
        mPaintsquare.setAntiAlias(true);

        int R = 20;
        //old dist = 633
        double dist_real = 633;

        canvas.drawCircle(500,30,R1*(float)dist_real,mPaintsquare);
        canvas.drawCircle(30,950,R2*(float)dist_real,mPaintsquare);
        canvas.drawCircle(970,950,R3*(float)dist_real,mPaintsquare);

        canvas.drawText("Beacon 1",430,90,mPaintsquare);
        canvas.drawText("Beacon 2",20,1000,mPaintsquare);
        canvas.drawText("Beacon 3",860,1000,mPaintsquare);
    }

    private void DrawPosition(Canvas canvas){
        mPaintsquare.reset();
        mPaintsquare.setColor(getResources().getColor(android.R.color.holo_purple));
        mPaintsquare.setStrokeWidth(5);
        mPaintsquare.setTextSize(30);
        mPaintsquare.setStyle(Paint.Style.FILL);
        mPaintsquare.setAntiAlias(true);

        int R = 20;

        float cy = R1*633;
        float cx = (R2*633) - (R3*633);
        canvas.drawCircle(cx,cy,R,mPaintsquare);
    }

    private float DistanceCalculate (int rssi, int txPower){
        int x = txPower - rssi;
        float y = x/100.0f;
        double d = Math.pow(10,y);
        return (float)d;
    }

    private float PointUserCalculate () {

        return 1;
    }
}