package com.example.birdsofafeather.model.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "sessions")
public class Session {

    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    public String sessionId;

    @TypeConverters(StringConverter.class)
    @ColumnInfo(name = "people")
    public List<String> peopleIDs;

    @ColumnInfo(name = "sessionName")
    public String sessionName;

    public Session(String sessionId, String sessionName){
        this.sessionId = sessionId;
        this.sessionName = sessionName;
        this.peopleIDs = new ArrayList<String>();
    }
}
