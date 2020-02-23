package com.gmail.pavkascool.update;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.gmail.pavkascool.update.ai.AIPlayer;
import com.gmail.pavkascool.update.database.BattleDatabase;
import com.gmail.pavkascool.update.database.Result;
import com.gmail.pavkascool.update.delegates.Configuration;
import com.gmail.pavkascool.update.utils.Coordinates;
import com.gmail.pavkascool.update.views.CellView;
import com.gmail.pavkascool.update.views.CellViewLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BattleActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, OnFireListener{

    public static final int FLEET_SIZE = 10;
    public static final String YOURS = "Your Turn";
    public static final String ENEMYS = "Enemy's Turn";
    public static final String YOUR_VICTORY = "You Have Won!";
    public static final String YOUR_DEFEAT = "You Have Lost!";

    public static final int RESULT_MISS = 0;
    public static final int RESULT_DAMAGE = 1;
    public static final int RESULT_DROWN = 2;

    private CellViewLayout white;
    private CellViewLayout black;
    private CheckBox debug;
    private TextView turn;

    private Configuration config;

    private boolean isAgainstAI;

    private int turnNo;
    private int shotNo;

    private Player player;
    private BattleDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        db = BattleApplication.getInstance().getBattleDatabase();
        if (savedInstanceState == null)
            isAgainstAI = BattleApplication.getInstance().isAgainstAI();
        else isAgainstAI = savedInstanceState.getBoolean("againstAI");


        if(isAgainstAI) player = new AIPlayer();
        else player = new HumanPlayer();

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
                int decks = Math.max(location[2], location[3]);
                ship.setDecks(decks);
                config.addShip(ship);
            }

        }

        config.setAsFriend(white);
        config.setAsEnemy(black);


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

        String whoseTurn = null;
        if(config.isOver()) {
            whoseTurn = config.isYourVictory()? YOUR_VICTORY : YOUR_DEFEAT;
        } else {
            whoseTurn = config.isYourTurn() ? YOURS : ENEMYS;
        }
        turn.setText(whoseTurn);

        turnNo = config.getTurnNumber();
        shotNo = config.getShotNumber();

    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("My Fleet is " + config.getFleet() + ", Ememy's Fleet is " + config.getEnemyFleet());
        System.out.println("SOCKET CONNECTED " + BattleApplication.getInstance().getBluetoothSocket().isConnected());
//        if(!config.isYourTurn()) {
//            turn.setText(ENEMYS);
//            sufferAttacks();
//        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!isAgainstAI) Connector.getInstance().stopCommunication();
    }

    private List<CellView> getLocationsByBlueTooth() {
        if(config.getEnemyShips().size() == FLEET_SIZE) return config.getEnemyShips();

        Intent intent = getIntent();
        System.out.println("INTENT is " + intent);
        int[] enemies = intent.getIntArrayExtra("enemies");
        int index = 0;
        List<CellView> enemyShips = new ArrayList<>();
        for(int i = 0; i < FLEET_SIZE; i++) {
            CellView enemy = new CellView(this, null);
            enemy.setLocationCol(enemies[index++]);
            enemy.setLocationRow(enemies[index++]);
            enemy.setCols(enemies[index++]);
            enemy.setRows(enemies[index++]);
            enemyShips.add(enemy);
            black.addView(enemy);
        }

        return enemyShips;
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
                                final Result result = getResult(true);
                                Thread t = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        db.resultDao().insert(result);
                                        BattleApplication.getInstance().getStartModel().updateResults();
                                    }
                                });
                                t.start();

                                //return;
                                try {
                                    Thread.currentThread().sleep(500);
                                    if(!isAgainstAI) Connector.getInstance().stopCommunication();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                finish();
                            }
                            return;
                        }
                    }
                }
                config.setYourTurn(false);
                turn.setText(ENEMYS + config.getTurnNumber());
                config.addEnemyShot(coordinates);
                sufferAttacks();
            }

        }
    }

    private void sufferAttacks() {

        new Thread(runnable).start();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            outer: while(!config.isYourTurn()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(750);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                final Coordinates coordinates = player.takeTarget();
                System.out.println("ENEMY FIRES: " + coordinates.getCol() + " " + coordinates.getRow());
                for(int i = 0; i < white.getChildCount(); i++) {
                    final CellView ship = (CellView)(white.getChildAt(i));
                    for(final Coordinates crd: ship.getCoordinates()) {
                        if(coordinates.equals(crd)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ship.damage(crd);
                                    System.out.println("HIT! DECKS LEFT: " + ship.getDecks());
                                }
                            });
                            try {
                                TimeUnit.MILLISECONDS.sleep(900);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            System.out.println("Repeating...HIT! DECKS LEFT: " + ship.getDecks());
                            BattleActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    config.addHit(coordinates);
                                }
                            });

                            if(ship.isDrowned()) {
                                player.getReport(coordinates, RESULT_DROWN);
                                System.out.println("ENEMY DRAWNED MY SHIP");
                                for(final Coordinates cc: ship.getCoordinates()) {

                                    BattleActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            config.addNeighbours(cc.getZone());
                                        }
                                    });

                                }
                                config.sink();
                                System.out.println("My Fleet Size = " + config.getFleet());
                                if(config.getFleet() == 0) {
                                    Result result = getResult(false);
                                    db.resultDao().insert(result);
                                    BattleApplication.getInstance().getStartModel().updateResults();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(BattleActivity.this, "YOU HAVE LOST!", Toast.LENGTH_LONG).show();
                                            turn.setText(YOUR_DEFEAT);

                                        }
                                    });

                                    //return;
                                    try {
                                        Thread.currentThread().sleep(500);
                                        if(!isAgainstAI) Connector.getInstance().stopCommunication();
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    finish();
                                }
                            }
                            else {
                                player.getReport(coordinates, RESULT_DAMAGE);
                            }
                            continue outer;
                        }
                    }
                }
                player.getReport(coordinates, RESULT_MISS);
                BattleActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        config.addShot(coordinates);
                        config.incrementTurn();
                    }
                });
                try {
                    TimeUnit.MILLISECONDS.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Coordinates " + coordinates.getCol() + " " + coordinates.getRow() + " added to SHOTS = " + player.getShots().contains(coordinates));
                config.setYourTurn(true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        turn.setText(YOURS + config.getTurnNumber());
                    }
                });
            }
        }
    };

    Runnable notifier = new Runnable() {
        @Override
        public void run() {
            config.notifyFriend();
        }
    };

    private Result getResult(boolean yourVictory) {
        String dateString = new SimpleDateFormat("dd-MM-yy hh:mm").format(new Date());
        String turn = String.valueOf(config.getTurnNumber());
        String winner = yourVictory? "you" : player.getName();
        return new Result(dateString, winner, turn);
    }


}
