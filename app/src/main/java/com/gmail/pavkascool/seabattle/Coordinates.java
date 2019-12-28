package com.gmail.pavkascool.seabattle;

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
}
