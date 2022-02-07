package com.example.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface PersonWithCoursesDao {
    @Transaction
    @Query("SELECT * FROM persons")
    List<com.example.birdsofafeather.model.db.PersonWithCourses> getAll();

    @Query("SELECT * FROM persons WHERE id=:id")
    com.example.birdsofafeather.model.db.PersonWithCourses get(int id);

    @Query("SELECT COUNT(*) from persons")
    int count();

    @Insert
    void insert(Person person);
}
