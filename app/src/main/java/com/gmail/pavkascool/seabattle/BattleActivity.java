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

    private Configuration config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        white = findViewById(R.id.white);
        black = findViewById(R.id.black);

        config = (Configuration)getLastCustomNonConfigurationInstance();
        if(config == null) {
            config = new Configuration();
            Intent intent = getIntent();
            for(int i = 0; i < 4; i++) {
                CellView ship = new CellView(this, null);
                int[] location = intent.getIntArrayExtra("shp" + i);
                ship.setLocationCol(location[0]);
                ship.setLocationRow(location[1]);
                ship.setCols(location[2]);
                ship.setRows(location[3]);
                config.addShip(ship);
            }

        }
        List<CellView> ships = config.getShips();
        for(int i = 0; i < ships.size(); i++) {
            CellView ship = ships.get(i);
            if(ship.getParent() != null) ((ViewGroup)(ship.getParent())).removeView(ship);
            white.addView(ship);

        }
    }


    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return config;
    }
}
