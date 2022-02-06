package com.example.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface PersonWithNotesDao {
    @Transaction
    @Query("SELECT * FROM persons")
    List<com.example.birdsofafeather.model.db.PersonWithNotes> getAll();

    @Query("SELECT * FROM persons WHERE id=:id")
    com.example.birdsofafeather.model.db.PersonWithNotes get(int id);

    @Query("SELECT COUNT(*) from persons")
    int count();
}
