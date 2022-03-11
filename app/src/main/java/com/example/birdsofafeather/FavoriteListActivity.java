package com.example.birdsofafeather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.PersonWithCourses;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//set up the student taken common course list
public class FavoriteListActivity extends AppCompatActivity {
    protected RecyclerView personsRecyclerView;
    protected RecyclerView.LayoutManager personsLayoutManager;
    protected PersonsViewAdapter personsViewAdapter;

    private AppDatabase db;

    private List<PersonWithCourses> favClassmatesByNumCourses;
    private List<PersonWithCourses> favClassmatesByCourseSize;
    private List<PersonWithCourses> favClassmatesByRecentCourses;

    private String currSpinnerVal;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_prev_person_list);
        setTitle("Favorite BoFs");

        // Sort Selection Drop-down functionality
        Spinner sortSpinner = findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> sortArrAdapter = ArrayAdapter.createFromResource
                (this,R.array.sort, android.R.layout.simple_spinner_item);
        sortArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sortSpinner.setAdapter(sortArrAdapter);

        // get default spinner value
        currSpinnerVal = sortSpinner.getSelectedItem().toString();

        db = AppDatabase.singleton(getApplicationContext());
        //first we get the person list in the database
        List<PersonWithCourses> persons = db.personWithCoursesDao().getAll();

        //then we create a sublist of classmate that have common course with user
        //user personId is 1, but List is start with 0
        List<PersonWithCourses> classMates = persons.subList(1, persons.size());

        //get favorite classmates
        favClassmatesByNumCourses = new ArrayList<>();
        favClassmatesByCourseSize = new ArrayList<>();
        favClassmatesByRecentCourses = new ArrayList<>();
        for(int i = 0; i < classMates.size(); i++){
            if(classMates.get(i).getFavorite()){
                favClassmatesByNumCourses.add(classMates.get(i));
                favClassmatesByCourseSize.add(classMates.get(i));
                favClassmatesByRecentCourses.add(classMates.get(i));
            }
        }

        sortAllLists();

        // set recyclerview to initial favorite classmates sorting method
        personsRecyclerView = findViewById(R.id.persons_view);

        //send the info to the viewAdapter
        personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);

        personsViewAdapter = new PersonsViewAdapter(favClassmatesByNumCourses, db);
        personsRecyclerView.setAdapter(personsViewAdapter);
    }

    public void onApplySortClicked(View view) {
        Spinner newSortSpinnerView = findViewById(R.id.sort_spinner);
        currSpinnerVal = newSortSpinnerView.getSelectedItem().toString();
        personsRecyclerView = findViewById(R.id.persons_view);
        switch (currSpinnerVal){
            case "Most Shared Classes":
                updateView(favClassmatesByNumCourses);
                break;
            case "Most Recent Classes":
                updateView(favClassmatesByRecentCourses);
                break;
            case "Smallest Classes":
                updateView(favClassmatesByCourseSize);
                break;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        setContentView(R.layout.activity_prev_person_list);
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
                updateView(favClassmatesByNumCourses);
                break;
            case "Most Recent Classes":
                updateView(favClassmatesByRecentCourses);
                break;
            case "Smallest Classes":
                updateView(favClassmatesByCourseSize);
                break;
        }
    }

    public void updateView(List<PersonWithCourses> sortedList) {
        for(int i = 0; i < sortedList.size(); i++){
            sortedList.set(i, db.personWithCoursesDao().get(sortedList.get(i).getId()));
        }
        personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);

        personsViewAdapter = new PersonsViewAdapter(sortedList, db);
        personsRecyclerView.setAdapter(personsViewAdapter);
    }

    public void sortAllLists() {
        favClassmatesByNumCourses.sort(new Comparator<PersonWithCourses>() {
            @Override
            public int compare(PersonWithCourses t1, PersonWithCourses t2) {
                if(!t1.getWavingToUs() && !t2.getWavingToUs()) {
                    return t2.getCourses().size() - t1.getCourses().size();
                }
                else if (t1.getWavingToUs()){
                    return -1;
                }
                else {
                    return 1;
                }
            }
        });

        favClassmatesByCourseSize.sort(new Comparator<PersonWithCourses>() {
            @Override
            public int compare(PersonWithCourses t1, PersonWithCourses t2) {
                if(!t1.getWavingToUs() && !t2.getWavingToUs()) {
                    if(t2.person.sizePriority>t1.person.sizePriority){
                        return 1;
                    } else if(t2.person.sizePriority<t1.person.sizePriority){
                        return -1;
                    }
                    return 0;
                }
                else if (t1.getWavingToUs()){
                    return -1;
                }
                else {
                    return 1;
                }
            }
        });

        favClassmatesByRecentCourses.sort(new Comparator<PersonWithCourses>() {
            @Override
            public int compare(PersonWithCourses t1, PersonWithCourses t2) {
                if(!t1.getWavingToUs() && !t2.getWavingToUs()) {
                    return t2.person.recentPriority - t1.person.recentPriority;
                }
                else if (t1.getWavingToUs()){
                    return -1;
                }
                else {
                    return 1;
                }
            }
        });
    }

    public void onFinishClicked(View view) {
        finish();
    }
}
