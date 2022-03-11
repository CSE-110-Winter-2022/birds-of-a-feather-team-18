package com.example.birdsofafeather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.PersonWithCourses;
import com.example.birdsofafeather.model.db.Session;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

//set up the student taken common course list
public class SessionListActivity extends AppCompatActivity {
    protected RecyclerView sessionsRecyclerView;
    protected RecyclerView.LayoutManager sessionsLayoutManager;
    protected SessionsViewAdapter sessionsViewAdapter;
    //set up data base
    private AppDatabase db;
    public List<Session> sessions;


    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_session_list);
        setTitle(R.string.app_title);

        db = AppDatabase.singleton(getApplicationContext());

        // Get list of all sessions
        sessions = db.sessionsDao().getAll();

        // Put list of sessions into recycler view
        sessionsRecyclerView = findViewById(R.id.sessions_view);

        //send the info to the viewAdapter
        sessionsLayoutManager = new LinearLayoutManager(this);
        sessionsRecyclerView.setLayoutManager(sessionsLayoutManager);

        sessionsViewAdapter = new SessionsViewAdapter(sessions, db);
        sessionsRecyclerView.setAdapter(sessionsViewAdapter);

    }

    public void onFinishClicked(View view) {
        finish();
    }
}
