package com.example.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface CoursesDao {
    @Transaction
    @Query("SELECT * FROM courses where person_id=:personId")
    List<Course> getForPerson(String personId);

    @Query("SELECT * FROM courses where id=:id")
    Course get(String id);

    @Query("SELECT COUNT(*) from courses")
    int count();

    @Query("SELECT MAX(id) from courses")
    int maxId();

    @Query("DELETE FROM courses")
    void deleteAll();

    @Query("DELETE FROM courses WHERE person_id !=:id")
    void deleteExceptUser(String id);

    @Insert
    void insert(Course course);

    @Delete
    void delete(Course course);
}
