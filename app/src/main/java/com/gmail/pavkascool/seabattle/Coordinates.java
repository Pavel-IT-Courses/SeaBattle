package com.gmail.pavkascool.seabattle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Coordinates {

    private int[] coords = new int[2];

    public Coordinates(int r, int c) {
        coords[0] = r;
        coords[1] = c;
    }

    public int[] getCoords() {
        return coords;
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
}
