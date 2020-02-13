package com.gmail.pavkascool.update.ai;

import com.gmail.pavkascool.update.Player;
import com.gmail.pavkascool.update.utils.Coordinates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.gmail.pavkascool.update.BattleActivity.*;

public class AIPlayer implements Player {
    private final static int RANDOM_SHOTS = 10;

    private static AIPlayer instance;
    public final String name = "AI Player";
    private int rows = 10;
    private int columns = 10;
    private List<Coordinates> damagedEnemy = new ArrayList<>();
    private List<Coordinates> shots = new ArrayList<>();
    private List<Coordinates> hits = new ArrayList<>();
    private Set<Coordinates> neighbours = new HashSet<>();
    private Map<Integer, Integer> enemyShipMap;
    private int minLength;

    private boolean isTurn;
    private int shotNo;

    private Random random = new Random();

//    public static AIPlayer getInstance() {
//        if(instance == null) instance = new AIPlayer();
//        return instance;
//    }

    public List<Coordinates> getShots() {
        return shots;
    }

    public AIPlayer() {
        initEnemyFleet();
    }

    @Override
    public Coordinates takeTarget() {

        shotNo++;
        if(damagedEnemy.isEmpty()) return randomTarget();
        else return targetOnDamaged();
    }

    @Override
    public void getReport(Coordinates coordinates, int result) {
        switch(result) {
            case RESULT_MISS:
                shots.add(coordinates);
                break;
            case RESULT_DAMAGE:
                shipIsDamaged(coordinates);
                break;
            case RESULT_DROWN:
                shipIsDrowned(coordinates);
                break;
        }
    }

    private void shipIsDrowned(Coordinates coordinates) {
        hits.add(coordinates);
        damagedEnemy.add(coordinates);
        for(Coordinates c: damagedEnemy) neighbours.addAll(c.getZone());
        damagedEnemy = new ArrayList<Coordinates>();
    }
    private void shipIsDamaged(Coordinates coordinates) {
        hits.add(coordinates);
        damagedEnemy.add(coordinates);
    }

    private Coordinates randomTarget() {

        Coordinates shot = null;
        do {
            int r = random.nextInt(rows);
            int c = random.nextInt(columns);
            shot = new Coordinates(r, c);
        }
        while(cellIsChecked(shot));
        return shot;
    }

    private Coordinates shotVertical(Coordinates coordinates) {
        if(coordinates.nextVertical(rows - 1) == null || cellIsChecked(coordinates.nextVertical())) return coordinates.prevVertical();
        if(coordinates.prevVertical(0) == null || cellIsChecked(coordinates.prevVertical())) return coordinates.nextVertical();

        boolean down = random.nextBoolean();
        if(down) return coordinates.nextVertical();
        return coordinates.prevVertical();
    }

    private Coordinates shotHorizontal(Coordinates coordinates) {
        if(coordinates.nextHorizontal(columns - 1) == null || cellIsChecked(coordinates.nextHorizontal())) return coordinates.prevHorizontal();
        if(coordinates.prevHorizontal(0) == null || cellIsChecked(coordinates.prevHorizontal())) return coordinates.nextHorizontal();

        boolean right = random.nextBoolean();
        if(right) return coordinates.nextHorizontal();
        return coordinates.prevHorizontal();
    }

    private Coordinates shotLower() {
        Coordinates coordinates = damagedEnemy.get(0);
        int low = coordinates.getRow();
        for(Coordinates c: damagedEnemy) {
            if(c.getRow() > low) {
                low = c.getRow();
                coordinates = c;
            }
        }
        return coordinates.nextVertical(rows - 1);
    }

    private Coordinates shotUpper() {
        Coordinates coordinates = damagedEnemy.get(0);
        int up = coordinates.getRow();
        for (Coordinates c: damagedEnemy) {
            if(c.getRow() < up) {
                up = c.getRow();
                coordinates = c;
            }
        }
        return coordinates.prevVertical(0);
    }

    private Coordinates shotRighter() {
        Coordinates coordinates = damagedEnemy.get(0);
        int right = coordinates.getCol();
        for(Coordinates c: damagedEnemy) {
            if(c.getCol() > right) {
                right = c.getCol();
                coordinates = c;
            }
        }
        return coordinates.nextHorizontal(columns - 1);
    }

    private Coordinates shotLefter() {
        Coordinates coordinates = damagedEnemy.get(0);
        int left = coordinates.getCol();
        for(Coordinates c: damagedEnemy) {
            if(c.getCol() < left) {
                left = c.getCol();
                coordinates = c;
            }
        }
        return coordinates.prevHorizontal(0);
    }

    private Coordinates targetOnDamaged() {

        if(damagedEnemy.size() == 1) {
            Coordinates coordinates = damagedEnemy.get(0);

            if(measureHorizontalSpace(coordinates, true) < Math.max(2, minLength)) {
                System.out.println("WE HAVE TO SHOT VERTICALLY");
                return shotVertical(coordinates);
            }

            if(measureVerticalSpace(coordinates, true) < Math.max(2, minLength)) {
                System.out.println("WE HAVE TO SHOT HORIZONTALLY");
                return shotHorizontal(coordinates);
            }

            boolean vertical = random.nextBoolean();
            if(vertical) {
                System.out.println("Random vertical shot");
                return shotVertical(coordinates);
            }
            else {
                System.out.println("Random Horizontal shot");
                return shotHorizontal(coordinates);
            }
        }
        else {
            if(isDamagedVertical()) {
                if(shotUpper() == null || cellIsChecked(shotUpper())) return shotLower();
                if(shotLower() == null || cellIsChecked(shotLower())) return shotUpper();

                boolean up = random.nextBoolean();
                if(up) return shotUpper();
                return shotLower();
            }
            else {
                if(shotLefter() == null || cellIsChecked(shotLefter())) return shotRighter();
                if(shotRighter() == null || cellIsChecked(shotRighter())) return shotLefter();

                boolean right = random.nextBoolean();
                if(right) return shotRighter();
                return shotLefter();
            }
        }

    }

    private boolean isDamagedVertical() {
        if(damagedEnemy.get(0).getCol() == damagedEnemy.get(1).getCol()) return true;
        return false;
    }

    private int measureHorizontalSpace(Coordinates coordinates) {
        int horizontalSpace = 0;
        if(!cellIsChecked(coordinates)) {
            horizontalSpace++;
            int maxCol = columns - 1;
            Coordinates next = coordinates.nextHorizontal(maxCol);
            while (next != null && !cellIsChecked(next)) {
                horizontalSpace++;
                next = next.nextHorizontal(maxCol);
            }
            Coordinates prev = coordinates.prevHorizontal(0);
            while (prev != null && !cellIsChecked(prev)) {
                horizontalSpace++;
                prev = prev.prevHorizontal(0);
            }
        }
        return horizontalSpace;
    }

    private int measureHorizontalSpace(Coordinates coordinates, boolean checked) {
        int horizontalSpace = 1;
        int maxCol = columns - 1;
        Coordinates next = coordinates.nextHorizontal(maxCol);
        System.out.println("NEXT COORDINATES ARE " + next);
        while (next != null && !cellIsChecked(next)) {
            System.out.println("NEXT = " + next + " and IsChecked = " + cellIsChecked(next));
            horizontalSpace++;
            next = next.nextHorizontal(maxCol);
        }
        Coordinates prev = coordinates.prevHorizontal(0);
        System.out.println("PREV COORDINATES ARE " + prev);
        while (prev != null && !cellIsChecked(prev)) {
            System.out.println("PREV = " + prev + " and IsChecked = " + cellIsChecked(prev));
            horizontalSpace++;
            prev = prev.prevHorizontal(0);
        }
        System.out.println("Horizontal Space Left is " + horizontalSpace);
        return horizontalSpace;
    }

    private int measureVerticalSpace(Coordinates coordinates) {
        int verticalSpace = 0;
        if(!cellIsChecked(coordinates)) {
            verticalSpace++;
            int maxRow = rows - 1;
            Coordinates next = coordinates.nextVertical(maxRow);
            while (next != null && !cellIsChecked(next)) {
                verticalSpace++;
                next = next.nextVertical(maxRow);
            }
            Coordinates prev = coordinates.prevVertical(0);
            while (prev != null && !cellIsChecked(prev)) {
                verticalSpace++;
                prev = prev.prevVertical(0);
            }
        }
        return verticalSpace;
    }

    private int measureVerticalSpace(Coordinates coordinates, boolean checked) {
        int verticalSpace = 1;

        int maxRow = rows - 1;
        Coordinates next = coordinates.nextVertical(maxRow);
        System.out.println("NEXT COORDINATES ARE " + next);
        while (next != null && !cellIsChecked(next)) {
            System.out.println("NEXT = " + next + " and IsChecked = " + cellIsChecked(next));
            verticalSpace++;
            next = next.nextVertical(maxRow);
        }
        Coordinates prev = coordinates.prevVertical(0);
        System.out.println("PREV COORDINATES ARE " + prev);
        while (prev != null && !cellIsChecked(prev)) {
            System.out.println("PREV = " + prev + " and IsChecked = " + cellIsChecked(prev));
            verticalSpace++;
            prev = prev.prevVertical(0);
        }
        System.out.println("Vertical Space Left is " + verticalSpace);
        return verticalSpace;
    }

    private boolean canBeTargeted(Coordinates coordinates) {
        if(measureHorizontalSpace(coordinates) < minLength && measureVerticalSpace(coordinates) < minLength) return false;
        return true;
    }

    private boolean cellIsChecked(Coordinates coordinates) {
        if(shots.contains(coordinates) || hits.contains(coordinates) || neighbours.contains(coordinates)) {
            return true;
        }
        return false;
    }

    private void initEnemyFleet() {
        enemyShipMap = new HashMap<Integer, Integer>();
        enemyShipMap.put(1, 4);
        enemyShipMap.put(2, 3);
        enemyShipMap.put(3, 2);
        enemyShipMap.put(4, 1);
        minLength = 1;
    }

    private void sink(int decks) {
        int num = enemyShipMap.get(decks);
        if(num == 1) {
            enemyShipMap.remove(decks);
            if(decks == minLength) {
                minLength = Collections.min(enemyShipMap.keySet());
            }
        }
        else {
            enemyShipMap.put(decks, --num);
        }
    }
}
