package com.gmail.pavkascool.seabattle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    CellView white;
    CellView black;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        white = findViewById(R.id.white);
        black = findViewById(R.id.black);
    }
}
