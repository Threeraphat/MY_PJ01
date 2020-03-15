package com.example.my_pj01;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.example.my_pj01.Models.BeaconModel;
import com.ichbingrumpig.pathfinder.OnPathFoundListener;
import com.ichbingrumpig.pathfinder.Pathfinder;
import com.ichbingrumpig.pathfinder.Settings;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Navigation extends View {
    private int numColumns, numRows;
    private int cellWidth, cellHeight;
    private Paint blackPaint = new Paint();
    private Rect mRectsquare;
    String _shelf;
    private Paint mPaintsquare;
    public static int X_Start = 0, Y_Start = 0;
    public static int X_Stop = 0, Y_Stop = 0;
    public double result;

    int[][] gameGrid;
    private static final int GRID_VALUE_SHELF = 1;
    private static final int GRID_VALUE_FREE = 0;
    private static final int GRID_VALUE_BOULDERS_STARTING_VALUE = 4;
    float R1, R2, R3 = 0;
    //////////////////////////////////////////////////
    //Q412
    // map  x pixel 1028
    // map y pixel 1339
    // x = 516 cm ( 5.16 m )
    // y = 607 cm ( 6.07 m )
    // 1028 / 5.16 = 199.2248 px per meter in px (X)
    // 1339 / 6.07 = 220.5930 px per meter in px (Y)
    float x_1 = 2.58f, x_2 = 0, x_3 = 5.16f;
    float y_1 = 0, y_2 = 3.035f, y_3 = 3.035f;

    //////////////////////////////////////////////
    // ROOM MNR
    // y = 900  y/2 = 450 900/9 = 100
    // x = 600   x/2 = 300  900/6 = 150
    // x_1 = 2.0f, x_2 = 0, x_3 = 4.0f;
    //float y_1 = 0, y_2 = 5.4f, y_3 = 5.4f;
    double _x = 0, _y = 0;
    double r = 0;

    public Navigation(Context context) {
        super(context);
        init(null);
    }

    public Navigation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        blackPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        init(attrs);
    }

    public Navigation(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public Navigation(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setNumColumns(30);
        setNumRows(30);
        ReceiveBeacon();
        DrawGridMap(canvas);
        DrawBeacon(canvas);
        PointUserCalculate(canvas);
        drawPath(canvas);
        drawShelf(canvas);
        postInvalidateDelayed(50);
    }

    int rssi1 = 0, rssi2 = 0, rssi3 = 0;
    int measuredPower = -70; ////hard coded power value. Usually ranges between -59 to -70

    private void ReceiveBeacon() {
        Set set = IndexActivity.mBTDevicesHashMap.entrySet();

        if (set == null) return;
        // Get an iterator
        Iterator i = set.iterator();

        // Display elements
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            if (me.getKey().equals("Beacon_1")) {
                rssi1 = ((BeaconModel) me.getValue()).getRSSI();
                R1 = DistanceCalculate(rssi1, measuredPower); //D1
                if (R1 > 3.035f) R1 = 3.035f;
            } else if (me.getKey().equals("Beacon_2")) {
                rssi2 = ((BeaconModel) me.getValue()).getRSSI();
                R2 = DistanceCalculate(rssi2, measuredPower);
                if (R2 > 2.575f) R2 = 2.575f;
            } else if (me.getKey().equals("Beacon_3")) {
                rssi3 = ((BeaconModel) me.getValue()).getRSSI();
                R3 = DistanceCalculate(rssi3, measuredPower);
                if (R3 > 2.575f) R3 = 2.575f;
            }
        }
    }

    private void DrawGridMap(Canvas canvas) {
        mPaintsquare.reset();
        mPaintsquare.setColor(getResources().getColor(android.R.color.darker_gray));
        mPaintsquare.setStrokeWidth(1);
        mPaintsquare.setStyle(Paint.Style.FILL);
        mPaintsquare.setAntiAlias(true);

        int width = getWidth();
        int height = getHeight();

        for (int i = 0; i < numColumns; i++) {
            if (i % 5 == 0) {
                canvas.drawLine(i * cellWidth, 0, i * cellWidth, height, blackPaint);
            } else {
                canvas.drawLine(i * cellWidth, 0, i * cellWidth, height, mPaintsquare);
            }
        }

        for (int i = 0; i < numRows; i++) {
            if (i % 5 == 0) {
                canvas.drawLine(0, i * cellHeight, width, i * cellHeight, blackPaint);
            } else {
                canvas.drawLine(0, i * cellHeight, width, i * cellHeight, mPaintsquare);
            }
        }
    }

    private void DrawBeacon(Canvas canvas) {
        mPaintsquare.reset();
        mPaintsquare.setTextSize(30);
        mPaintsquare.setAntiAlias(true);

        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                if (i == 0 && j > 13 && j < 16) {
                    mPaintsquare.setStyle(Paint.Style.FILL);
                    mPaintsquare.setColor(getResources().getColor(android.R.color.holo_red_light));
                    canvas.drawRect(i * cellWidth, j * cellHeight, (i + 1) * cellWidth, (j + 1) * cellHeight, mPaintsquare);
                    if (j == 14) {
                        canvas.drawText("Beacon 2", (i + 1) * cellWidth, (j + 1) * cellHeight, mPaintsquare);
                    }
                } else if (i > 13 && i < 16 && j == 0) {
                    mPaintsquare.setStyle(Paint.Style.FILL);
                    mPaintsquare.setColor(getResources().getColor(android.R.color.holo_green_light));
                    canvas.drawRect(i * cellWidth, j * cellHeight, (i + 1) * cellWidth, (j + 1) * cellHeight, mPaintsquare);
                    if (i == 14) {
                        canvas.drawText("Beacon 1", (i - 1) * cellWidth, (j + 2) * cellHeight, mPaintsquare);
                    }
                } else if (i == 29 && j > 13 && j < 16) {
                    mPaintsquare.setStyle(Paint.Style.FILL);
                    mPaintsquare.setColor(getResources().getColor(android.R.color.holo_blue_light));
                    canvas.drawRect(i * cellWidth, j * cellHeight, (i + 1) * cellWidth, (j + 1) * cellHeight, mPaintsquare);
                    if (j == 15) {
                        canvas.drawText("Beacon 3", (i - 4) * cellWidth, (j) * cellHeight, mPaintsquare);
                    }
                }
            }
        }
        mPaintsquare.setStyle(Paint.Style.STROKE);
        mPaintsquare.setColor(getResources().getColor(android.R.color.holo_green_light));
        mPaintsquare.setStrokeWidth(5);
        canvas.drawCircle((numColumns / 2) * cellWidth, 0 * cellHeight, R1 * 240.5930f, mPaintsquare); //220.5930f

        mPaintsquare.setStyle(Paint.Style.STROKE);
        mPaintsquare.setColor(getResources().getColor(android.R.color.holo_red_light));
        mPaintsquare.setStrokeWidth(5);
        canvas.drawCircle(0 * cellWidth, (numRows / 2) * cellHeight, R2 * 199.2248f, mPaintsquare);

        mPaintsquare.setStyle(Paint.Style.STROKE);
        mPaintsquare.setColor(getResources().getColor(android.R.color.holo_blue_light));
        mPaintsquare.setStrokeWidth(5);
        canvas.drawCircle(numColumns * cellWidth, (numRows / 2) * cellHeight, R3 * 199.2248f, mPaintsquare);
    }

    private void drawShelf(Canvas canvas) {
        mPaintsquare.reset();
        mPaintsquare.setTextSize(30);
        mPaintsquare.setAntiAlias(true);

        for (int x = 0; x < numColumns; x++) {
            for (int j = 0; j < numRows; j++) {
                if (x > 9 && x < 15 && j == 6) { ////Shelf 1
                    mPaintsquare.setStyle(Paint.Style.FILL);
                    mPaintsquare.setColor(getResources().getColor(android.R.color.darker_gray));
                    canvas.drawRect(x * cellWidth, j * cellHeight, (x + 1) * cellWidth, (j + 1) * cellHeight, mPaintsquare);
                    if (x == 10) {
                        mPaintsquare.setStyle(Paint.Style.FILL);
                        mPaintsquare.setColor(getResources().getColor(android.R.color.holo_blue_dark));
                        canvas.drawText("Shelf 1", (x + 1) * cellWidth, j * cellHeight, mPaintsquare);
                    }
                } else if (x > 9 && x < 15 && j == 7) {
                    mPaintsquare.setStyle(Paint.Style.FILL);
                    mPaintsquare.setColor(getResources().getColor(android.R.color.darker_gray));
                    canvas.drawRect(x * cellWidth, j * cellHeight, (x + 1) * cellWidth, (j + 1) * cellHeight, mPaintsquare);
                } else if (x > 19 && x < 25 && j == 6) { ////Shelf 2
                    mPaintsquare.setStyle(Paint.Style.FILL);
                    mPaintsquare.setColor(getResources().getColor(android.R.color.darker_gray));
                    canvas.drawRect(x * cellWidth, j * cellHeight, (x + 1) * cellWidth, (j + 1) * cellHeight, mPaintsquare);
                    if (x == 20) {
                        mPaintsquare.setStyle(Paint.Style.FILL);
                        mPaintsquare.setColor(getResources().getColor(android.R.color.holo_blue_dark));
                        canvas.drawText("Shelf 2", (x + 1) * cellWidth, j * cellHeight, mPaintsquare);
                    }
                } else if (x > 19 && x < 25 && j == 7) {
                    mPaintsquare.setStyle(Paint.Style.FILL);
                    mPaintsquare.setColor(getResources().getColor(android.R.color.darker_gray));
                    canvas.drawRect(x * cellWidth, j * cellHeight, (x + 1) * cellWidth, (j + 1) * cellHeight, mPaintsquare);
                }
            }
        }
    }

    //10 ^ ((Measured Power – RSSI)/(10 * N))
    private float DistanceCalculate(int rssi, int txPower) {
        int x = txPower - rssi;
        float y = x / 30f; //n * 3
        double d = Math.pow(10, y);
        return (float) d;
    }

    List<Integer> pos = new ArrayList<Integer>();
    private void PointUserCalculate(Canvas canvas) {
        mPaintsquare.reset();
        mPaintsquare.setColor(getResources().getColor(android.R.color.holo_green_light));
        mPaintsquare.setStrokeWidth(30);
        mPaintsquare.setTextSize(30);
        mPaintsquare.setStyle(Paint.Style.FILL);
        mPaintsquare.setAntiAlias(true);

        canvas.drawText("MapScale_Width : " + getWidth(), 50, 850, mPaintsquare);
        canvas.drawText("MapScale_Height  : " + getHeight(), 50, 950, mPaintsquare);
        canvas.drawText("beacon1-R1 : " + R1, 50, 1050, mPaintsquare);
        canvas.drawText("beacon2-R2 : " + R2, 50, 1150, mPaintsquare);
        canvas.drawText("beacon3-R3 : " + R3, 50, 1250, mPaintsquare);

        //Find constant of circle #2- cirlce #1
        float K_a = (R1 * R1) - (R2 * R2) - (x_1 * x_1) + (x_2 * x_2) - (y_1 * y_1) + (y_2 * y_2);
        //Find constant of circle #3- cirlce #1
        float K_b = (R1 * R1) - (R3 * R3) - (x_1 * x_1) + (x_3 * x_3) - (y_1 * y_1) + (y_3 * y_3);
        //Find constants of [x=A_0+A_1*r, y=B_0+B_1*r]
        float D = (x_1 * (y_2 - y_3)) + (x_2 * (y_3 - y_1)) + (x_3 * (y_1 - y_2));
        float A_0 = ((K_a * (y_1 - y_3)) + (K_b * (y_2 - y_1))) / (2 * D);
        float B_0 = -1 * ((K_a * (x_1 - x_3)) + (K_b * (x_2 - x_1))) / (2 * D);
        float A_1 = -1 * ((R1 * (y_2 - y_3)) + (R2 * (y_3 - y_1)) + (R3 * (y_1 - y_2))) / D;
        float B_1 = ((R1 * (x_2 - x_3)) + (R2 * (x_3 - x_1)) + (R3 * (x_1 - x_2))) / D;
        //Find constants of C_0 + 2*C_1*r + C_2^2 = 0
        float C_0 = (((A_0 - x_1) * (A_0 - x_1)) + (B_0 - y_1) * (B_0 - y_1)) - (R1 * R1);
        float C_1 = (A_1 * (A_0 - x_1)) + (B_1 * (B_0 - y_1)) - R1;
        float C_2 = (A_1 * A_1) + (B_1 * B_1) - 1;
        //Solve for r
        r = ((-1 * C_1) + (Math.sqrt(Math.pow(C_1, 2) - (C_0 * C_2)))) / C_2;
        //Solve for [x,y]
        _x = A_0 + A_1 * r;
        _y = B_0 + B_1 * r;

        if (_x < 0) _x *= -1;
        if (_y < 0) _y *= -1;

        X_Start = (int) (_x * 199.2248f);
        Y_Start = (int) (_y * 220.5930f);

        //filterLocation(X_Start, Y_Start);

        pos.add(X_Start);
        pos.add(Y_Start);

        if(pos.size() != 4){
            int x0 = pos.get(0);
            int y0 = pos.get(1);
            int x1 = pos.get(2);
            int y1 = pos.get(3);
            double distance = Math.sqrt((Math.pow((x1-x0), 2)) + (Math.pow((y1-y0), 2)));
            if(distance >50) {
                X_Start = x0;
                Y_Start = y0;
            }
            pos.remove(2);
            pos.remove(3);
        }
        //จบ แค่นี้ พรุ่งนี้ลองทดสอบ ตอนเริ่มรันโปรแกรม จุดมันต้องนิ่งอยู่ที่ๆ หนึ่ง เราต้องเดินไปหา แล้วเริ่มจากตรงนั้นครับ
        // ลองดูครับ มันไม่ควรแกว่งแล้ว ถ้า X_Start, Y_Start มีแค่ตรงนี้

        mapEdgeCheck(X_Start, Y_Start);
        userCheckStartX(X_Start);
        userCheckStartY(Y_Start);

        canvas.drawCircle((float) (X_Start), (float) (Y_Start), 25, mPaintsquare);
        mPaintsquare.setColor(getResources().getColor(android.R.color.black));
        canvas.drawText("YOU", (float) X_Start - 30, (float) Y_Start + 60, mPaintsquare);

        Log.d("redDot", "---------->" + _x + "  " + _y);
        canvas.drawText("x : " + _x, 50, 550, mPaintsquare);
        canvas.drawText("y : " + _y, 50, 650, mPaintsquare);
    }

    List listlocation_Y = new ArrayList();
    List listlocation_X = new ArrayList();
    int i = 0;
    private void filterLocation(double x, double y) {
        double oldLocationX, nextLocationX;
        double oldLocationY, nextLocationY;
        listlocation_X.add(x);
        listlocation_Y.add(y);

        if (listlocation_X.size() > 1 && listlocation_Y.size() > 1) {
            oldLocationX = (double) listlocation_X.get(0);
            nextLocationX = (double) listlocation_X.get(1);
            oldLocationY = (double) listlocation_Y.get(0);
            nextLocationY = (double) listlocation_Y.get(1);
            double sumY = (nextLocationY - oldLocationY);
            double sumX = (nextLocationX - oldLocationX);
            result = Math.sqrt((Math.pow(sumY, 2)) + (Math.pow(sumX, 2)));
            Log.d("TAG_result", "----result----->" + result);
            if (result <= 300) {
                X_Start = (int) nextLocationX;
                Y_Start = (int) nextLocationY;
                listlocation_Y.clear();
                listlocation_X.clear();
                listlocation_X.add(nextLocationX);
                listlocation_Y.add(nextLocationY);
            } else if (result > 300) {
                X_Start = (int) oldLocationX;
                Y_Start = (int) oldLocationY;
                listlocation_Y.clear();
                listlocation_X.clear();
                listlocation_X.add(oldLocationX);
                listlocation_Y.add(oldLocationY);
                i++;
                if(i == 5){
                    X_Start = (int) nextLocationX;
                    Y_Start = (int) nextLocationY;
                    i = 0;
                }
            }
        }
    }

    private void drawPath(final Canvas canvas) {
        mPaintsquare.reset();
        mPaintsquare.setColor(getResources().getColor(android.R.color.holo_red_light));
        mPaintsquare.setStrokeWidth(3);
        mPaintsquare.setTextSize(30);
        mPaintsquare.setStyle(Paint.Style.FILL);
        mPaintsquare.setAntiAlias(true);

        gameGrid = new int[getWidth()][getHeight()];
        //create block shelf
        for (int x = 320; x <= 858; x++) {
            for (int y = 245; y <= 370; y++)
                if (x > 319 && x < 535 && y > 244 && y < 371) { // shelf 1
                    gameGrid[x][y] = 1;
                } else if (x > 664 && x < 858 && y > 244 && y < 371) { // shelf 2
                    gameGrid[x][y] = 1;
                }
        }

        Pathfinder.initialize(new Settings() {
            @Override
            public int[][] getGrid() {
                return gameGrid;
            }

            @Override
            public SparseArray<Float> setTravellingCostRules() {
                SparseArray<Float> travellingCostRules = new SparseArray<>();
                //travellingCostRules.put(GRID_VALUE_FREE, 1f);
                return travellingCostRules;
            }

            @Override
            public boolean isNodeBlocked(int x, int y) {
                return gameGrid[x][y] == GRID_VALUE_SHELF || gameGrid[x][y] >= GRID_VALUE_BOULDERS_STARTING_VALUE;
            }
        });
        _shelf = MapActivity.shelfNum;
        shelfCheck(_shelf);

        canvas.drawText("x : " + X_Start, 50, 250, mPaintsquare);
        canvas.drawText("y : " + Y_Start, 50, 350, mPaintsquare);

        Pathfinder.findPath(X_Start, Y_Start, X_Stop, Y_Stop, new OnPathFoundListener() {
            @Override
            public void onPathFound(Path path) {
                if (path == null) Log.d("PATHFIND", "No Path possible!");
                else {
                    canvas.drawCircle(X_Stop, Y_Stop, 20, mPaintsquare);
                    mPaintsquare.setStyle(Paint.Style.STROKE);
                    canvas.drawPath(path, mPaintsquare);
                }
            }
        });
    }

    private void shelfCheck(String shelf) {
        if (shelf.equals("1")) {
            this.X_Stop = 422;
            this.Y_Stop = 385;
        } else if (shelf.equals("2")) {
            this.X_Stop = 771;
            this.Y_Stop = 385;
        }
    }

    private void mapEdgeCheck(int x, int y) {
        if (x > 1027) X_Start = 1027;
        else if (x < 1) X_Start = 1;
        if (y > 1338) Y_Start = 1338;
        else if (y < 1) Y_Start = 1;
    }

    private void userCheckStartX(int x) {
        if (x > 310 && x < 540) { //// shelf 1 check
            if (x > 310 && x < 205) X_Start = 310;
            else if (x > 204 && x < 540) X_Start = 540;
        } else if (x > 660 && x < 860) { //// shelf 2 check
            if (x > 660 && x < 759) X_Start = 660;
            else if (x > 758 && x < 860) X_Start = 860;
        }
    }

    private void userCheckStartY(int y) {
        if (y > 240 && y < 385) { //// shelf 1 check
            if (y > 240 && y < 308) Y_Start = 240;
            else if (y > 307 && y < 385) Y_Start = 385;
        } else if (y > 240 && y < 385) { //// shelf 2 check
            if (y > 240 && y < 308) Y_Start = 240;
            else if (y > 307 && y < 385) Y_Start = 385;
        }
    }
}