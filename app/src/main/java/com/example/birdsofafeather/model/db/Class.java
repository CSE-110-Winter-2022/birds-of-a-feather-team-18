package com.example.birdsofafeather.model.db;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "classes")
public class Class {
    @PrimaryKey
    @ColumnInfo(name = "id")
    public int classId;

    @ColumnInfo(name = "person_id")
    public int personId;

    @ColumnInfo(name = "text")
    public String text;

    public Class(int classId, int personId, String text){
        this.classId = classId;
        this.personId = personId;
        this.text = text;
    }
}
