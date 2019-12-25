package com.gmail.pavkascool.seabattle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DeploymentActivity extends AppCompatActivity implements View.OnClickListener {

    private CellViewLayout start;
    private CellViewLayout battlefield;

    private CellView ship4;
    private CellView ship3_1, ship3_2;
    private CellView ship2_1, ship2_2, ship2_3;
    private CellView ship1_1, ship1_2, ship1_3, ship1_4;

    private List<CellView> ships = new ArrayList<CellView>();

    private CellView selectedShip;

    private Button rotate;
    private Button fight;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deployment);

        rotate = findViewById(R.id.rotate);
        rotate.setOnClickListener(this);

        fight = findViewById(R.id.fight);
        fight.setOnClickListener(this);

        start = findViewById(R.id.start);
        battlefield = findViewById(R.id.battlefield);

        ship4 = findViewById(R.id.ship4);
        ships.add(ship4);
        ship3_1 = findViewById(R.id.ship3_1);
        ships.add(ship3_1);
        ship1_1 = findViewById(R.id.ship1_1);
        ships.add(ship1_1);

        if(savedInstanceState != null) {
            obtainFleetLocation(savedInstanceState);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        saveFleetLocation(savedInstanceState);

    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.rotate:
                //TODO
                break;

            case R.id.fight:
                if(start.getChildCount() > 0) {
                    Toast.makeText(this, "Your Ships are not completely deployed yet! Complete Deployment!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, BattleActivity.class);
                    startActivity(intent);
                }
        }

    }

    private void saveFleetLocation(Bundle bundle) {
        for(int i = 0; i < ships.size(); i++) {
            CellView ship = ships.get(i);
            int[] location = new int[3];
            location[0] = ((ViewGroup)(ship.getParent())).getId();
            location[1] = ship.getLocationCol();
            location[2] = ship.getLocationRow();
            String name = "ship" + i;
            bundle.putIntArray(name, location);
        }
    }

    private void obtainFleetLocation(Bundle bundle) {
        for(int i = 0; i < ships.size(); i++) {
            CellView ship = ships.get(i);
            int[] location = bundle.getIntArray("ship" + i);
            if(location[0] == R.id.battlefield) {
                start.removeView(ship);
                battlefield.addView(ship);
                ship.setLocationCol(location[1]);
                ship.setLocationRow(location[2]);
            }
        }
    }
}
