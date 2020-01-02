package com.gmail.pavkascool.seabattle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Configuration {
    private List<CellView> ships;
    private List<Coordinates> shots;
    private Set<Coordinates> neighbours;
    private List<CellView> enemies;

    public Configuration() {
        ships = new ArrayList<CellView>();
        shots = new ArrayList<Coordinates>();
        neighbours = new HashSet<Coordinates>();
        enemies = new ArrayList<CellView>();
        System.out.println("ENEMIES = " + enemies);
    }

    public void addShip(CellView ship) {
        ships.add(ship);
    }
    public void addEnemy(CellView enemy) { enemies.add(enemy); }

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

    public List<CellView> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<CellView> enemies) {
        this.enemies = enemies;
    }
}


