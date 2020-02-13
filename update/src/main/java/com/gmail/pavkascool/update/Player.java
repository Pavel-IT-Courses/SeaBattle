package com.gmail.pavkascool.update;

import com.gmail.pavkascool.update.utils.Coordinates;

public interface Player {

    Coordinates takeTarget();
    void getReport(Coordinates coordinates, int result);
}
