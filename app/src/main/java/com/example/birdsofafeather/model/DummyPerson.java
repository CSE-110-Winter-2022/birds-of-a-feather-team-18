package com.example.birdsofafeather.model;

import java.util.Arrays;
import java.util.List;

public class DummyPerson implements com.example.birdsofafeather.model.IPerson {
    private final int id;
    private final String name;
    private final String photo;
    private final String[] courses;

    public DummyPerson(int id, String name, String photo, String[] courses) {
        this.id = id;
        this.name = name;
        this.photo = photo;
        this.courses = courses;
    }

    @Override
    public int getId(){
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPhoto() { return photo; }

    @Override
    public List<String> getCourses() {
        return Arrays.asList(courses);
    }
}
