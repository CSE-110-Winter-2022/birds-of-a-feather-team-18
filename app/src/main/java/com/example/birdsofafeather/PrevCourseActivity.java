package com.example.birdsofafeather;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.model.IPerson;
import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;
import com.example.birdsofafeather.model.db.Person;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.List;

public class PrevCourseActivity extends AppCompatActivity {
    private AppDatabase db;
    private IPerson person;

    private RecyclerView coursesRecyclerView;
    private RecyclerView.LayoutManager coursesLayoutManager;
    private CoursesViewAdapter coursesViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prev_course);
        AppDatabase db = AppDatabase.singleton(getApplicationContext());
        List<? extends IPerson> persons = db.personWithCoursesDao().getAll();

        int  newPersonWithNotesId = db.personWithCoursesDao().maxId() + 1;
        Person user = new Person(newPersonWithNotesId, "user");

        db.personWithCoursesDao().insert(user);



        Course[] courses = {new Course(0,0,"CSE110")};


        coursesRecyclerView = findViewById(R.id.prev_courses_view);
        coursesLayoutManager = new LinearLayoutManager(this);
        coursesRecyclerView.setLayoutManager(coursesLayoutManager);

        coursesViewAdapter = new CoursesViewAdapter(Arrays.asList(courses));


        coursesRecyclerView.setAdapter(coursesViewAdapter);





    }

    public void onAddPrevCourseClicked(View view){
        AppDatabase db = AppDatabase.singleton(getApplicationContext());
        int newCourseId = db.coursesDao().maxId()+1;
        int personId = 0;
        TextView newSubjectTextView = findViewById(R.id.subject_view);
        TextView newYearTextView = findViewById(R.id.year_view);
        TextView newQuarterTextView = findViewById(R.id.quarter_view);
        TextView newCourseNumTextView = findViewById(R.id.course_number_view);

        String newSubjectText = newSubjectTextView.getText().toString();
        String newYearText = newYearTextView.getText().toString();
        String newQuarterText = newQuarterTextView.getText().toString();
        String newCourseNumText = newCourseNumTextView.getText().toString();

        String prevCourse = newYearText + newQuarterText + newSubjectText + newCourseNumText;

        Course newCourse = new Course(newCourseId, personId, prevCourse);
        db.coursesDao().insert(newCourse);

        coursesViewAdapter.addCourse(newCourse);
    }

    public void onDoneClicked(View view) {
        finish();
    }
}