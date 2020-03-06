package com.example.my_pj01;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import com.ichbingrumpig.pathfinder.OnPathFoundListener;
import com.ichbingrumpig.pathfinder.Pathfinder;
import com.ichbingrumpig.pathfinder.Settings;

import static com.example.my_pj01.CustomView.X_Start;
import static com.example.my_pj01.CustomView.X_Stop;
import static com.example.my_pj01.CustomView.Y_Start;
import static com.example.my_pj01.CustomView.Y_Stop;

public class MapActivity extends AppCompatActivity {

    int[][] gameGrid;
    private static final int GRID_VALUE_SHELF = 1;
    private static final int GRID_VALUE_FREE = 0;
    private static final int GRID_VALUE_BOULDERS_STARTING_VALUE = 4;
    Handler handler = new Handler();
    public static String shelfNum;
    public static final int REQUEST_ENABLE_BT = 1;
    public static Path real_path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Utils.FullScreen(this);
        Bundle bundle = getIntent().getExtras();
        shelfNum = bundle.getString("shelf");
        /*final Runnable r = new Runnable() {
            public void run() {

                gameGrid = new int[1028][1339];
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

                Pathfinder.initialize(new Settings() {
                    @Override
                    public int[][] getGrid() {
                        return gameGrid;
                    }

                    @Override
                    public SparseArray<Float> setTravellingCostRules() {
                        SparseArray<Float> travellingCostRules = new SparseArray<>();
                        travellingCostRules.put(GRID_VALUE_FREE, 1f);
                        return travellingCostRules;
                    }

                    @Override
                    public boolean isNodeBlocked(int x, int y) {
                        return gameGrid[x][y] == GRID_VALUE_SHELF || gameGrid[x][y] >= GRID_VALUE_BOULDERS_STARTING_VALUE;
                    }
                });
                shelfCheck(shelfNum);

                Pathfinder.findPath(X_Start, Y_Start, X_Stop, Y_Stop, new OnPathFoundListener() {
                    @Override
                    public void onPathFound(Path path) {
                        // The algorithm will return here.
                        // If a path has been found the resulting android.graphics.Path object is passed into this method.
                        // If no path is possible NULL is passed into this method.
                        if (path == null) Log.d("PATHFIND", "No Path possible!");
                        else {
                            real_path = path;
                        }
                    }
                });
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(r, 1000);
    }

    private void shelfCheck(String shelf) {
        if (shelf.equals("1")) {
            X_Stop = 422;
            Y_Stop = 375;
        } else if (shelf.equals("2")) {
            X_Stop = 771;
            Y_Stop = 375;
        }
    }*/
    }
}