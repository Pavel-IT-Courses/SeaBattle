package com.gmail.pavkascool.update.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Result.class}, version = 1)
public abstract class BattleDatabase extends RoomDatabase {
    public abstract ResultDao resultDao();
}
