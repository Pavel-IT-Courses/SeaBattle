package com.gmail.pavkascool.update;

import com.gmail.pavkascool.update.utils.Coordinates;

import java.util.List;

public class HumanPlayer implements Player {
    private String name;
    private Connector connector;

    public HumanPlayer() {
        connector = Connector.getInstance();
    }

    @Override
    public Coordinates takeTarget() {
        return null;
    }

    @Override
    public void getReport(Coordinates coordinates, int result) {

    }

    @Override
    public List<Coordinates> getShots() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
