package com.gmail.pavkascool.seabattle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, OnFireListener {

    private static final int FLEET_SIZE = 10;
    public static final String YOURS = "Your Turn";
    public static final String ENEMYS = "Enemy's Turn";
    public static final String YOUR_VICTORY = "You Have Won!";
    public static final String YOUR_DEFEAT = "You Have Lost!";

    private CellViewLayout white;
    private CellViewLayout black;
    private CheckBox debug;
    private TextView turn;

    private Configuration config;

    private boolean isAgainstAI;

    private int turnNo;
    private int shotNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        if (savedInstanceState == null)
            isAgainstAI = getIntent().getBooleanExtra("againstAI", true);
        else isAgainstAI = savedInstanceState.getBoolean("againstAI");
        System.out.println("Against AI = " + isAgainstAI);
        white = findViewById(R.id.white);
        black = findViewById(R.id.black);
        black.setOnFireListener(this);

        debug = findViewById(R.id.debug);
        debug.setOnCheckedChangeListener(this);

        turn = findViewById(R.id.turn);

        config = (Configuration) getLastCustomNonConfigurationInstance();
        if (config == null) {
            config = new Configuration();
            Intent intent = getIntent();
            for (int i = 0; i < FLEET_SIZE; i++) {
                CellView ship = new CellView(this, null);
                int[] location = intent.getIntArrayExtra("shp" + i);
                ship.setLocationCol(location[0]);
                ship.setLocationRow(location[1]);
                ship.setCols(location[2]);
                ship.setRows(location[3]);
                config.addShip(ship);
            }

        }

        config.setAsFriend(white);
        config.setAsEnemy(black);

        String whoseTurn = null;
        if(config.isOver()) {
            whoseTurn = config.isYourVictory()? YOUR_VICTORY : YOUR_DEFEAT;
        } else {
            whoseTurn = config.isYourTurn() ? YOURS : ENEMYS;
        }
        turn.setText(whoseTurn);

        turnNo = config.getTurnNumber();
        shotNo = config.getShotNumber();

        List<CellView> ships = config.getShips();
        for (int i = 0; i < ships.size(); i++) {
            CellView ship = ships.get(i);
            if (ship.getParent() != null) ((ViewGroup) (ship.getParent())).removeView(ship);
            white.addView(ship);

        }
        config.setEnemyShips(getEnemyLocations());
        List<CellView> enemies = config.getEnemyShips();
        for (int i = 0; i < enemies.size(); i++) {
            CellView ship = enemies.get(i);
            if (ship.getParent() != null) ((ViewGroup) (ship.getParent())).removeView(ship);
            black.addView(ship);
            if (!debug.isChecked()) ship.setVisibility(View.INVISIBLE);
            else ship.setVisibility(View.VISIBLE);
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("againstAI", isAgainstAI);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return config;
    }

    private List<CellView> getEnemyLocations() {
        if (isAgainstAI) return getLocationsFromAI();
        else return getLocationsByBlueTooth();
    }

    private List<CellView> getLocationsByBlueTooth() {
        return new ArrayList<CellView>();
    }

    private List<CellView> getLocationsFromAI() {
        if (config.getEnemyShips().size() == FLEET_SIZE) return config.getEnemyShips();

        List<CellView> enemyShips = new ArrayList<CellView>();
        int maxLength = 4;
        int count = 5;
        Random random = new Random();
        int bound = black.getCols();
        CellView enemy;
        int colLoc;
        int rowLoc;
        for (int l = maxLength; l > 0; l--) {
            for (int num = 0; num < count - l; num++) {
                do {
                    enemy = new CellView(this, null);
                    int orientation = random.nextInt(2);
                    enemy.setOrientation(l, orientation);
                    enemy.setDecks(l);
                    colLoc = random.nextInt(bound + 1 - enemy.getCols());
                    rowLoc = random.nextInt(bound + 1 - enemy.getRows());
                    enemy.setLocationCol(colLoc);
                    enemy.setLocationRow(rowLoc);
                }
                while (black.isLocProhibited(colLoc, rowLoc, enemy));
                if (enemy.getParent() != null)
                    ((CellViewLayout) (enemy.getParent())).removeView(enemy);
                black.addView(enemy);
                enemyShips.add(enemy);
            }
        }

        return enemyShips;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            showEnemies();
        } else {
            hideEnemies();
        }
    }

    private void showEnemies() {
        for (int i = 0; i < black.getChildCount(); i++) {
            black.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

    private void hideEnemies() {
        for (int i = 0; i < black.getChildCount(); i++) {
            black.getChildAt(i).setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onFire(int c, int r) {
        if (config.isYourTurn()) {
            config.incrementShot();
            Coordinates coordinates = new Coordinates(r, c);
            if (config.getEnemyShots().contains(coordinates) || config.getEnemyHits().contains(coordinates)) {
                Toast.makeText(this, "Already checked. Fire again", Toast.LENGTH_SHORT).show();
            } else {
                for (int i = 0; i < black.getChildCount(); i++) {
                    CellView enemy = (CellView) (black.getChildAt(i));
                    for (Coordinates crd : enemy.getCoordinates()) {
                        if (crd.equals(coordinates)) {
                            enemy.damage();
                            System.out.println(enemy.getDecks());
                            config.addEnemyHit(crd);
                            if (enemy.isDrowned()) {
                                Toast.makeText(this, "DROWNED!", Toast.LENGTH_SHORT).show();
                                config.sinkEnemy();

                                for (Coordinates cc : enemy.getCoordinates()) {
                                    config.addEnemyNeighbours(cc.getZone());
                                }
                            }
                            if (config.getEnemyFleet() == 0) {
                                Toast.makeText(this, "YOU HAVE WON!", Toast.LENGTH_SHORT).show();
                                turn.setText(YOUR_VICTORY);
                                config.setYourVictory(true);
                                return;
                            }
                            return;
                        }
                    }
                }
                config.setYourTurn(false);
                turn.setText(ENEMYS);
                config.addEnemyShot(coordinates);
            }
        }
    }
}
