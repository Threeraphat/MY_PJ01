package com.example.my_pj01;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MapActivity extends AppCompatActivity {

    public static String shelfNum;
    public static final int REQUEST_ENABLE_BT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Utils.FullScreen(this);
        Bundle bundle = getIntent().getExtras();
        shelfNum = bundle.getString("shelf");
    }
}