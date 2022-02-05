package com.example.birdsofafeather.model.db;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "persons")
public class Person {
    @PrimaryKey
    @ColumnInfo(name = "id")
    public int personId;

    @ColumnInfo(name = "name")
    public String name;
}