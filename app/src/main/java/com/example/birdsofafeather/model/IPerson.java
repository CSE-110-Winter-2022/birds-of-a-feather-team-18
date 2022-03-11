package com.example.birdsofafeather.model;

import java.util.List;

public interface IPerson {
    String getId();
    String getName();
    String getPhoto();
    List<String> getCourses();
    boolean getFavorite();
    boolean getWavingToThem();
    boolean getWavingToUs();
}
