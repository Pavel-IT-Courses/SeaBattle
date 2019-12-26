package com.gmail.pavkascool.seabattle;

import android.os.Bundle;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;

public class BattleActivity extends AppCompatActivity {

    private CellViewLayout white;
    private CellViewLayout black;
    private CellView ship;
    private int cellSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //  super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        white = findViewById(R.id.white);
        black = findViewById(R.id.black);
        ship = findViewById(R.id.ship);
        if (savedInstanceState != null) {
            int location = savedInstanceState.getInt("parent");
            if(location != R.id.white) {
                white.removeView(ship);
                black.addView(ship);
            }
            ship.setLocationCol(savedInstanceState.getInt("locCol"));
            ship.setLocationRow(savedInstanceState.getInt("locRow"));
        }

        super.onCreate(savedInstanceState);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("parent", ((ViewGroup)ship.getParent()).getId());
        savedInstanceState.putInt("locCol", ship.getLocationCol());
        savedInstanceState.putInt("locRow", ship.getLocationRow());
    }
}
