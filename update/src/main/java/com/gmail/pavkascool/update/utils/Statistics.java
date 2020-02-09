package com.gmail.pavkascool.update.utils;

import com.gmail.pavkascool.update.database.Result;

import java.util.List;

public class Statistics {

    private List<Result> results;
    private int victories, defeats;

    public Statistics(List<Result> results, int victories, int defeats) {
        this.results = results;
        this.victories = victories;
        this.defeats = defeats;
    }

    public List<Result> getResults() {
        return results;
    }

    public int getVictories() {
        return victories;
    }

    public int getDefeats() {
        return defeats;
    }
}
