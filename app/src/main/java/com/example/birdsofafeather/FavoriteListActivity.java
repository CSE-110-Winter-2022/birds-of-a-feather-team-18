package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Person;
import com.example.birdsofafeather.model.db.PersonWithCourses;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//set up the student taken common course list
public class FavoriteListActivity extends AppCompatActivity {
    private static final String TAG = "Bofs-Nearby";
    protected RecyclerView personsRecyclerView;
    protected RecyclerView.LayoutManager personsLayoutManager;
    protected PersonsViewAdapter personsViewAdapter;

    private AppDatabase db;

    private List<PersonWithCourses> favoriteClassMatesByNumCourses;
    private List<PersonWithCourses> favoriteClassMatesByCourseSize;
    private List<PersonWithCourses> favoriteClassMatesByRecentCourses;

    private String currSpinnerVal;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_favorite_list);
        setTitle("Favorite BoFs");

        //get currSpinnerVal from intent extra
        Intent intent = getIntent();
        currSpinnerVal = intent.getStringExtra("currSortText");

        // Sort Selection Drop-down functionality
        Spinner sortSpinner = findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> sortArrAdapter = ArrayAdapter.createFromResource
                (this,R.array.sort, android.R.layout.simple_spinner_item);
        sortArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sortSpinner.setAdapter(sortArrAdapter);
        // set spinner selection to current spinner value
        sortSpinner.setSelection(sortArrAdapter.getPosition(currSpinnerVal));

        db = AppDatabase.singleton(getApplicationContext());
        //first we get the person list in the database
        List<PersonWithCourses> persons = db.personWithCoursesDao().getAll();

        //then we create a sublist of classmate that have common course with user
        //user personId is 1, but List is start with 0
        List<PersonWithCourses> classMates = persons.subList(1, persons.size());

        //get favorite classmates
        favoriteClassMatesByNumCourses = new ArrayList<>();
        favoriteClassMatesByCourseSize = new ArrayList<>();
        favoriteClassMatesByRecentCourses = new ArrayList<>();
        for(int i = 0; i < classMates.size(); i++){
            if(classMates.get(i).getFavorite()){
                favoriteClassMatesByNumCourses.add(classMates.get(i));
                favoriteClassMatesByCourseSize.add(classMates.get(i));
                favoriteClassMatesByRecentCourses.add(classMates.get(i));
            }
        }

        //Use a sorting to sort the number of common course
        //more common course, higher priority
        favoriteClassMatesByNumCourses.sort(new Comparator<PersonWithCourses>() {
            @Override
            public int compare(PersonWithCourses t1, PersonWithCourses t2) {
                return t2.getCourses().size() - t1.getCourses().size();
            }
        });

        favoriteClassMatesByCourseSize.sort(new Comparator<PersonWithCourses>() {
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

        favoriteClassMatesByRecentCourses.sort(new Comparator<PersonWithCourses>() {
            @Override
            public int compare(PersonWithCourses t1, PersonWithCourses t2) {
                return t2.person.recentPriority - t1.person.recentPriority;
            }
        });

        //get init favorite list
        List<PersonWithCourses> initFavoriteList = new ArrayList<>();
        switch (currSpinnerVal){
            case "Most Shared Classes":
                initFavoriteList = favoriteClassMatesByNumCourses;
                break;
            case "Most Recent Classes":
                initFavoriteList = favoriteClassMatesByRecentCourses;
                break;
            case "Smallest Classes":
                initFavoriteList = favoriteClassMatesByCourseSize;
                break;
        }

        // set recyclerview to initial favorite classmates sorting method
        personsRecyclerView = findViewById(R.id.persons_view);

        //send the info to the viewAdapter
        personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);

        personsViewAdapter = new PersonsViewAdapter(initFavoriteList, db);
        personsRecyclerView.setAdapter(personsViewAdapter);
    }

    public void onApplySortClicked(View view) {
        Spinner newSortSpinnerView = findViewById(R.id.sort_spinner);
        currSpinnerVal = newSortSpinnerView.getSelectedItem().toString();
        switch (currSpinnerVal){
            case "Most Shared Classes":
                personsRecyclerView = findViewById(R.id.persons_view);

                //send the info to the viewAdapter
                personsLayoutManager = new LinearLayoutManager(this);
                personsRecyclerView.setLayoutManager(personsLayoutManager);

                personsViewAdapter = new PersonsViewAdapter(favoriteClassMatesByNumCourses, db);
                personsRecyclerView.setAdapter(personsViewAdapter);
                break;
            case "Most Recent Classes":
                personsRecyclerView = findViewById(R.id.persons_view);

                //send the info to the viewAdapter
                personsLayoutManager = new LinearLayoutManager(this);
                personsRecyclerView.setLayoutManager(personsLayoutManager);

                personsViewAdapter = new PersonsViewAdapter(favoriteClassMatesByRecentCourses, db);
                personsRecyclerView.setAdapter(personsViewAdapter);
                break;
            case "Smallest Classes":
                personsRecyclerView = findViewById(R.id.persons_view);

                //send the info to the viewAdapter
                personsLayoutManager = new LinearLayoutManager(this);
                personsRecyclerView.setLayoutManager(personsLayoutManager);

                personsViewAdapter = new PersonsViewAdapter(favoriteClassMatesByCourseSize, db);
                personsRecyclerView.setAdapter(personsViewAdapter);
                break;
        }
    }


    @Override
    protected void onResume(){
        super.onResume();
        setContentView(R.layout.activity_favorite_list);
        setTitle("Favorite BoFs");

        Spinner sortSpinner = findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> sortArrAdapter = ArrayAdapter.createFromResource
                (this,R.array.sort, android.R.layout.simple_spinner_item);
        sortArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sortSpinner.setAdapter(sortArrAdapter);
        sortSpinner.setSelection(sortArrAdapter.getPosition(currSpinnerVal));

        personsRecyclerView = findViewById(R.id.persons_view);

        switch (currSpinnerVal){
            case "Most Shared Classes":
                // refresh recyclerview with updated database
                for(int i = 0; i < favoriteClassMatesByNumCourses.size(); i++){
                    favoriteClassMatesByNumCourses.set(i, db.personWithCoursesDao().get(favoriteClassMatesByNumCourses.get(i).getId()));
                }
                //send the info to the viewAdapter
                personsLayoutManager = new LinearLayoutManager(this);
                personsRecyclerView.setLayoutManager(personsLayoutManager);

                personsViewAdapter = new PersonsViewAdapter(favoriteClassMatesByNumCourses, db);
                personsRecyclerView.setAdapter(personsViewAdapter);
                break;
            case "Most Recent Classes":
                // refresh recyclerview with updated database
                for(int i = 0; i < favoriteClassMatesByRecentCourses.size(); i++){
                    favoriteClassMatesByRecentCourses.set(i, db.personWithCoursesDao().get(favoriteClassMatesByRecentCourses.get(i).getId()));
                }
                //send the info to the viewAdapter
                personsLayoutManager = new LinearLayoutManager(this);
                personsRecyclerView.setLayoutManager(personsLayoutManager);

                personsViewAdapter = new PersonsViewAdapter(favoriteClassMatesByRecentCourses, db);
                personsRecyclerView.setAdapter(personsViewAdapter);
                break;
            case "Smallest Classes":
                // refresh recyclerview with updated database
                for(int i = 0; i < favoriteClassMatesByCourseSize.size(); i++){
                    favoriteClassMatesByCourseSize.set(i, db.personWithCoursesDao().get(favoriteClassMatesByCourseSize.get(i).getId()));
                }
                //send the info to the viewAdapter
                personsLayoutManager = new LinearLayoutManager(this);
                personsRecyclerView.setLayoutManager(personsLayoutManager);

                personsViewAdapter = new PersonsViewAdapter(favoriteClassMatesByCourseSize, db);
                personsRecyclerView.setAdapter(personsViewAdapter);
                break;
        }
    }

    public void onStartClicked(View view) {
        Intent intent = new Intent(FavoriteListActivity.this, SearchService.class);
        startService(intent);
        Log.d(TAG, "Start Clicked, service start");
    }

    //bind the button to stop service
    public void onStopClicked(View view) {
        Intent intent = new Intent(FavoriteListActivity.this, SearchService.class);
        stopService(intent);
        Log.d(TAG, "Stop Clicked, service stop");
    }


    public void onNotFavoriteClicked(View view) {
        finish();
    }
}
