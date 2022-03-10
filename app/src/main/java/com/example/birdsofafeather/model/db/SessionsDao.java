package com.example.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.TypeConverters;

import java.util.List;

@Dao
public interface SessionsDao {

    @Query("SELECT * FROM sessions")
    List<Session> getAll();

    @Query("SELECT * FROM sessions where id=:id")
    Session get(String id);

    @Query("SELECT COUNT(*) from sessions")
    int count();

    @Query("SELECT MAX(id) from sessions")
    int maxId();

    @Insert
    void insert(Session session);

    @Delete
    void delete(Session session);

    @Query("UPDATE sessions SET sessionName =:newSessionName WHERE id=:id")
    void updateSessionName(String newSessionName, String id);
}
