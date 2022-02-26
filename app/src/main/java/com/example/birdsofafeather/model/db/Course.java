package com.example.birdsofafeather.model.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "courses")
public class Course {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public int courseId;

    @ColumnInfo(name = "person_id")
    public int personId;

    @ColumnInfo(name = "text")
    public String text;

    @ColumnInfo(name = "size")
    public String size;

    public Course(int courseId, int personId, String text, String size){
        this.courseId = courseId;
        this.personId = personId;
        this.text = text;
        this.size = size;
    }
}
