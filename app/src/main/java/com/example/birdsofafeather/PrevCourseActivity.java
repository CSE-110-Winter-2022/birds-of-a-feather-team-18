package com.example.birdsofafeather;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.birdsofafeather.model.IPerson;
import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;
import com.example.birdsofafeather.model.db.Person;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class PrevCourseActivity extends AppCompatActivity {
    private AppDatabase db;
    private int personId;
    private int currCourseCount;

    private RecyclerView coursesRecyclerView;
    private RecyclerView.LayoutManager coursesLayoutManager;
    private CoursesViewAdapter coursesViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prev_course);
        // Get database
        db = AppDatabase.singleton(getApplicationContext());

        // get user ID (always 1)
        personId = 1;
        List<Course> courses = db.coursesDao().getForPerson(personId);

        // Quarter Drop-down functionality
        Spinner spinnerQuarter = findViewById(R.id.quarter_spinner);
        ArrayAdapter<CharSequence> quarterArrAdapter = ArrayAdapter.createFromResource
                (this,R.array.quarter, android.R.layout.simple_spinner_item);
        quarterArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerQuarter.setAdapter(quarterArrAdapter);

        //Year Drop-down functionality
        Spinner spinnerYear = findViewById(R.id.year_spinner);
        ArrayList<String> yearList = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for(int y = thisYear - 10; y <= thisYear; y++){
            yearList.add(Integer.toString(y));
        }
        ArrayAdapter<String> yearArrAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yearList);
        spinnerYear.setAdapter(yearArrAdapter);

        // Class Size Drop-down functionality
        Spinner spinnerSize = findViewById(R.id.class_size_spinner);
        ArrayAdapter<CharSequence> sizeArrAdapter = ArrayAdapter.createFromResource
                (this,R.array.size, android.R.layout.simple_spinner_item);
        sizeArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerSize.setAdapter(sizeArrAdapter);


        // Course RecyclerView
        coursesRecyclerView = findViewById(R.id.prev_courses_view);
        coursesLayoutManager = new LinearLayoutManager(this);
        coursesRecyclerView.setLayoutManager(coursesLayoutManager);
        coursesViewAdapter = new CoursesViewAdapter(courses);
        coursesRecyclerView.setAdapter(coursesViewAdapter);
    }

    public void onAddPrevCourseClicked(View view){
        // get unique courseID
        int newCourseId = db.coursesDao().maxId()+1;

        // get TextView/Spinner for Year/Quarter/Subject/CourseNum
        TextView newSubjectTextView = findViewById(R.id.subject_view);
        Spinner newYearSpinnerView = findViewById(R.id.year_spinner);
        Spinner newQuarterSpinnerView = findViewById(R.id.quarter_spinner);
        Spinner newSizeSpinnerView = findViewById(R.id.class_size_spinner);
        TextView newCourseNumTextView = findViewById(R.id.course_number_view);

        // get String for Year/Quarter/Subject/CourseNum
        String newSubjectText = newSubjectTextView.getText().toString();
        String newYearText = newYearSpinnerView.getSelectedItem().toString();
        String newQuarterText = newQuarterSpinnerView.getSelectedItem().toString();
        String newSizeText = newSizeSpinnerView.getSelectedItem().toString();
        String newCourseNumText = newCourseNumTextView.getText().toString();

        switch(newSizeText) {
            case "Tiny (<40)":
                newSizeText = "Tiny";
                break;
            case "Small (40-75)":
                newSizeText = "Small";
                break;
            case "Medium (75-150)":
                newSizeText = "Medium";
                break;
            case "Large (150-250)":
                newSizeText = "Large";
                break;
            case "Huge (250-400)":
                newSizeText = "Huge";
                break;
            case "Gigantic (400+)":
                newSizeText = "Gigantic";
                break;
            default:
                newSizeText = "";
                break;
        }

        String prevCourse =  newQuarterText + newYearText + " " + newSubjectText + " " + newCourseNumText;

        Course newCourse = new Course(newCourseId, personId, prevCourse, newYearText, newQuarterText, newSizeText);

        // Don't add duplicate classes
        List<Course> courses = db.coursesDao().getForPerson(personId);
        boolean alreadyInDatabase = false;
        for(int i=0; i<courses.size(); ++i) {
            if (courses.get(i).text.equals(prevCourse)) {
                alreadyInDatabase = true;
                break;
            }
        }
        if(!alreadyInDatabase){
            db.coursesDao().insert(newCourse);
            currCourseCount = db.coursesDao().getForPerson(personId).size();
            coursesViewAdapter.addCourse(newCourse);
        }
    }

    public void onDoneClicked(View view) {
        finish();
    }

    public int getCourseCount(){
        return currCourseCount;
    }

    public List<Course> getCourses() {
        List<Course> courses = db.coursesDao().getForPerson(personId);
        return courses;
    }

}