package com.example.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface PersonWithCoursesDao {
    @Transaction
    @Query("SELECT * FROM persons")
    List<PersonWithCourses> getAll();

    @Query("SELECT * FROM persons WHERE id=:id")
    PersonWithCourses get(String id);

    @Query("SELECT COUNT(*) from persons")
    int count();

    /*
    @Query("SELECT MAX(id) from persons")
    int maxId();
    */

    @Query("DELETE FROM persons")
    void deleteAll();

    @Insert
    void insert(Person person);

    @Delete
    void delete(Person person);

    @Query("DELETE FROM persons WHERE id !=:id")
    void deleteExceptUser(String id);

    @Query("UPDATE persons SET photo = :newPhoto WHERE id =:id")
    void updatePhoto(String newPhoto, String id);

    @Query("UPDATE persons SET favorite = :isFavorite WHERE id =:id")
    void updateFavorite(boolean isFavorite, String id);
}
