package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.model.IPerson;
import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;
import com.example.birdsofafeather.model.db.PersonWithCourses;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.UUID;


public class PersonDetailActivity extends AppCompatActivity {
    private AppDatabase db;
    private IPerson person;
    private static final String TAG = "Bofs-Nearby";

    private RecyclerView coursesRecyclerView;
    private RecyclerView.LayoutManager coursesLayoutManager;
    private CoursesViewAdapter coursesViewAdapter;

    private Message mActiveMessage;
    private MessageListener messageListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_detail);

        Intent intent = getIntent();
        String personId = intent.getStringExtra("person_id");

        db = AppDatabase.singleton(this);
        person = db.personWithCoursesDao().get(personId);
        List<Course> courses = db.coursesDao().getForPerson(personId);

        //Set up photo
        String photo = person.getPhoto();
        ImageView imageView = findViewById(R.id.image_view);
        Picasso.get().load(photo).into(imageView);

        setTitle(person.getName());

        //Check if person is favorite
        CheckBox favStar = findViewById(R.id.detail_star);
        boolean favorite = person.getFavorite();
        favStar.setChecked(favorite);

        //Check if send wave to the person
        ImageButton waveSend = findViewById(R.id.detail_wave);
        ImageButton waveHasSend = findViewById(R.id.detail_wave_send);
        boolean waving = person.getWavingToThem();
        if (waving) {
            waveHasSend.setVisibility(View.VISIBLE);
            waveSend.setEnabled(false);
            waveSend.setVisibility(View.INVISIBLE);
        }
        waveSend.setActivated(waving);
        waveHasSend.setActivated(waving);

        // Set up recyclerview
        coursesRecyclerView = findViewById(R.id.courses_view);
        coursesLayoutManager = new LinearLayoutManager(this);
        coursesRecyclerView.setLayoutManager(coursesLayoutManager);

        coursesViewAdapter = new CoursesViewAdapter(courses);
        coursesRecyclerView.setAdapter(coursesViewAdapter);
    }


    public void onGoBackClicked(View view) {
        finish();
    }

    public void onDetailStarClicked(View view) {
        boolean isFavorite = ((CheckBox)view).isChecked();

        db.personWithCoursesDao().updateFavorite(isFavorite, person.getId());
    }

    //once the button is clicked, we will send a waving message
    public void onDetailWaveClicked(View view) {

        ImageButton waveHasSend = findViewById(R.id.detail_wave_send);
        ImageButton waveSend = findViewById(R.id.detail_wave);
        Toast.makeText(this,"Wave has send", Toast.LENGTH_SHORT).show();
        waveSend.setVisibility(View.INVISIBLE);
        waveHasSend.setVisibility(View.VISIBLE);
        //set the wave to true first
        db.personWithCoursesDao().updateWavingToThem(true, person.getId());
        //publish profile
        publish(createCSV());
    }

    private void publish(String message) {
        Log.i(TAG, "Publishing message: " + message);

        mActiveMessage = new Message(message.getBytes());
        Nearby.getMessagesClient(this).publish(mActiveMessage);
    }

    public String createCSV() {
        String selfName = db.personWithCoursesDao().get("1").getName();
        String selfPhotoURL = db.personWithCoursesDao().get("1").getPhoto();
        String selfString = selfName + selfPhotoURL;
        String selfId = UUID.nameUUIDFromBytes(selfString.getBytes()).toString();
        List<Course> selfCourses = db.coursesDao().getForPerson("1");
        String allCourses = "";
        for(int i = 0; i < selfCourses.size(); i++) {
            Course currCourse = selfCourses.get(i);
            String[] textInfo = currCourse.text.split(" ");
            allCourses += currCourse.year + "," + currCourse.quarter + "," + textInfo[1] + "," + textInfo[2] + "," + currCourse.size;
            if(i != selfCourses.size() - 1) {
                allCourses += "\n";
            }
        }
        List<PersonWithCourses> peopleWavingTo = db.personWithCoursesDao().getAllWavingToThem();
        String allWaves = "";
        for(int i = 0; i < peopleWavingTo.size(); i++) {
            PersonWithCourses currPerson = peopleWavingTo.get(i);
            allWaves += currPerson.getId() + ",wave" + ",,,";
            if(i != peopleWavingTo.size() - 1) {
                allWaves += "\n";
            }
        }
        String selfCSV = "";

        if(peopleWavingTo.isEmpty()) {
            selfCSV = selfId + ",,,,\n" + selfName + ",,,,\n" + selfPhotoURL + ",,,,\n" + allCourses;
        }
        else {
            selfCSV = selfId + ",,,,\n" + selfName + ",,,,\n" + selfPhotoURL + ",,,,\n" + allCourses + "\n" + allWaves;
        }

        return selfCSV;
    }
}