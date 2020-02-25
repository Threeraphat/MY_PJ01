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
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.example.my_pj01.Models.BTLE_Device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class CustomView extends View {
    private int numColumns, numRows;
    private int cellWidth, cellHeight;
    private Paint blackPaint = new Paint();
    private boolean[][] cellChecked;
    private Rect mRectsquare;
    private Paint mPaintsquare;
    ArrayList<Integer> RSSIAverage_1 = new ArrayList();
    ArrayList<Integer> RSSIAverage_2 = new ArrayList();
    ArrayList<Integer> RSSIAverage_3 = new ArrayList();
    int R1_sum, R2_sum, R3_sum = 0;
    float R1, R2, R3 = 0;
    ///////////////////////////////////////////////////
    //  ARM ROOM
    //y = 6.0 M
    //x = 3.9 M
    //Beacon_1 x = 1.95f y = 0
    //Beacon_2 x = 0 y = 3f
    //Beacon_3 x = 3.9f y = 3f

    //float x_1 = 1.95f, x_2 = 0 , x_3 = 3.90f;
    //float y_1 = 0, y_2 = 3.00f, y_3 = 3.00f;
    ///////////////////////////////////////////////////

    //  TABLE 150 x 180 (x,y)
    //  900 Pixel / 1.5 m(150cm)  = 600 (X)
    //  900 Pixel / 1.8 m(180cm) = 500 (Y)
    float x_1 = 0.75f, x_2 = 0, x_3 = 1.5f;
    float y_1 = 0, y_2 = 0.9f, y_3 = 0.9f;

    //////////////////////////////////////////////
    // ROOM MNR
    // y = 900  y/2 = 450 900/9 = 100
    // x = 600   x/2 = 300  900/6 = 150
    // x_1 = 2.0f, x_2 = 0, x_3 = 4.0f;
    //float y_1 = 0, y_2 = 5.4f, y_3 = 5.4f;
    double _x = 0, _y = 0;
    double r = 0;

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
            int column = (int) (event.getX() / cellWidth);
            int row = (int) (event.getY() / cellHeight);
            cellChecked[column][row] = !cellChecked[column][row];
            //invalidate();
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setNumColumns(30);
        setNumRows(30);
        ReceiveBeacon();
        DrawGrid(canvas);
        DrawMap(canvas);
        DrawBeacon(canvas);
        PointUserCalculate(canvas);
        mPaintsquare.reset();

        postInvalidateDelayed(50);
    }

    int rssi1 = 0;
    int rssi2 = 0;
    int rssi3 = 0;
    int measuredPower = -65; ////hard coded power value. Usually ranges between -59 to -65
    private void ReceiveBeacon() {
        Set set = IndexActivity.mBTDevicesHashMap.entrySet();
        // Get an iterator
        Iterator i = set.iterator();

        // Display elements
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            if (me.getKey().equals("Beacon_1")) {
                rssi1 = ((BTLE_Device) me.getValue()).getRSSI();
                String name = ((BTLE_Device) me.getValue()).getName();
                String addr = ((BTLE_Device) me.getValue()).getAddress();
                RSSIAverage_1.addAll(Collections.singleton(rssi1));
                if (RSSIAverage_1.size() > 3) {
                    R1_sum = AverageRSSI(RSSIAverage_1); //R1_avg
                    R1 = DistanceCalculate(R1_sum, measuredPower); //D1
                    if(R1>1.8f) R1 = 1.8f;
                    RSSIAverage_1.clear();
                }

                Log.d("BAS_TEST", "--------> RSSIAverage1 " + R1_sum);
                Log.d("BAS_TEST", "---------->" + name + "  Distance" + DistanceCalculate(rssi1, measuredPower) + "  RSSI------>" + rssi1 + "  " + addr);
            } else if (me.getKey().equals("Beacon_2")) {
                rssi2 = ((BTLE_Device) me.getValue()).getRSSI();
                String name = ((BTLE_Device) me.getValue()).getName();
                String addr = ((BTLE_Device) me.getValue()).getAddress();
                RSSIAverage_2.addAll(Collections.singleton(rssi2));
                if (RSSIAverage_2.size() > 3) {
                    R2_sum = AverageRSSI(RSSIAverage_2);
                    R2 = DistanceCalculate(R2_sum, measuredPower);
                    if(R2>1.5f) R2 = 1.5f;
                    RSSIAverage_2.clear();
                }

                Log.d("BAS_TEST", "--------> RSSIAverage2 " + R2_sum);
                Log.d("BAS_TEST", "---------->" + name + "  Distance" + DistanceCalculate(rssi2, measuredPower) + "  RSSI------>" + rssi2 + "  " + addr);
            } else if (me.getKey().equals("Beacon_3")) {
                rssi3 = ((BTLE_Device) me.getValue()).getRSSI();
                String name = ((BTLE_Device) me.getValue()).getName();
                String addr = ((BTLE_Device) me.getValue()).getAddress();
                RSSIAverage_3.addAll(Collections.singleton(rssi3));
                if (RSSIAverage_3.size() > 3) {
                    R3_sum = AverageRSSI(RSSIAverage_3);
                    R3 = DistanceCalculate(R3_sum, measuredPower);
                    if(R3>1.5f) R3 = 1.5f;
                    RSSIAverage_3.clear();
                }

                Log.d("BAS_TEST", "--------> RSSIAverage3 " + R3_sum);
                Log.d("BAS_TEST", "---------->" + name + "  Distance" + DistanceCalculate(rssi3, measuredPower) + "  RSSI------>" + rssi3 + "  " + addr);
            }
        }
    }

    private void DrawMap(Canvas canvas) {
        mPaintsquare.reset();
        mPaintsquare.setColor(getResources().getColor(android.R.color.darker_gray));
        mPaintsquare.setStrokeWidth(15);
        mPaintsquare.setStyle(Paint.Style.FILL);
        mPaintsquare.setAntiAlias(true);

        if(numColumns == 0 || numRows == 0) {
            return;
        }

        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {


            }
        }
    }

    private void DrawGrid(Canvas canvas) {
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

            for (int _Col = 0; _Col < numColumns - 1; _Col++) {
                if (_Col > 0 && _Col < numColumns - 1) {

                }
                for (int _Row = 0; _Row < numRows - 1; _Row++) {

                }
            }
        }

        for (int i = 1; i < numRows; i++) {
            canvas.drawLine(0, i * cellHeight, width, i * cellHeight, blackPaint);
        }
    }

    private void DrawBeacon(Canvas canvas) {
        mPaintsquare.reset();
        mPaintsquare.setTextSize(30);
        mPaintsquare.setAntiAlias(true);

        if (numColumns == 0 || numRows == 0) {
            return;
        }

        int R = 20;
        //old dist = 633
        double dist_real = 500;


        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                if (i == 0 && j > 13 && j < 16){
                    mPaintsquare.setStyle(Paint.Style.FILL);
                    mPaintsquare.setColor(getResources().getColor(android.R.color.holo_red_light));
                    canvas.drawRect(i * cellWidth, j * cellHeight, (i + 1) * cellWidth, (j + 1) * cellHeight, mPaintsquare);
                    if(j == 14) {
                        canvas.drawText("Beacon 2", (i+1) * cellWidth, (j + 1) * cellHeight, mPaintsquare);
                    }
                }
                else if(i > 13 && i < 16 && j == 0){
                    mPaintsquare.setStyle(Paint.Style.FILL);
                    mPaintsquare.setColor(getResources().getColor(android.R.color.holo_green_light));
                    canvas.drawRect(i * cellWidth, j * cellHeight, (i + 1) * cellWidth, (j + 1) * cellHeight, mPaintsquare);
                    if(i == 14) {
                        canvas.drawText("Beacon 1", (i-1) * cellWidth, (j + 2) * cellHeight, mPaintsquare);
                    }
                }
                else if(i == 29 && j > 13 && j < 16){
                    mPaintsquare.setStyle(Paint.Style.FILL);
                    mPaintsquare.setColor(getResources().getColor(android.R.color.holo_blue_light));
                    canvas.drawRect(i * cellWidth, j * cellHeight, (i + 1) * cellWidth, (j + 1) * cellHeight, mPaintsquare);
                    if(j == 15) {
                        canvas.drawText("Beacon 3", (i-4) * cellWidth, (j) * cellHeight, mPaintsquare);
                    }
                }
            }
        }
        mPaintsquare.setStyle(Paint.Style.STROKE);
        mPaintsquare.setColor(Color.parseColor("#E95E13CF"));
        mPaintsquare.setStrokeWidth(5);
        canvas.drawCircle((numColumns / 2 )* cellWidth, 0 * cellHeight, R1 * (float) dist_real, mPaintsquare);
        canvas.drawCircle(0 * cellWidth,  (numRows / 2 )* cellHeight, R2 * (float) dist_real, mPaintsquare);
        canvas.drawCircle(numColumns * cellWidth, (numRows / 2 )* cellHeight, R3 * (float) dist_real, mPaintsquare);
    }

    //10 ^ ((Measured Power – RSSI)/(10 * N))
    private float DistanceCalculate(int rssi, int txPower) {
        int x = txPower - rssi;
        float y = x / 30f; //n * 3
        double d = Math.pow(10, y);
        return (float) d;
    }

    private void PointUserCalculate(Canvas canvas) {

        mPaintsquare.reset();
        mPaintsquare.setColor(getResources().getColor(android.R.color.holo_green_light));
        mPaintsquare.setStrokeWidth(5);
        mPaintsquare.setTextSize(30);
        mPaintsquare.setStyle(Paint.Style.FILL);
        mPaintsquare.setAntiAlias(true);

        canvas.drawText("MapScale_Width : " + getWidth() , 50, 850, mPaintsquare);
        canvas.drawText("MapScale_Height  : " + getHeight() , 50, 950, mPaintsquare);
        canvas.drawText("beacon1 : " + rssi1 , 50, 1050, mPaintsquare);
        canvas.drawText("beacon2 : " + rssi2, 50, 1150, mPaintsquare);
        canvas.drawText("beacon3 : " + rssi3, 50, 1250, mPaintsquare);
        canvas.drawText("beacon1-R1 : " + R1 , 50, 1350, mPaintsquare);
        canvas.drawText("beacon2-R2 : " + R2, 50, 1450, mPaintsquare);
        canvas.drawText("beacon3-R3 : " + R3, 50, 1550, mPaintsquare);

        //canvas.drawText("Y_1 : " + y_1, 50, 1350, mPaintsquare);
        //canvas.drawText("Y_2 : " + y_2, 50, 1450, mPaintsquare);
        //canvas.drawText("Y_3 : " + y_3, 50, 1550, mPaintsquare);

        //Find constant of circle #2- cirlce #1
        //float K_a = -(R1*R1)+(c)+(x_1*x_1)-(x_2*x_2)+(y_1*y_1)-(y_2*y_2);
        float K_a = (R1 * R1) - (R2 * R2) - (x_1 * x_1) + (x_2 * x_2) - (y_1 * y_1) + (y_2 * y_2);

        //Find constant of circle #3- cirlce #1
        //float K_b = -(R1*R1)+(R3*R3)+(x_1*x_1)-(x_3*x_3)+(y_1*y_1)-(y_3*y_3);
        float K_b = (R1 * R1) - (R3 * R3) - (x_1 * x_1) + (x_3 * x_3) - (y_1 * y_1) + (y_3 * y_3);

        //Find constants of [x=A_0+A_1*r, y=B_0+B_1*r]
        float D = (x_1 * (y_2 - y_3)) + (x_2 * (y_3 - y_1)) + (x_3 * (y_1 - y_2));
        float A_0 = ((K_a * (y_1 - y_3)) + (K_b * (y_2 - y_1))) / (2 * D);
        float B_0 = -1 * ((K_a * (x_1 - x_3)) + (K_b * (x_2 - x_1))) / (2 * D);
        float A_1 = -1 * ((R1 * (y_2 - y_3)) + (R2 * (y_3 - y_1)) + (R3 * (y_1 - y_2))) / D;
        float B_1 = ((R1 * (x_2 - x_3)) + (R2 * (x_3 - x_1)) + (R3 * (x_1 - x_2))) / D;

        //Find constants of C_0 + 2*C_1*r + C_2^2 = 0
        /*double C_0 = Math.pow(A_0 - x_1,2)+Math.pow(B_0-y_1,2)-Math.pow(R1,2);
        double C_1 = A_1*(A_0-x_1) + B_1*(B_0-y_1)-R1;
        double C_2 = Math.pow(A_1,2)+ Math.pow(B_1,2) - 1;*/

        //float C_0=(A_0*A_0)-2*A_0*x_1+(B_0*B_0)-2*B_0*y_1-(R1*R1)+(x_1*x_1)+(y_1*y_1);
        //float C_1=A_0*A_1-A_1*x_1+B_0*B_1-B_1*y_1-R1;
        float C_0 = (((A_0 - x_1) * (A_0 - x_1)) + (B_0 - y_1) * (B_0 - y_1)) - (R1 * R1);
        float C_1 = (A_1 * (A_0 - x_1)) + (B_1 * (B_0 - y_1)) - R1;
        float C_2 = (A_1 * A_1) + (B_1 * B_1) - 1;

        //Solve for r
        r = ((-1 * C_1) + (Math.sqrt(Math.pow(C_1, 2) - (C_0 * C_2)))) / C_2;

        //Solve for [x,y]
        _x = A_0 + A_1 * r;
        _y = B_0 + B_1 * r;

        if(_x < 0) _x *= -1;
        if(_y < 0) _y *= -1;

        //Red dot
        canvas.drawCircle((float) (_x) * 500, (float) (_y) * 600, 25, mPaintsquare);
        Log.d("redDot", "---------->" + _x + "  " + _y);

        canvas.drawText("x : " + _x, 50, 1650, mPaintsquare);
        canvas.drawText("y : " + _y, 50, 1750, mPaintsquare);
    }

    private int AverageRSSI(List<Integer> RSSI_list) {
        Integer sum = 0;
        if (!RSSI_list.isEmpty()) {
            for (Integer rssi : RSSI_list) {
                sum += rssi;
            }
        }
        return sum / RSSI_list.size();
    }
}