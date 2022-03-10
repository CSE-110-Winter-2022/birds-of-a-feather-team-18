package com.example.birdsofafeather;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;
import com.example.birdsofafeather.model.db.Person;
import com.example.birdsofafeather.model.db.PersonWithCourses;
import com.example.birdsofafeather.model.db.Session;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

//set up the student taken common course list
public class PersonListActivity extends AppCompatActivity {
    private static final String TAG = "Bofs-Nearby";
    protected RecyclerView personsRecyclerView;
    protected RecyclerView.LayoutManager personsLayoutManager;
    protected PersonsViewAdapter personsViewAdapter;
    //set up data base
    private AppDatabase db;

    public List<PersonWithCourses> classMatesByNumCourses;
    private List<PersonWithCourses> classMatesByCourseSize;
    private List<PersonWithCourses> classMatesByRecentCourses;
    private Session currSession;

    private String newSortText;

    private Message mActiveMessage;
    private MessageListener messageListener;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_person_list);
        setTitle(R.string.app_title);

        //Use a sorting to sort the number of common course
        //more common course, higher priority
        updateAllLists();

        personsRecyclerView = findViewById(R.id.persons_view);

        //send the info to the viewAdapter
        updateView(classMatesByNumCourses);

        messageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.d(TAG, "Found message: " + new String(message.getContent()));
            }

            @Override
            public void onLost(Message message) {
                Log.d(TAG, "Lost sight of message: " + new String(message.getContent()));
            }
        };

        mActiveMessage = new Message("Hello World".getBytes());
    }

    public void onApplySortClicked(View view) {
        Spinner newSortSpinnerView = findViewById(R.id.sort_spinner);
        newSortText = newSortSpinnerView.getSelectedItem().toString();
        switch (newSortText){
            case "Most Shared Classes":
                personsRecyclerView = findViewById(R.id.persons_view);

                //send the info to the viewAdapter
                updateView(classMatesByNumCourses);
                break;
            case "Most Recent Classes":
                personsRecyclerView = findViewById(R.id.persons_view);

                //send the info to the viewAdapter
                updateView(classMatesByRecentCourses);
                break;
            case "Smallest Classes":
                personsRecyclerView = findViewById(R.id.persons_view);

                //send the info to the viewAdapter
                updateView(classMatesByCourseSize);
                break;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        setContentView(R.layout.activity_person_list);
        setTitle(R.string.app_title);

        // Sort Selection Drop-down functionality
        Spinner sortSpinner = findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> sortArrAdapter = ArrayAdapter.createFromResource
                (this,R.array.sort, android.R.layout.simple_spinner_item);
        sortArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sortSpinner.setAdapter(sortArrAdapter);
        sortSpinner.setSelection(sortArrAdapter.getPosition(newSortText));

        personsRecyclerView = findViewById(R.id.persons_view);

        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        Session s = db.sessionsDao().get(preferences.getString("currSession",""));

        switch (newSortText){
            case "Most Shared Classes":
                // refresh recyclerview with updated database
                updateListAndView(classMatesByNumCourses);
                break;
            case "Most Recent Classes":
                // refresh recyclerview with updated database
                updateListAndView(classMatesByRecentCourses);
                break;
            case "Smallest Classes":
                // refresh recyclerview with updated database
                updateListAndView(classMatesByCourseSize);
                break;
        }
    }

    public void updateListAndView(List<PersonWithCourses> sortedList){
        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        currSession = db.sessionsDao().get(preferences.getString("currSession",""));
        sortedList.clear();
        if(currSession!=null) {
            List<String> ids = currSession.peopleIDs;
            for (int i = 0; i < ids.size(); i++) {
                sortedList.add(db.personWithCoursesDao().get(ids.get(i)));
            }
        }

        //send the info to the viewAdapter
        updateView(sortedList);
    }

    public void updateView(List<PersonWithCourses> sortedList) {
        personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);

        personsViewAdapter = new PersonsViewAdapter(sortedList, db);
        personsRecyclerView.setAdapter(personsViewAdapter);
    }

    public void updateAllLists() {
        // Sort Selection Drop-down functionality
        Spinner sortSpinner = findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> sortArrAdapter = ArrayAdapter.createFromResource
                (this,R.array.sort, android.R.layout.simple_spinner_item);
        sortArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sortSpinner.setAdapter(sortArrAdapter);

        // get default spinner value
        newSortText = sortSpinner.getSelectedItem().toString();

        db = AppDatabase.singleton(getApplicationContext());
        //first we get the person list in the database

        classMatesByNumCourses = new ArrayList<PersonWithCourses>();
        classMatesByCourseSize = new ArrayList<PersonWithCourses>();
        classMatesByRecentCourses = new ArrayList<PersonWithCourses>();
        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        currSession = db.sessionsDao().get(preferences.getString("currSession",""));
        if(currSession!=null) {
            List<String> ids = currSession.peopleIDs;
            for (int i = 0; i < ids.size(); i++) {
                classMatesByNumCourses.add(db.personWithCoursesDao().get(ids.get(i)));
                classMatesByCourseSize.add(db.personWithCoursesDao().get(ids.get(i)));
                classMatesByRecentCourses.add(db.personWithCoursesDao().get(ids.get(i)));
            }
        }

        classMatesByNumCourses.sort(new Comparator<PersonWithCourses>() {
            @Override
            public int compare(PersonWithCourses t1, PersonWithCourses t2) {
                return t2.getCourses().size() - t1.getCourses().size();
            }
        });

        classMatesByCourseSize.sort(new Comparator<PersonWithCourses>() {
            @Override
            public int compare(PersonWithCourses t1, PersonWithCourses t2) {
                if(t2.person.sizePriority>t1.person.sizePriority){
                    return 1;
                } else if(t2.person.sizePriority<t1.person.sizePriority){
                    return -1;
                }
                return 0;
            }
        });

        classMatesByRecentCourses.sort(new Comparator<PersonWithCourses>() {
            @Override
            public int compare(PersonWithCourses t1, PersonWithCourses t2) {
                return t2.person.recentPriority - t1.person.recentPriority;
            }
        });
    }

    public void onStartClicked(View view) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        alertBuilder.setTitle("Starting Session")
                .setMessage("Resume a previous session or start a new one?")
                .setCancelable(true)
                .setPositiveButton("New", (dialog,id) -> {
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mmaa");
                    String dateString = dateFormat.format(new Date()).toString();
                    Session newSession = new Session(UUID.randomUUID().toString(), dateString);
                    SharedPreferences preferences = getSharedPreferences("session",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("currSession", newSession.sessionId);
                    editor.apply();
                    db.sessionsDao().insert(newSession);

                    Intent intent = new Intent(PersonListActivity.this, SearchService.class);
                    startService(intent);
                    Log.d(TAG, "Start Clicked, service start");
                    dialog.cancel();

                })
                .setNegativeButton("Previous", (dialog,id) -> {
                    //implement choosing previous session
                    Intent intent = new Intent(PersonListActivity.this, SessionListActivity.class);
                    startActivity(intent);
                });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

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


        publish("Here is the profile should be publish");
        subscribe();
        /*
        Intent intent = new Intent(PersonListActivity.this, SearchService.class);
        startService(intent);
        Log.d(TAG, "Start Clicked, service start");
        */
    }

    //bind the button to stop service
    public void onStopClicked(View view) {
        if(isMyServiceRunning(SearchService.class)) {
            showNameSessionDialog(this);
        }
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("currSession", "none");
        editor.apply();

        Intent intent = new Intent(PersonListActivity.this, SearchService.class);
        stopService(intent);
        Log.d(TAG, "Stop Clicked, service stop");
    }

    public void showNameSessionDialog(Context c) {
        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        final EditText taskEditText = new EditText(c);
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(c)
                .setTitle("Name Session")
                .setView(taskEditText)
                .setPositiveButton("Name", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = String.valueOf(taskEditText.getText());
                        Session s = db.sessionsDao().get(preferences.getString("currSession",""));
                        db.sessionsDao().updateSessionName(name, s.sessionId);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();

        //unpublish and unsubscribe the message
        unpublish();
        unsubscribe();
    }

    //check if search service is on or off
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            //if service is on, return true
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void publish(String message) {
        Log.i(TAG, "Publishing message: " + message);
        mActiveMessage = new Message(message.getBytes());
        Nearby.getMessagesClient(this).publish(mActiveMessage);
    }

    private void unpublish() {
        Log.i(TAG, "Unpublishing.");
        if (mActiveMessage != null) {
            Nearby.getMessagesClient(this).unpublish(mActiveMessage);
            mActiveMessage = null;
        }
    }

    private void subscribe() {
        Log.i(TAG, "Subscribing.");
        Nearby.getMessagesClient(this).subscribe(messageListener);
    }

    private void unsubscribe() {
        Log.i(TAG, "Unsubscribing.");
        Nearby.getMessagesClient(this).unsubscribe(messageListener);
    }
}
