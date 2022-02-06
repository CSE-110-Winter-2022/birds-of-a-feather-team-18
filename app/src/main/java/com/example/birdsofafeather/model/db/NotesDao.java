package com.example.birdsofafeather.model.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public interface NotesDao {
    @Transaction
    @Query("SELECT * FROM notes where person_id=:personId")
    List<Note> getForPerson(int personId);

    @Query("SELECT * FROM notes where id=:id")
    Note get(int id);

    @Query("SELECT COUNT(*) from notes")
    int count();

    @Insert
    void insert(Note note);

    @Delete
    void delete(Note note);
}
