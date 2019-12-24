package com.gmail.pavkascool.seabattle;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private CellViewLayout white;
    private CellViewLayout black;
    private CellView ship;
    private int cellSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        white = findViewById(R.id.white);
        //white.setOnDragListener(white);
        black = findViewById(R.id.black);
        ship = findViewById(R.id.ship);
        if (savedInstanceState != null) {
            ship.setLocationCol(savedInstanceState.getInt("locCol"));
            ship.setLocationRow(savedInstanceState.getInt("locRow"));
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("locCol", ship.getLocationCol());
        savedInstanceState.putInt("locRow", ship.getLocationRow());
    }
}
