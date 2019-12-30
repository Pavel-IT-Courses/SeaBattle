package com.gmail.pavkascool.seabattle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Configuration {
    private List<CellView> ships;
    private List<Coordinates> shots;
    private Set<Coordinates> neighbours;

    public Configuration() {
        ships = new ArrayList<CellView>();
        shots = new ArrayList<Coordinates>();
        neighbours = new HashSet<Coordinates>();
    }

    public void addShip(CellView ship) {
        ships.add(ship);
    }

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
}


