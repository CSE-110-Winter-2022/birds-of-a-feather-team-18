package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.model.DummyPerson;
import com.example.birdsofafeather.model.IPerson;
import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;
import com.example.birdsofafeather.model.db.Person;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

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
        db.coursesDao().deleteAll();
        db.personWithCoursesDao().deleteAll();

        //if the database is empty, start the login activity
        if (db.personWithCoursesDao().count() == 0) {
            Intent intent = new Intent(this, NameLoginActivity.class);
            startActivity(intent);
        }

    }

    public void onTestClicked(View view) {
        /*Intent intent = new Intent(this, PrevCourseActivity.class);
        startActivity(intent);*/
        Intent intent = new Intent(this, PersonListActivity.class);
        startActivity(intent);
    }

    public void onEnterClicked(View view) {
        TextView profile = findViewById(R.id.student_profile);
        String csvReader = "\\Users\\55369\\Desktop\\Study\\mock.csv";
        String test = "Bill,,,\n" +
                "https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0,,,\n" +
                "2021,FA,CSE,210\n" +
                "2022,WI,CSE,110\n" +
                "2022,SP,CSE,110";
        Scanner scanner = new Scanner(test);
        AppDatabase db = AppDatabase.singleton(this);
        String line = "";
        String csvSplitBy = ",";

        int count = 0;
        String name = null;
        String photoId = null;
        String year;
        String quarter;
        String courseType;
        String courseNum;
        String text;
        int personId = db.personWithCoursesDao().maxId() + 1;
        while (scanner.hasNextLine()) {
            String[] array = scanner.nextLine().split(csvSplitBy);
            if (count == 0) {
                name = array[0];
                count++;
            } else if (count == 1) {
                photoId = array[0];
                count++;

            } else {
                year = array[0];
                quarter = array[1];
                courseType = array[2];
                courseNum = array[3];
                text = quarter + year + ' ' + courseType + ' ' + courseNum;
                int courseId = db.coursesDao().maxId() + 1;
                Course c = new Course(courseId, personId, text);
                db.coursesDao().insert(c);
            }
        }
        Person newPerson = new Person(personId, name, photoId);
        db.personWithCoursesDao().insert(newPerson);
        scanner.close();

/*        try {
            br = new BufferedReader(new FileReader(csvReader));
            int count = 0;
            String name = null;
            String photoId = null;
            int personId = db.personWithCoursesDao().maxId() + 1;
            while ((line = br.readLine()) != null) {
                String[] array = line.split(csvSplitBy);
                if (array[1] == null && count == 0) {
                    name = array[0];
                    count++;
                } else if (array[1] == null && count == 1) {
                    photoId = array[0];
                    count++;
                    Person newPerson = new Person(personId, name, photoId);
                    Log.d("Here", "Name" + name);
                    db.personWithCoursesDao().insert(newPerson);
                } else {
                    String year = array[0];
                    String quarter = array[1];
                    String courseType = array[2];
                    String courseNum = array[3];
                    String text = quarter + year + ' ' + courseType + ' ' + courseNum;
                    int courseId = db.coursesDao().maxId() + 1;
                    Course c = new Course(courseId, personId, text);
                    db.coursesDao().insert(c);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
*/
    }
}
//FA2022 CSE 110