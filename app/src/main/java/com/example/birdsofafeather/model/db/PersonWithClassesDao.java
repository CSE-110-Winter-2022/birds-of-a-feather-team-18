package com.example.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface PersonWithClassesDao {
    @Transaction
    @Query("SELECT * FROM persons")
    List<PersonWithClasses> getAll();

    @Query("SELECT * FROM persons WHERE id=:id")
    PersonWithClasses get(int id);

    @Query("SELECT COUNT(*) from persons")
    int count();
}
