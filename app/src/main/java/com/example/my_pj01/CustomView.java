package com.example.my_pj01;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.example.my_pj01.Models.BTLE_Device;
import com.ichbingrumpig.pathfinder.OnPathFoundListener;
import com.ichbingrumpig.pathfinder.Pathfinder;
import com.ichbingrumpig.pathfinder.Settings;

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
    int X_Start = 0, Y_Start = 0;
    int X_Stop = 0, Y_Stop = 0;

    int[][] gameGrid;
    private static final int GRID_VALUE_SHELF = 1;
    private static final int GRID_VALUE_FREE = 0;
    private static final int GRID_VALUE_BOULDERS_STARTING_VALUE = 4;

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
    //  1028 Pixel / 1.5 m(150cm)  = 685 (X)
    //  1339 Pixel / 1.8 m(180cm) = 744 (Y)
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

    public CustomView(Context context) {
        super(context);
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
        DrawGridMap(canvas);
        DrawBeacon(canvas);
        PointUserCalculate(canvas);
        drawPath(canvas);
        drawShelf(canvas);
        mPaintsquare.reset();

        postInvalidateDelayed(10);
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
                    if (R1 > 1.8f) R1 = 1.8f;
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
                    if (R2 > 1.5f) R2 = 1.5f;
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
                    if (R3 > 1.5f) R3 = 1.5f;
                    RSSIAverage_3.clear();
                }

                Log.d("BAS_TEST", "--------> RSSIAverage3 " + R3_sum);
                Log.d("BAS_TEST", "---------->" + name + "  Distance" + DistanceCalculate(rssi3, measuredPower) + "  RSSI------>" + rssi3 + "  " + addr);
            }
        }
    }

    private void DrawGridMap(Canvas canvas) {
        mPaintsquare.reset();
        mPaintsquare.setColor(getResources().getColor(android.R.color.darker_gray));
        mPaintsquare.setStrokeWidth(1);
        mPaintsquare.setStyle(Paint.Style.FILL);
        mPaintsquare.setAntiAlias(true);
        if (numColumns == 0 || numRows == 0) {
            return;
        }

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

        if (numColumns == 0 || numRows == 0) {
            return;
        }

        int R = 20;
        //old dist = 633
        double dist_real = 744;

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
        canvas.drawCircle((numColumns / 2) * cellWidth, 0 * cellHeight, R1 * (float) dist_real, mPaintsquare);

        mPaintsquare.setStyle(Paint.Style.STROKE);
        mPaintsquare.setColor(getResources().getColor(android.R.color.holo_red_light));
        mPaintsquare.setStrokeWidth(5);
        canvas.drawCircle(0 * cellWidth, (numRows / 2) * cellHeight, R2 * (float) dist_real, mPaintsquare);

        mPaintsquare.setStyle(Paint.Style.STROKE);
        mPaintsquare.setColor(getResources().getColor(android.R.color.holo_blue_light));
        mPaintsquare.setStrokeWidth(5);
        canvas.drawCircle(numColumns * cellWidth, (numRows / 2) * cellHeight, R3 * (float) dist_real, mPaintsquare);
    }

    private void drawShelf(Canvas canvas) {
        mPaintsquare.reset();
        mPaintsquare.setTextSize(30);
        mPaintsquare.setAntiAlias(true);

        if (numColumns == 0 || numRows == 0) {
            return;
        }

        for (int i = 0; i < numColumns; i++) {
            for (int j = 0; j < numRows; j++) {
                if (i > 9 && i < 15 && j == 6) { ////Shelf 1
                    mPaintsquare.setStyle(Paint.Style.FILL);
                    mPaintsquare.setColor(getResources().getColor(android.R.color.darker_gray));
                    canvas.drawRect(i * cellWidth, j * cellHeight, (i + 1) * cellWidth, (j + 1) * cellHeight, mPaintsquare);
                    if (i == 10) {
                        mPaintsquare.setStyle(Paint.Style.FILL);
                        mPaintsquare.setColor(getResources().getColor(android.R.color.holo_blue_dark));
                        canvas.drawText("Shelf 1", (i + 1) * cellWidth, j * cellHeight, mPaintsquare);
                    }
                } else if (i > 9 && i < 15 && j == 7) {
                    mPaintsquare.setStyle(Paint.Style.FILL);
                    mPaintsquare.setColor(getResources().getColor(android.R.color.darker_gray));
                    canvas.drawRect(i * cellWidth, j * cellHeight, (i + 1) * cellWidth, (j + 1) * cellHeight, mPaintsquare);
                } else if (i > 19 && i < 25 && j == 6) { ////Shelf 2
                    mPaintsquare.setStyle(Paint.Style.FILL);
                    mPaintsquare.setColor(getResources().getColor(android.R.color.darker_gray));
                    canvas.drawRect(i * cellWidth, j * cellHeight, (i + 1) * cellWidth, (j + 1) * cellHeight, mPaintsquare);
                    if (i == 20) {
                        mPaintsquare.setStyle(Paint.Style.FILL);
                        mPaintsquare.setColor(getResources().getColor(android.R.color.holo_blue_dark));
                        canvas.drawText("Shelf 2", (i + 1) * cellWidth, j * cellHeight, mPaintsquare);
                    }
                } else if (i > 19 && i < 25 && j == 7) {
                    mPaintsquare.setStyle(Paint.Style.FILL);
                    mPaintsquare.setColor(getResources().getColor(android.R.color.darker_gray));
                    canvas.drawRect(i * cellWidth, j * cellHeight, (i + 1) * cellWidth, (j + 1) * cellHeight, mPaintsquare);
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

    private void PointUserCalculate(Canvas canvas) {

        mPaintsquare.reset();
        mPaintsquare.setColor(getResources().getColor(android.R.color.holo_green_light));
        mPaintsquare.setStrokeWidth(30);
        mPaintsquare.setTextSize(30);
        mPaintsquare.setStyle(Paint.Style.FILL);
        mPaintsquare.setAntiAlias(true);

        canvas.drawText("MapScale_Width : " + getWidth(), 50, 850, mPaintsquare);
        canvas.drawText("MapScale_Height  : " + getHeight(), 50, 950, mPaintsquare);

//        canvas.drawText("beacon1 : " + rssi1, 50, 1050, mPaintsquare);
//        canvas.drawText("beacon2 : " + rssi2, 50, 1150, mPaintsquare);
//        canvas.drawText("beacon3 : " + rssi3, 50, 1250, mPaintsquare);
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

        //Red dot
        canvas.drawCircle((float) (_x) * 685, (float) (_y) * 100, 25, mPaintsquare);
        mPaintsquare.setColor(getResources().getColor(android.R.color.black));
        canvas.drawText("YOU",(float)((_x) * 685) - 30,(float)((_y) * 100) + 60,mPaintsquare);
        Log.d("redDot", "---------->" + _x + "  " + _y);

        canvas.drawText("x : " + _x, 50, 550, mPaintsquare);
        canvas.drawText("y : " + _y, 50, 650, mPaintsquare);
    }

    private void drawPath(final Canvas canvas) {
        mPaintsquare.reset();
        mPaintsquare.setColor(getResources().getColor(android.R.color.holo_red_light));
        mPaintsquare.setStrokeWidth(3);
        mPaintsquare.setTextSize(30);
        mPaintsquare.setStyle(Paint.Style.FILL);
        mPaintsquare.setAntiAlias(true);

        X_Start = (int) (_x * 685);
        Y_Start = (int) (_y * 100);


        canvas.drawText("x : " + X_Start, 50, 250, mPaintsquare);
        canvas.drawText("y : " + Y_Start, 50, 350, mPaintsquare);


        gameGrid = new int[getWidth()][getHeight()];
        //create block shelf
        for (int i = 320; i <= 858; i++) {
            for (int j = 245; j <= 370; j++)
                //shelf 1
                if (i > 319 && i < 535 && j > 245 && j < 370) {
                    gameGrid[i][j] = 1;
                } else if (i > 664 && i < 858 && j > 245 && j < 370) { // shelf 2
                    gameGrid[i][j] = 1;
                }
        }

        // If stuff happens on the board (like e.g. towns are build, aliens land on some Nodes, etc)
        // the gameGrid array will be updated. See the declaration of the GRID_VALUE_ constants above.
        // Now we initialize the library to our rules

        Pathfinder.initialize(new Settings() {
            @Override
            public int[][] getGrid() {
                // If the algorithm asks for the grid, return our game grid array.
                return gameGrid;
            }

            @Override
            public SparseArray<Float> setTravellingCostRules() {
                // This is where we specify our travelling cost rules.
                // As we defined above, if a node contains aliens (-> grid at that location has value of 3)
                // travelling should be only a third as fast as normal.
                // Travelling on swamps should be only half as fast.

                // !Note:
                // Remember that we are initializing the COSTS, not the travelling speed.
                // Rules of thumb: Generally, travelling half as fast means double the costs.

                // !Also note:
                // We don't put in the value of towns since we don't move slower, but not at all (they are blocked, after all).
                SparseArray<Float> travellingCostRules = new SparseArray<>();

                // The key is the grid value
                // while the value is the factor. (For swamps this is 2, because the costs are doubled and for
                // alien Nodes the value is 3, since the costs are tripled).
                // By default Nodes have a travelling factor of 1, if not specified.
                //travellingCostRules.put(GRID_VALUE_FREE, 35f);
                return travellingCostRules;
            }

            @Override
            public boolean isNodeBlocked(int x, int y) {
                // Here we return true if the Node at [x, y] is blocked.
                // In our example every Node in the area of a town is blocked,
                // as well as every Node that is in the area of a boulder.
                // Recap: Boulders each have their own grid value, starting at 4 and incrementing per boulder.
                // Thus we return true if the grid value at [x, y] is 1 (town)
                // OR bigger than or equal to 4 (boulder):
                return gameGrid[x][y] == GRID_VALUE_SHELF || gameGrid[x][y] >= GRID_VALUE_BOULDERS_STARTING_VALUE;
            }
        });
        String _shelf = MapActivity.shelfNum;
        shelfCheck(_shelf);
        userCheckStart(X_Start, Y_Start);

        Pathfinder.findPath(X_Start, Y_Start, X_Stop, Y_Stop, new OnPathFoundListener() {
            @Override
            public void onPathFound(Path path) {
                // The algorithm will return here.
                // If a path has been found the resulting android.graphics.Path object is passed into this method.
                // If no path is possible NULL is passed into this method.
                if (path == null) Log.d("PATHFIND", "No Path possible!");
                else {
                    // ...
                    // Awesome path stuff going on here.
                    Log.d("PATHFIND", "----------------->" + path.isEmpty());
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
            this.Y_Stop = 375;
        } else if (shelf.equals("2")) {
            this.X_Stop = 771;
            this.Y_Stop = 375;
        }
    }

    private void userCheckStart(int x, int y) {

        if (x > 1018) {
            X_Start = 1018;
        } else if (y > 1319) {
            Y_Start = 1319;
        } else if (x < 20) {
            X_Start = 20;
        } else if (y < 20) {
            Y_Start = 20;
        } /*else if (x > 319 && x < 535) { //// X_Start check
            if (x > 319) {
                X_Start = 318;
            } else if (x < 535) {
                X_Start = 536;
            }
        } else if (y > 245 && y < 370) { //// Y_Start check
            if (y > 245) {
                Y_Start = 244;
            } else if (y < 370) {
                Y_Start = 371;
            }
        }*/
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