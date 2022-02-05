package com.example.birdsofafeather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.birdsofafeather.model.IPerson;
import com.example.birdsofafeather.model.db.ProfileDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ProfileDatabase db = ProfileDatabase.singleton(getApplicationContext());
        List<? extends IPerson> persons = db.PersonWithClassesDao().getAll();
    }
}