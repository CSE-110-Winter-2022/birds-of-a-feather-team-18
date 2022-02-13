package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.model.DummyPerson;
import com.example.birdsofafeather.model.IPerson;
import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;
import com.example.birdsofafeather.model.db.Person;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Bofs-Nearby";
    private MessageListener messageListener;


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

        MessageListener realListener = new MessageListener() {};

        TextView profile = findViewById(R.id.student_profile);
        String test = profile.getText().toString();

        this.messageListener = new FakedMessageListener(realListener, test, db);

    }

    public void onTestClicked(View view) {
        /*Intent intent = new Intent(this, PrevCourseActivity.class);
        startActivity(intent);*/
        Intent intent = new Intent(this, PersonListActivity.class);
        startActivity(intent);
    }

    public void onEnterClicked(View view) {
        TextView profile = findViewById(R.id.student_profile);
        String test = profile.getText().toString();

        MessageListener realListener = new MessageListener() {
            @Override
            public void onFound(@NonNull Message message) {
                Log.d(TAG, "Found Profile: " + new String(message.getContent()));
            }

            @Override
            public void onLost(@NonNull Message message) {
                Log.d(TAG, "Profile loaded in DataBase " + new String(message.getContent()));
            }
        };
        AppDatabase db = AppDatabase.singleton(this);
        //Use messageListener to save profile in database
        this.messageListener = new FakedMessageListener(realListener, test, db);

        profile.setText("");
    }
    @Override
    protected void onStart() {
        super.onStart();
        Nearby.getMessagesClient(this).subscribe(messageListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Nearby.getMessagesClient(this).unsubscribe(messageListener);
    }
}
