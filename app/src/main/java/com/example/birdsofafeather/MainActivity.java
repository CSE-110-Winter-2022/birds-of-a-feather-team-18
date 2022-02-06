package com.example.birdsofafeather;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.example.birdsofafeather.model.DummyPerson;
import com.example.birdsofafeather.model.IPerson;
import com.example.birdsofafeather.model.db.AppDatabase;

public class MainActivity extends AppCompatActivity {
    protected RecyclerView personsRecyclerView;
    protected RecyclerView.LayoutManager personsLayoutManager;
    protected com.example.birdsofafeather.PersonsViewAdapter personsViewAdapter;

    protected IPerson[] data = {
            new DummyPerson(0, "Jane Doe", new String[]{
                    "Likes cats.",
                    "Favorite color is blue."
            }),
            new DummyPerson(1, "John Public", new String[]{
                    "Likes dogs.",
                    "Favorite color is red."
            }),
            new DummyPerson(2, "Richard Roe", new String[]{
                    "Likes birds.",
                    "Favorite color is yellow."
            })}
    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_title);

        AppDatabase db = AppDatabase.singleton(getApplicationContext());
        List<? extends IPerson> persons = db.personWithNotesDao().getAll();

        personsRecyclerView = findViewById(R.id.persons_view);

        personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);

        personsViewAdapter = new PersonsViewAdapter(persons);
        personsRecyclerView.setAdapter(personsViewAdapter);
    }
}