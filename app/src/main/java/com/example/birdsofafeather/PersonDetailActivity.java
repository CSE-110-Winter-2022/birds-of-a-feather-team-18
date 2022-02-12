package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.model.IPerson;
import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;

import java.util.List;


public class PersonDetailActivity extends AppCompatActivity {
    private AppDatabase db;
    private IPerson person;

    private RecyclerView coursesRecyclerView;
    private RecyclerView.LayoutManager coursesLayoutManager;
    private CoursesViewAdapter coursesViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        Intent intent = getIntent();
        int personId = intent.getIntExtra("person_id", 0);

        db = AppDatabase.singleton(this);
        person = db.personWithCoursesDao().get(personId);
        List<Course> courses = db.coursesDao().getForPerson(personId);

        setTitle(person.getName());

        coursesRecyclerView = findViewById(R.id.courses_view);
        coursesLayoutManager = new LinearLayoutManager(this);
        coursesRecyclerView.setLayoutManager(coursesLayoutManager);

        coursesViewAdapter = new CoursesViewAdapter(courses);
        coursesRecyclerView.setAdapter(coursesViewAdapter);
    }

    public void onAddCourseClicked(View view){
        int newCourseId = db.coursesDao().count()+1;
        int personId = person.getId();
        TextView newCourseTextView = findViewById(R.id.new_course_textview);
        String newCourseText = newCourseTextView.getText().toString();

        Course newCourse = new Course(newCourseId, personId, newCourseText);
        db.coursesDao().insert(newCourse);

        coursesViewAdapter.addCourse(newCourse);
    }

    public void onGoBackClicked(View view) {
        finish();
    }
}