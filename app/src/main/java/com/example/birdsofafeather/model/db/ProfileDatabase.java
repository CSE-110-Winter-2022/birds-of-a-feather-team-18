package com.example.birdsofafeather.model.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Person.class, Class.class}, version = 1)
public abstract class ProfileDatabase extends RoomDatabase {
    private static ProfileDatabase singletonInstance;

    public static ProfileDatabase singleton(Context context) {
        if(singletonInstance == null) {
            singletonInstance = Room.databaseBuilder(context, ProfileDatabase.class, "persons.db")
                    .build();
        }

        return singletonInstance;
    }

    public abstract PersonWithClassesDao PersonWithClassesDao();
    public abstract ClassesDao ClassesDao();
}
