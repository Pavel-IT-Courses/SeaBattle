package com.gmail.pavkascool.seabattle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

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
        //ship.setOnDragListener(white);

        //cellSize = white.getCellSize();
        //ship.setCellSize(cellSize);

    }
}
