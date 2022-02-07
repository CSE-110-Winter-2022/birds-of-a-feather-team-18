package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.model.DummyPerson;
import com.example.birdsofafeather.model.IPerson;
import com.example.birdsofafeather.model.db.AppDatabase;

public class MainActivity extends AppCompatActivity {
    protected RecyclerView personsRecyclerView;
    protected RecyclerView.LayoutManager personsLayoutManager;

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


        AppDatabase db = AppDatabase.singleton(this);
        if(db.personWithCoursesDao().count() == 0){
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }


    }

    public void onTestClicked(View view) {
        Intent intent = new Intent(this, PrevCourseActivity.class);
        startActivity(intent);
    }
}