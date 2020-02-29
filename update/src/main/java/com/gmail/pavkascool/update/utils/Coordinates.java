package com.gmail.pavkascool.update.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;

public class Coordinates {
    private int[] coords = new int[2];

    public Coordinates(int r, int c) {
        coords[0] = r;
        coords[1] = c;
    }

    public int getRow() {
        return coords[0];
    }
    public int getCol() {
        return coords[1];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinates that = (Coordinates) o;
        return Arrays.equals(coords, that.coords);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(coords);
    }

    public Set<Coordinates> getZone() {
        Set<Coordinates> zone = new HashSet<Coordinates>();
        zone.add(this);
        zone.add(new Coordinates(coords[0]-1, coords[1]));
        zone.add(new Coordinates(coords[0]+1, coords[1]));
        zone.add(new Coordinates(coords[0], coords[1]-1));
        zone.add(new Coordinates(coords[0], coords[1]+1));
        zone.add(new Coordinates(coords[0]-1, coords[1]-1));
        zone.add(new Coordinates(coords[0]-1, coords[1]+1));
        zone.add(new Coordinates(coords[0]+1, coords[1]+1));
        zone.add(new Coordinates(coords[0]+1, coords[1]-1));

        return zone;
    }

    @NonNull
    @Override
    public String toString() {
        return " " + getRow() + " " + getCol();
    }

    public Coordinates nextHorizontal() {
        return new Coordinates(getRow(), getCol() + 1);
    }

    public Coordinates nextHorizontal(int maxCol) {
        if(getCol() + 1 > maxCol) return null;
        return new Coordinates(getRow(), getCol() + 1);
    }

    public Coordinates prevHorizontal() {
        return new Coordinates(getRow(), getCol() - 1);
    }

    public Coordinates prevHorizontal(int minCol) {
        if(getCol() - 1 < minCol) return null;
        return new Coordinates(getRow(), getCol() - 1);
    }

    public Coordinates nextVertical() {
        return new Coordinates(getRow() + 1, getCol());
    }

    public Coordinates nextVertical(int maxRow) {
        if(getRow() + 1 > maxRow) return null;
        return new Coordinates(getRow() + 1, getCol());
    }

    public Coordinates prevVertical() {
        return new Coordinates(getRow() - 1, getCol());
    }

    public Coordinates prevVertical(int minRow) {
        if(getRow() - 1 < minRow) return null;
        return new Coordinates(getRow() - 1, getCol());
    }
}
