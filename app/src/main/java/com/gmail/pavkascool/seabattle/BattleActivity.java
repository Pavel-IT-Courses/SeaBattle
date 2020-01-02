package com.gmail.pavkascool.seabattle;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleActivity extends AppCompatActivity {

    private final static int FLEET_SIZE = 10;

    private CellViewLayout white;
    private CellViewLayout black;

    private Configuration config;

    private boolean againstAI = true;

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
            for(int i = 0; i < FLEET_SIZE; i++) {
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
        config.setEnemies(getEnemyLocations());
        List<CellView> enemies = config.getEnemies();
        for(int i = 0; i < enemies.size(); i++) {
            CellView ship = enemies.get(i);
            if(ship.getParent() != null) ((ViewGroup)(ship.getParent())).removeView(ship);
            black.addView(ship);
        }

    }


    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return config;
    }

    private List<CellView> getEnemyLocations() {
        if(againstAI) return getLocationsFromAI();
        else return getLocationsByBlueTooth();
    }

    private List<CellView> getLocationsByBlueTooth() {
        return null;
    }

    private List<CellView> getLocationsFromAI() {
        if(config.getEnemies().size() == 10) return config.getEnemies();

        List<CellView> enemyShips = new ArrayList<CellView>();
        int maxLength = 4;
        int count = 5;
        Random random = new Random();
        int bound = black.getCols();
        CellView enemy;
        int colLoc;
        int rowLoc;
        for(int l = maxLength; l > 0; l--) {
            for(int num = 0; num < count - l; num++) {
                do {
                    enemy = new CellView(this, null);
                    int orientation = random.nextInt(2);
                    enemy.setOrientation(l, orientation);
                    colLoc = random.nextInt(bound + 1 - enemy.getCols());
                    rowLoc = random.nextInt(bound + 1 - enemy.getRows());
                    enemy.setLocationCol(colLoc);
                    enemy.setLocationRow(rowLoc);
                    //enemy.setVisibility(View.INVISIBLE);
                }
                while(black.isLocProhibited(colLoc, rowLoc, enemy));
                if(enemy.getParent() != null) ((CellViewLayout)(enemy.getParent())).removeView(enemy);
                black.addView(enemy);
                enemyShips.add(enemy);
            }
        }

        return enemyShips;
    }
}
