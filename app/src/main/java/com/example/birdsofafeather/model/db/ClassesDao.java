package com.example.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface ClassesDao {
    @Transaction
    @Query("SELECT * FROM classes where person_id=:personId")
    List<Class> getForPerson(int personId);

    @Query("SELECT * FROM classes where id=:id")
    Class get(int id);

    @Query("SELECT COUNT(*) from classes")
    int count();

    @Insert
    void insert(Class newClass);

    @Delete
    void delete(Class newClass);
}
