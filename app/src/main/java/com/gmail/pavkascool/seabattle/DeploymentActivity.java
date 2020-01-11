package com.gmail.pavkascool.seabattle;

import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class DeploymentActivity extends AppCompatActivity implements View.OnClickListener {

    private CellViewLayout start;
    private CellViewLayout battlefield;

    private CellView ship4;
    private CellView ship3_1, ship3_2;
    private CellView ship2_1, ship2_2, ship2_3;
    private CellView ship1_1, ship1_2, ship1_3, ship1_4;

    private List<CellView> ships = new ArrayList<CellView>();
    private Set<Coordinates> prohibited;

    private Button rotate;
    private Button fight;
    private Button auto;

    private boolean isAgainstAI;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deployment);

        rotate = findViewById(R.id.rotate);
        rotate.setOnClickListener(this);

        auto = findViewById(R.id.auto);
        auto.setOnClickListener(this);

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
        ship2_1 = findViewById(R.id.ship2_1);
        ships.add(ship2_1);
        ship3_2 = findViewById(R.id.ship3_2);
        ships.add(ship3_2);
        ship1_2 = findViewById(R.id.ship1_2);
        ships.add(ship1_2);
        ship1_3 = findViewById(R.id.ship1_3);
        ships.add(ship1_3);
        ship1_4 = findViewById(R.id.ship1_4);
        ships.add(ship1_4);
        ship2_2 = findViewById(R.id.ship2_2);
        ships.add(ship2_2);
        ship2_3 = findViewById(R.id.ship2_3);
        ships.add(ship2_3);

        if(savedInstanceState != null) {
            obtainFleetLocation(savedInstanceState);
            isAgainstAI = savedInstanceState.getBoolean("againstAI");
        }
        else {
            isAgainstAI = getIntent().getBooleanExtra("againstAI", true);
        }
    }

        @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        saveFleetLocation(savedInstanceState);
        savedInstanceState.putBoolean("againstAI", isAgainstAI);
    }

    @Override
    public void onClick(View v) {
        CellView ship = battlefield.getSelectedShip();
        switch(v.getId()) {
            case R.id.rotate:
                if(ship == null) {
                    Toast.makeText(this, "No Ship to rotate selected", Toast.LENGTH_SHORT).show();
                }
                else {
                    battlefield.removeView(ship);
                    ship.rotate();
                    if(battlefield.isLocProhibited(ship.getLocationCol(), ship.getLocationRow(), ship)) {
                        ship.rotateBack();
                        final Toast toast = Toast.makeText(this, "You cannot rotate your Ship in this position", Toast.LENGTH_SHORT);
                        toast.show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                toast.cancel();
                            }
                        }, 1000);
                    }
                    battlefield.addView(ship);
                }
                break;

            case R.id.auto:
                List<CellView> sps = new ArrayList<CellView>();
                int maxLength = 4;
                int count = 5;
                Random random = new Random();
                int bound = battlefield.getCols();
                CellView s;
                int colLoc;
                int rowLoc;
                for(int l = maxLength; l > 0; l--) {
                    for(int num = 0; num < count - l; num++) {
                        do {
                            s = new CellView(this, null);
                            int orientation = random.nextInt(2);
                            s.setOrientation(l, orientation);
                            colLoc = random.nextInt(bound + 1 - s.getCols());
                            rowLoc = random.nextInt(bound + 1 - s.getRows());
                            s.setLocationCol(colLoc);
                            s.setLocationRow(rowLoc);
                        }
                        while(battlefield.isLocProhibited(colLoc, rowLoc, s));
                        if(s.getParent() != null) ((CellViewLayout)(s.getParent())).removeView(s);
                        battlefield.addView(s);
                        sps.add(s);
                        start.removeAllViews();
                        ships = sps;
                    }
                }

                break;

            case R.id.fight:
                if(start.getChildCount() > 0) {
                    Toast.makeText(this, "Your Ships are not completely deployed yet! Complete Deployment!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, BattleActivity.class);
                    intent.putExtra("againstAI", isAgainstAI);

                    for(int i = 0; i < ships.size(); i++) {
                        CellView shp = ships.get(i);
                        String name = "shp" + i;
                        int[] loc = new int[4];
                        loc[0] = shp.getLocationCol();
                        loc[1] = shp.getLocationRow();
                        loc[2] = shp.getCols();
                        loc[3] = shp.getRows();
                        intent.putExtra(name, loc);
                    }
                    startActivity(intent);
                }
        }

    }

    private void saveFleetLocation(Bundle bundle) {
        for(int i = 0; i < ships.size(); i++) {
            CellView ship = ships.get(i);
            int[] location = new int[5];
            location[0] = ((ViewGroup)(ship.getParent())).getId();
            location[1] = ship.getLocationCol();
            location[2] = ship.getLocationRow();
            location[3] = ship.getCols();
            location[4] = ship.getRows();
            String name = "ship" + i;
            bundle.putIntArray(name, location);
        }
    }

    private void obtainFleetLocation(Bundle bundle) {
        for (int i = 0; i < ships.size(); i++) {
            CellView ship = ships.get(i);
            int[] location = bundle.getIntArray("ship" + i);
            if (location[0] == R.id.battlefield) {
                ((ViewGroup)(ship.getParent())).removeView(ship);

                ship.setLocationCol(location[1]);
                ship.setLocationRow(location[2]);
                ship.setCols(location[3]);
                ship.setRows(location[4]);

                battlefield.addView(ship);
            }

        }
    }
}
