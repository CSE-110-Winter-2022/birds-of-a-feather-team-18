package com.example.birdsofafeather.model.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "persons")
public class Person {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    public String personId;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "photo")
    public String photo;

    @ColumnInfo(name = "sizePriority")
    public float sizePriority;

    @ColumnInfo(name = "recentPriority")
    public int recentPriority;

    @ColumnInfo(name = "favorite")
    public boolean favorite;

    @ColumnInfo(name = "wavingToThem")
    public boolean wavingToThem;

    @ColumnInfo(name = "wavingToUs")
    public boolean wavingToUs;

    public Person(String personId, String name, String photo, boolean favorite){
        this.personId = personId;
        this.name = name;
        this.photo = photo;
        sizePriority = 0;
        recentPriority = 0;
        this.favorite = favorite;
        this.wavingToThem = false;
        this.wavingToUs = false;
    }
}
