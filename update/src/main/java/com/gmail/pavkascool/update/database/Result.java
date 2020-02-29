package com.gmail.pavkascool.update.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Result {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String date;
    private String winner;
    private String turns;

    public Result(String date, String winner, String turns) {
        this.date = date;
        this.winner = winner;
        this.turns = turns;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public String getWinner() {
        return winner;
    }

    public String getTurns() {
        return turns;
    }

}
