package com.gmail.pavkascool.seabattle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BattleActivity extends AppCompatActivity {

    private CellViewLayout white;
    private CellViewLayout black;
    //private List<CellView> ships = new ArrayList<CellView>();

    private Configuration config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        white = findViewById(R.id.white);
        black = findViewById(R.id.black);

//        if(savedInstanceState == null) {
//            Intent intent = getIntent();
//            for(int i = 0; i < 4; i++) {
//                CellView ship = new CellView(this, null);
//                int[] location = intent.getIntArrayExtra("shp" + i);
//                ship.setLocationCol(location[0]);
//                ship.setLocationRow(location[1]);
//                ship.setCols(location[2]);
//                ship.setRows(location[3]);
//                ships.add(ship);
//                white.addView(ship);
//            }
//        }
//        else {
//
//            for(int i = 0; i < 4; i++) {
//                CellView ship = new CellView(this, null);
//                int[] location = savedInstanceState.getIntArray("ship" + i);
//                ship.setLocationCol(location[0]);
//                ship.setLocationRow(location[1]);
//                ship.setCols(location[2]);
//                ship.setRows(location[3]);
//                ships.add(ship);
//                white.addView(ship);
//
//            }
//        }
        config = (Configuration)getLastCustomNonConfigurationInstance();
        Log.d("MyConfig","CONFIG = " + config);
        if(config == null) {
            config = new Configuration();
            Intent intent = getIntent();
            for(int i = 0; i < 4; i++) {
                CellView ship = new CellView(this, null);
                Log.d("MyConfig", "INSIDE I = " + i);
                int[] location = intent.getIntArrayExtra("shp" + i);
                ship.setLocationCol(location[0]);
                ship.setLocationRow(location[1]);
                ship.setCols(location[2]);
                ship.setRows(location[3]);
                config.addShip(ship);
                System.out.println("FIRST CONFIG PARENT = " + ship.getParent());
            }

        }
        List<CellView> ships = new ArrayList<CellView>(config.getShips());
        for(int i = 0; i < ships.size(); i++) {
            CellView ship = ships.get(i);
            Log.d("MyConfig", "OUTSIDE I = " + i + " Parent = " + ship.getParent());
            System.out.println("PARENT = " + ship.getParent());
            //white.removeView(ship);
            System.out.println("PARENT AFTER REMOVAL = " + ship.getParent());
            if(ship.getParent() == null) white.addView(ship);
            //ship.setVisibility(View.VISIBLE);

            Log.d("MyConfig", "ADDING I = " + i);
        }
    }

//    @Override
//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        super.onSaveInstanceState(savedInstanceState);
//        for(int i = 0; i < ships.size(); i++) {
//            CellView ship = ships.get(i);
//            int[] loc = new int[4];
//            loc[0] = ship.getLocationCol();
//            loc[1] = ship.getLocationRow();
//            loc[2] = ship.getCols();
//            loc[3] = ship.getRows();
//            savedInstanceState.putIntArray("ship" + i, loc);
//        }
//
//    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        Log.d("MyConfig", "SAVE = " + config);
        return config;
    }
}
