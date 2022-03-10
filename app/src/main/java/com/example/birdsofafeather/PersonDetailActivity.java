package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.model.IPerson;
import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;
import com.squareup.picasso.Picasso;

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

        waveSend.setVisibility(View.INVISIBLE);
        waveHasSend.setVisibility(View.VISIBLE);
        db.personWithCoursesDao().updateWavingToThem(true, person.getId());
    }
}