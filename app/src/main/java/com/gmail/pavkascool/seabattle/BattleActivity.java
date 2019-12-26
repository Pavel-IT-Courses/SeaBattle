package com.gmail.pavkascool.seabattle;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class BattleActivity extends AppCompatActivity {

    private CellViewLayout white;
    private CellViewLayout black;
    private List<CellView> ships = new ArrayList<CellView>();
    private int cellSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        white = findViewById(R.id.white);
        black = findViewById(R.id.black);

        if(savedInstanceState == null) {
            Intent intent = getIntent();
            for(int i = 0; i < 4; i++) {
                CellView ship = new CellView(this, null);
                int[] location = intent.getIntArrayExtra("shp" + i);
                ship.setLocationCol(location[0]);
                ship.setLocationRow(location[1]);
                ship.setCols(location[2]);
                ship.setRows(location[3]);
                ships.add(ship);
                white.addView(ship);
            }
        }
        else {
            for(int i = 0; i < ships.size(); i++) {
                CellView ship = ships.get(i);
                int[] location = savedInstanceState.getIntArray("ship" + i);
                ship.setLocationCol(location[0]);
                ship.setLocationRow(location[1]);
                ship.setCols(location[2]);
                ship.setRows(location[3]);
                //ships.add(ship);
                white.addView(ship);

            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        for(int i = 0; i < ships.size(); i++) {
            CellView ship = ships.get(i);
            int[] loc = new int[4];
            loc[0] = ship.getLocationCol();
            loc[1] = ship.getLocationRow();
            loc[2] = ship.getCols();
            loc[3] = ship.getRows();
            savedInstanceState.putIntArray("ship" + i, loc);
        }

    }
}
