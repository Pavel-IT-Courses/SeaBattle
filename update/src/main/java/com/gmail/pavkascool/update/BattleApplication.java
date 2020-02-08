package com.gmail.pavkascool.update;

import android.app.Application;

import androidx.room.Room;

public class BattleApplication extends Application {

    private static BattleApplication instance;
    private boolean againstAI;
    private BattleDatabase db;

    public boolean isAgainstAI() {
        return againstAI;
    }

    public void setAgainstAI(boolean againstAI) {
        this.againstAI = againstAI;
    }

    public static BattleApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        db = Room.databaseBuilder(this, BattleDatabase.class, "battledatabase").build();
    }

    public BattleDatabase getBattleDatabase() {
        return db;
    }
}
