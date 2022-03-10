package com.example.birdsofafeather.model.db;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

import com.example.birdsofafeather.model.IPerson;

public class PersonWithCourses implements IPerson{
    @Embedded
    public Person person;

    @Relation(parentColumn = "id",
            entityColumn = "person_id",
            entity = Course.class,
            projection = {"text"})
    public List<String> courses;

    @Override
    public String getId(){
        return this.person.personId;
    }

    @Override
    public String getName() {
        return this.person.name;
    }

    @Override
    public String getPhoto() { return this.person.photo; }

    @Override
    public List<String> getCourses() {
        return this.courses;
    }

    @Override
    public boolean getFavorite(){
        return this.person.favorite;
    }

    @Override
    public boolean getWavingToThem() {return this.person.wavingToThem; }

    @Override
    public boolean getWavingToUs() {return this.person.wavingToUs; }
}
