package com.gmail.pavkascool.seabattle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Configuration {
    private List<CellView> ships;
    private List<Coordinates> shots;
    private List<Coordinates> hits;
    private Set<Coordinates> neighbours;

    private List<CellView> enemyShips;
    private List<Coordinates> enemyShots;
    private List<Coordinates> enemyHits;
    private Set<Coordinates> enemyNeighbours;

    private CellViewLayout friend;
    private CellViewLayout enemy;

    private int fleet;
    private int enemyFleet;

    private boolean isYourTurn;

    private int turn, shot;

    private boolean isOver, yourVictory;

    public Configuration() {
        ships = new ArrayList<CellView>();
        hits = new ArrayList<Coordinates>();
        shots = new ArrayList<Coordinates>();
        neighbours = new HashSet<Coordinates>();
        enemyShips = new ArrayList<CellView>();
        enemyHits = new ArrayList<Coordinates>();
        enemyShots = new ArrayList<Coordinates>();
        enemyNeighbours = new HashSet<Coordinates>();
        fleet = 10;
        enemyFleet = 10;
        Random random = new Random();
        isYourTurn = random.nextBoolean();
        if(isYourTurn) {
            turn = 1;
        }

    }

    public void sinkEnemy() {
        enemyFleet--;
    }
    public void sink() {
        fleet--;
    }

    public boolean isYourTurn() {
        return isYourTurn;
    }

    public void setYourTurn(boolean yourTurn) {
        isYourTurn = yourTurn;
    }

    public int getTurnNumber() {
        return turn;
    }

    public void incrementTurn() {
        turn++;
    }

    public int getShotNumber() {
        return shot;
    }

    public void incrementShot() {
        shot++;
    }

    public boolean isOver() {
        return isOver;
    }

    public void setOver(boolean over) {
        isOver = over;
    }

    public boolean isYourVictory() {
        return yourVictory;
    }

    public void setYourVictory(boolean yourVictory) {
        isOver = true;
        this.yourVictory = yourVictory;
    }

    public void addShip(CellView ship) {
        fleet++;
        ships.add(ship);
    }
    public void addEnemyHit(Coordinates coordinates) {
        enemyHits.add(coordinates);
        notifyEnemy();
    }
    public void addEnemyShot(Coordinates coordinates) {
        enemyShots.add(coordinates);
        notifyEnemy();
    }

    public void addEnemyNeighbours(Set<Coordinates> neighbour) {
        enemyNeighbours.addAll(neighbour);
        notifyEnemy();
    }

    public void addHit(Coordinates coordinates) {
        hits.add(coordinates);
        notifyFriend();
    }

    public void addShot(Coordinates coordinates) {
        shots.add(coordinates);
        notifyFriend();
    }
    public void addNeighbours(Set<Coordinates> neighbour) {
        neighbours.addAll(neighbour);
        notifyFriend();
    }

    public List<Coordinates> getEnemyHits() {
        return enemyHits;
    }
    public void addEnemy(CellView enemy) { enemyShips.add(enemy); }

    public List<CellView> getShips() {
        return ships;
    }

    public void setShips(List<CellView> ships) {
        this.ships = ships;
    }

    public List<Coordinates> getShots() {
        return shots;
    }

    public void setShots(List<Coordinates> shots) {
        this.shots = shots;
    }

    public Set<Coordinates> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(Set<Coordinates> neighbours) {
        this.neighbours = neighbours;
    }

    public List<CellView> getEnemyShips() {
        return enemyShips;
    }

    public void setEnemyShips(List<CellView> enemyShips) {
        this.enemyShips = enemyShips;
    }

    public int getFleet() {
        return fleet;
    }

    public void damaged() {
        enemyFleet--;
    }

    public void setFleet(int fleet) {
        this.fleet = fleet;
    }

    public int getEnemyFleet() {
        return enemyFleet;
    }

    public void setEnemyFleet(int enemyFleet) {
        this.enemyFleet = enemyFleet;
    }

    public List<Coordinates> getHits() {
        return hits;
    }

    public void setHits(List<Coordinates> hits) {
        this.hits = hits;
    }

    public List<Coordinates> getEnemyShots() {
        return enemyShots;
    }

    public void setEnemyShots(List<Coordinates> enemyShots) {
        this.enemyShots = enemyShots;
    }

    public void setEnemyHits(List<Coordinates> enemyHits) {
        this.enemyHits = enemyHits;
    }

    public Set<Coordinates> getEnemyNeighbours() {
        return enemyNeighbours;
    }

    public void setEnemyNeighbours(Set<Coordinates> enemyNeighbours) {
        this.enemyNeighbours = enemyNeighbours;
    }



    public void setAsFriend(CellViewLayout friend) {
        this.friend = friend;
        notifyFriend();
    }
    public void setAsEnemy(CellViewLayout enemy) {
        this.enemy = enemy;
        notifyEnemy();
    }
    public void notifyFriend() {
        friend.setShots(shots);
        friend.setHits(hits);
        friend.setNeighbours(neighbours);
        friend.invalidate();
    }
    public void notifyEnemy() {
        enemy.setShots(enemyShots);
        enemy.setHits(enemyHits);
        enemy.setNeighbours(enemyNeighbours);
        enemy.invalidate();
    }
}


