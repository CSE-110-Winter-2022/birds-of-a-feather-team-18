package com.example.birdsofafeather;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Person;
import com.example.birdsofafeather.model.db.PersonWithCourses;

import java.util.Comparator;
import java.util.List;

//set up the student taken common course list
public class PersonListActivity extends AppCompatActivity {
    protected RecyclerView personsRecyclerView;
    protected RecyclerView.LayoutManager personsLayoutManager;
    protected PersonsViewAdapter personsViewAdapter;
    //set up data base
    private AppDatabase db;
    private List<PersonWithCourses> classMatesByNumCourses;
    private List<PersonWithCourses> classMatesByCourseSize;
    private List<PersonWithCourses> classMatesByRecentCourses;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_person_list);
        setTitle(R.string.app_title);

        // Sort Selection Drop-down functionality
        Spinner spinnerSize = findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> sortArrAdapter = ArrayAdapter.createFromResource
                (this,R.array.sort, android.R.layout.simple_spinner_item);
        sortArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerSize.setAdapter(sortArrAdapter);

        db = AppDatabase.singleton(getApplicationContext());
        //first we get the person list in the database
        classMatesByNumCourses = db.personWithCoursesDao().getAll();
        classMatesByCourseSize = db.personWithCoursesDao().getAll();
        classMatesByRecentCourses = db.personWithCoursesDao().getAll();

        //then we create a sublist of classmate that have common course with user
        //user personId is 1, but List is start with 0
        classMatesByNumCourses = classMatesByNumCourses.subList(1, classMatesByNumCourses.size());
        classMatesByCourseSize = classMatesByCourseSize.subList(1, classMatesByCourseSize.size());
        classMatesByRecentCourses = classMatesByRecentCourses.subList(1, classMatesByRecentCourses.size());

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

        personsViewAdapter = new PersonsViewAdapter(classMatesByNumCourses);
        personsRecyclerView.setAdapter(personsViewAdapter);
    }

    public void onApplySortClicked(View view) {
        Spinner newSortSpinnerView = findViewById(R.id.sort_spinner);
        String newSortText = newSortSpinnerView.getSelectedItem().toString();
        switch (newSortText){
            case "Most Shared Classes":
                personsRecyclerView = findViewById(R.id.persons_view);

                //send the info to the viewAdapter
                personsLayoutManager = new LinearLayoutManager(this);
                personsRecyclerView.setLayoutManager(personsLayoutManager);

                personsViewAdapter = new PersonsViewAdapter(classMatesByNumCourses);
                personsRecyclerView.setAdapter(personsViewAdapter);
                break;
            case "Most Recent Classes":
                personsRecyclerView = findViewById(R.id.persons_view);

                //send the info to the viewAdapter
                personsLayoutManager = new LinearLayoutManager(this);
                personsRecyclerView.setLayoutManager(personsLayoutManager);

                personsViewAdapter = new PersonsViewAdapter(classMatesByRecentCourses);
                personsRecyclerView.setAdapter(personsViewAdapter);
                break;
            case "Smallest Classes":
                personsRecyclerView = findViewById(R.id.persons_view);

                //send the info to the viewAdapter
                personsLayoutManager = new LinearLayoutManager(this);
                personsRecyclerView.setLayoutManager(personsLayoutManager);

                personsViewAdapter = new PersonsViewAdapter(classMatesByCourseSize);
                personsRecyclerView.setAdapter(personsViewAdapter);
                break;
        }
    }
}
