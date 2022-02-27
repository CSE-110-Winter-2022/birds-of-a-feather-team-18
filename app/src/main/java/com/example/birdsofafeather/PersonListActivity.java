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
        List<PersonWithCourses> persons = db.personWithCoursesDao().getAll();

        //then we create a sublist of classmate that have common course with user
        //user personId is 1, but List is start with 0
        List<PersonWithCourses> classMates = persons.subList(1, persons.size());

        //Use a sorting to sort the number of common course
        //more common course, higher priority
        classMates.sort(new Comparator<PersonWithCourses>() {
            @Override
            public int compare(PersonWithCourses t1, PersonWithCourses t2) {
                return t2.getCourses().size() - t1.getCourses().size();
            }
        });

        personsRecyclerView = findViewById(R.id.persons_view);

        //send the info to the viewAdapter
        personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);

        personsViewAdapter = new PersonsViewAdapter(classMates);
        personsRecyclerView.setAdapter(personsViewAdapter);
    }

    public void onApplySortClicked(View view) {
        Spinner newSortSpinnerView = findViewById(R.id.sort_spinner);
        String newSortText = newSortSpinnerView.getSelectedItem().toString();

        //TODO add how changing sort changes the array inputted into the recycler view


    }
}
