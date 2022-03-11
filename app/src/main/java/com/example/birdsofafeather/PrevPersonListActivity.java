package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.PersonWithCourses;
import com.example.birdsofafeather.model.db.Session;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

//set up the student taken common course list
public class PrevPersonListActivity extends AppCompatActivity {
    protected RecyclerView personsRecyclerView;
    protected RecyclerView.LayoutManager personsLayoutManager;
    protected PersonsViewAdapter personsViewAdapter;
    //set up data base
    private AppDatabase db;

    public List<PersonWithCourses> classMatesByNumCourses;
    private List<PersonWithCourses> classMatesByCourseSize;
    private List<PersonWithCourses> classMatesByRecentCourses;

    private String currSortText;
    private String sessionName;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_prev_person_list);

        // Initialize database
        db = AppDatabase.singleton(getApplicationContext());

        // Get session from extra
        Intent intent = getIntent();
        String sessionId = intent.getStringExtra("session_id");
        Session currSession = db.sessionsDao().get(sessionId);

        // Set title to session name
        sessionName = db.sessionsDao().get(sessionId).sessionName;
        setTitle(sessionName);

        // Sort Selection Drop-down functionality
        Spinner sortSpinner = findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> sortArrAdapter = ArrayAdapter.createFromResource
                (this,R.array.sort, android.R.layout.simple_spinner_item);
        sortArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sortSpinner.setAdapter(sortArrAdapter);
        // Get default spinner value
        currSortText = sortSpinner.getSelectedItem().toString();

        // Get classmates from this session
        List<String> classMatesIds = currSession.peopleIDs;
        classMatesByNumCourses = new ArrayList<>();
        classMatesByCourseSize = new ArrayList<>();
        classMatesByRecentCourses = new ArrayList<>();
        for(int i = 0; i < classMatesIds.size(); i++){
            classMatesByNumCourses.add(db.personWithCoursesDao().get(classMatesIds.get(i)));
            classMatesByCourseSize.add(db.personWithCoursesDao().get(classMatesIds.get(i)));
            classMatesByRecentCourses.add(db.personWithCoursesDao().get(classMatesIds.get(i)));
        }

        //Use a sorting to sort the number of common course
        //more common course, higher priority
        sortAllLists();

        // Setup recycler view and send to view adapter
        personsRecyclerView = findViewById(R.id.persons_view);
        updateView(classMatesByNumCourses);
    }

    public void onApplySortClicked(View view) {
        // Update Spinner value
        Spinner newSortSpinnerView = findViewById(R.id.sort_spinner);
        currSortText = newSortSpinnerView.getSelectedItem().toString();

        personsRecyclerView = findViewById(R.id.persons_view);
        switch (currSortText){
            case "Most Shared Classes":
                // Refresh recycler view
                updateView(classMatesByNumCourses);
                break;
            case "Most Recent Classes":
                // Refresh recycler view
                updateView(classMatesByRecentCourses);
                break;
            case "Smallest Classes":
                // Refresh recycler view
                updateView(classMatesByCourseSize);
                break;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        setContentView(R.layout.activity_prev_person_list);
        setTitle(sessionName);

        // Sort Selection Drop-down functionality
        Spinner sortSpinner = findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> sortArrAdapter = ArrayAdapter.createFromResource
                (this,R.array.sort, android.R.layout.simple_spinner_item);
        sortArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sortSpinner.setAdapter(sortArrAdapter);
        // Set curr Spinner value to what it was before leaving activity
        sortSpinner.setSelection(sortArrAdapter.getPosition(currSortText));

        personsRecyclerView = findViewById(R.id.persons_view);

        switch (currSortText){
            case "Most Shared Classes":
                // refresh recyclerview with updated database
                updateView(classMatesByNumCourses);
                break;
            case "Most Recent Classes":
                // refresh recyclerview with updated database
                updateView(classMatesByRecentCourses);
                break;
            case "Smallest Classes":
                // refresh recyclerview with updated database
                updateView(classMatesByCourseSize);
                break;
        }
    }

    public void updateView(List<PersonWithCourses> sortedList) {
        for(int i = 0; i < sortedList.size(); i++){
            sortedList.set(i, db.personWithCoursesDao().get(sortedList.get(i).getId()));
        }

        // Send info to view adapter and setup recycler view
        personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);
        personsViewAdapter = new PersonsViewAdapter(sortedList, db);
        personsRecyclerView.setAdapter(personsViewAdapter);
    }

    public void sortAllLists() {

        // Sort by number of common courses
        classMatesByNumCourses.sort(new Comparator<PersonWithCourses>() {
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

        // Sort by course size
        classMatesByCourseSize.sort(new Comparator<PersonWithCourses>() {
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

        // Sort by recent courses
        classMatesByRecentCourses.sort(new Comparator<PersonWithCourses>() {
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
