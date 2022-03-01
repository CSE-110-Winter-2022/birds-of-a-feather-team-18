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

    @ColumnInfo(name = "photo")
    public String photo;

    @ColumnInfo(name = "sizePriority")
    public float sizePriority;

    @ColumnInfo(name = "recentPriority")
    public int recentPriority;

    public Person(int personId, String name, String photo){
        this.personId = personId;
        this.name = name;
        this.photo = photo;
        sizePriority = 0;
        recentPriority = 0;
    }
}
