package com.gmail.pavkascool.update;

import android.app.Application;

import com.gmail.pavkascool.update.database.BattleDatabase;
import com.gmail.pavkascool.update.delegates.StartModel;

import androidx.room.Room;

public class BattleApplication extends Application {

    private static BattleApplication instance;
    private boolean againstAI;
    private BattleDatabase db;
    private StartModel startModel;

    public boolean isAgainstAI() {
        return againstAI;
    }

    public void setAgainstAI(boolean againstAI) {
        this.againstAI = againstAI;
    }

    public static BattleApplication getInstance() {
        return instance;
    }

    public StartModel getStartModel() {
        return startModel;
    }

    public void setStartModel(StartModel startModel) {
        this.startModel = startModel;
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
