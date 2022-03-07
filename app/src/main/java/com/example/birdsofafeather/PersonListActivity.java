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
import com.example.birdsofafeather.model.db.Person;
import com.example.birdsofafeather.model.db.PersonWithCourses;
import com.example.birdsofafeather.model.db.Session;

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

    private List<PersonWithCourses> classMatesByNumCourses;
    private List<PersonWithCourses> classMatesByCourseSize;
    private List<PersonWithCourses> classMatesByRecentCourses;

    private String newSortText;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_person_list);
        setTitle(R.string.app_title);

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
        Session s = db.sessionsDao().get(preferences.getString("currSession",""));
        if(s!=null) {
            List<String> ids = s.peopleIDs;
            for (int i = 0; i < ids.size(); i++) {
                classMatesByNumCourses.add(db.personWithCoursesDao().get(ids.get(i)));
                classMatesByCourseSize.add(db.personWithCoursesDao().get(ids.get(i)));
                classMatesByRecentCourses.add(db.personWithCoursesDao().get(ids.get(i)));
            }
        }
        /*
        classMatesByNumCourses = db.personWithCoursesDao().getAll();
        classMatesByCourseSize = db.personWithCoursesDao().getAll();
        classMatesByRecentCourses = db.personWithCoursesDao().getAll();

        //then we create a sublist of classmate that have common course with user
        //user personId is 1, but List is start with 0
        classMatesByNumCourses = classMatesByNumCourses.subList(1, classMatesByNumCourses.size());
        classMatesByCourseSize = classMatesByCourseSize.subList(1, classMatesByCourseSize.size());
        classMatesByRecentCourses = classMatesByRecentCourses.subList(1, classMatesByRecentCourses.size());
        */
        //Use a sorting to sort the number of common course
        //more common course, higher priority
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

        personsRecyclerView = findViewById(R.id.persons_view);

        //send the info to the viewAdapter
        personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);

        personsViewAdapter = new PersonsViewAdapter(classMatesByNumCourses, db);
        personsRecyclerView.setAdapter(personsViewAdapter);
    }

    public void onApplySortClicked(View view) {
        Spinner newSortSpinnerView = findViewById(R.id.sort_spinner);
        newSortText = newSortSpinnerView.getSelectedItem().toString();
        switch (newSortText){
            case "Most Shared Classes":
                personsRecyclerView = findViewById(R.id.persons_view);

                //send the info to the viewAdapter
                personsLayoutManager = new LinearLayoutManager(this);
                personsRecyclerView.setLayoutManager(personsLayoutManager);

                personsViewAdapter = new PersonsViewAdapter(classMatesByNumCourses, db);
                personsRecyclerView.setAdapter(personsViewAdapter);
                break;
            case "Most Recent Classes":
                personsRecyclerView = findViewById(R.id.persons_view);

                //send the info to the viewAdapter
                personsLayoutManager = new LinearLayoutManager(this);
                personsRecyclerView.setLayoutManager(personsLayoutManager);

                personsViewAdapter = new PersonsViewAdapter(classMatesByRecentCourses, db);
                personsRecyclerView.setAdapter(personsViewAdapter);
                break;
            case "Smallest Classes":
                personsRecyclerView = findViewById(R.id.persons_view);

                //send the info to the viewAdapter
                personsLayoutManager = new LinearLayoutManager(this);
                personsRecyclerView.setLayoutManager(personsLayoutManager);

                personsViewAdapter = new PersonsViewAdapter(classMatesByCourseSize, db);
                personsRecyclerView.setAdapter(personsViewAdapter);
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

        switch (newSortText){
            case "Most Shared Classes":
                // refresh recyclerview with updated database
                for(int i = 0; i < classMatesByNumCourses.size(); i++){
                    classMatesByNumCourses.set(i, db.personWithCoursesDao().get(classMatesByNumCourses.get(i).getId()));
                }
                //send the info to the viewAdapter
                personsLayoutManager = new LinearLayoutManager(this);
                personsRecyclerView.setLayoutManager(personsLayoutManager);

                personsViewAdapter = new PersonsViewAdapter(classMatesByNumCourses, db);
                personsRecyclerView.setAdapter(personsViewAdapter);
                break;
            case "Most Recent Classes":
                // refresh recyclerview with updated database
                for(int i = 0; i < classMatesByRecentCourses.size(); i++){
                    classMatesByRecentCourses.set(i, db.personWithCoursesDao().get(classMatesByRecentCourses.get(i).getId()));
                }
                //send the info to the viewAdapter
                personsLayoutManager = new LinearLayoutManager(this);
                personsRecyclerView.setLayoutManager(personsLayoutManager);

                personsViewAdapter = new PersonsViewAdapter(classMatesByRecentCourses, db);
                personsRecyclerView.setAdapter(personsViewAdapter);
                break;
            case "Smallest Classes":
                // refresh recyclerview with updated database
                for(int i = 0; i < classMatesByCourseSize.size(); i++){
                    classMatesByCourseSize.set(i, db.personWithCoursesDao().get(classMatesByCourseSize.get(i).getId()));
                }
                //send the info to the viewAdapter
                personsLayoutManager = new LinearLayoutManager(this);
                personsRecyclerView.setLayoutManager(personsLayoutManager);

                personsViewAdapter = new PersonsViewAdapter(classMatesByCourseSize, db);
                personsRecyclerView.setAdapter(personsViewAdapter);
                break;
        }
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
                });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

        /*
        Intent intent = new Intent(PersonListActivity.this, SearchService.class);
        startService(intent);
        Log.d(TAG, "Start Clicked, service start");
        */
    }

    //bind the button to stop service
    public void onStopClicked(View view) {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("currSession", "none");
        editor.apply();

        Intent intent = new Intent(PersonListActivity.this, SearchService.class);
        stopService(intent);
        Log.d(TAG, "Stop Clicked, service stop");
    }

    // Go to Favorite List and send current spinner value
    public void onFavoriteClicked(View view) {
        Intent intent = new Intent(this, FavoriteListActivity.class);
        intent.putExtra("currSortText", newSortText);
        startActivity(intent);
    }
}
