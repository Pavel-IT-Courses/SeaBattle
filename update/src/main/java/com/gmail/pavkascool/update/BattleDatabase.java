package com.gmail.pavkascool.update;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Result.class}, version = 1)
public abstract class BattleDatabase extends RoomDatabase {
    public abstract ResultDao resultDao();
}
