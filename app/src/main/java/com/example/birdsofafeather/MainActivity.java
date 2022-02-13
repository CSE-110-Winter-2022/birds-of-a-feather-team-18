package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.model.DummyPerson;
import com.example.birdsofafeather.model.IPerson;
import com.example.birdsofafeather.model.db.AppDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    protected RecyclerView personsRecyclerView;
    protected RecyclerView.LayoutManager personsLayoutManager;
    protected PersonsViewAdapter personsViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_title);


        AppDatabase db = AppDatabase.singleton(this);

        //clear database of all persons and courses
        db.coursesDao().deleteExceptUser(1);
        db.personWithCoursesDao().deleteExceptUser(1);

        //if the database is empty, start the login activity
        if(db.personWithCoursesDao().count() == 0){
            Intent intent = new Intent(this, NameLoginActivity.class);
            startActivity(intent);
        }
        /*
        List<? extends IPerson> persons = db.personWithCoursesDao().getAll();

        personsRecyclerView = findViewById(R.id.persons_view);

        personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);

        personsViewAdapter = new PersonsViewAdapter(persons);
        personsRecyclerView.setAdapter(personsViewAdapter);
         */

    }

    public void onTestClicked(View view) {
        Intent intent = new Intent(this, PrevCourseActivity.class);
        startActivity(intent);
    }
}