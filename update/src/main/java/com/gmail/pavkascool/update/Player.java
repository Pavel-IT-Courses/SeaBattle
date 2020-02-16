package com.gmail.pavkascool.update;

import com.gmail.pavkascool.update.utils.Coordinates;

import java.util.List;

public interface Player {

    Coordinates takeTarget();
    void getReport(Coordinates coordinates, int result);
    List<Coordinates> getShots();
    String getName();
}
