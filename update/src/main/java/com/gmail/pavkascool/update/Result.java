package com.gmail.pavkascool.update;

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

    public void setDate(String date) {
        this.date = date;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getTurns() {
        return turns;
    }

    public void setTurns(String turns) {
        this.turns = turns;
    }
}
