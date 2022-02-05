package com.example.birdsofafeather.model.db;


import androidx.room.Embedded;
import androidx.room.Relation;

import com.example.birdsofafeather.model.IPerson;

import java.util.List;

public class PersonWithClasses implements IPerson {
    @Embedded
    public Person person;

    @Relation(parentColumn = "id",
            entityColumn = "person_id",
            entity = Class.class,
            projection = {"text"})
    public List<String> classes;

    @Override
    public int getId(){
        return this.person.personId;
    }

    @Override
    public String getName() {
        return this.person.name;
    }

    @Override
    public List<String> getClasses() {
        return this.classes;
    }
}
