package com.gmail.pavkascool.update;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ResultDao {

    @Query("SELECT * FROM RESULT")
    List<Result> getAll();

    @Query("SELECT COUNT(*) FROM RESULT WHERE WINNER='YOU'")
    int victories();

    @Query("SELECT COUNT(*) FROM RESULT WHERE WINNER!='YOU'")
    int defeats();

    @Insert
    long insert(Result result);

    @Delete
    int delete(Result result);
}
