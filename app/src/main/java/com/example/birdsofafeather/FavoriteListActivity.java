package com.example.birdsofafeather;

import android.os.Bundle;
import android.view.View;

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
    protected RecyclerView personsRecyclerView;
    protected RecyclerView.LayoutManager personsLayoutManager;
    protected PersonsViewAdapter personsViewAdapter;

    private AppDatabase db;
    private List<PersonWithCourses> favoriteClassmates;
    private List<PersonWithCourses> classMates;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_favorite_list);
        setTitle("Favorite BoFs");

        db = AppDatabase.singleton(getApplicationContext());
        //first we get the person list in the database
        List<PersonWithCourses> persons = db.personWithCoursesDao().getAll();

        //then we create a sublist of classmate that have common course with user
        //user personId is 1, but List is start with 0
        classMates = persons.subList(1, persons.size());

        //Use a sorting to sort the number of common course
        //more common course, higher priority
        classMates.sort(new Comparator<PersonWithCourses>() {
            @Override
            public int compare(PersonWithCourses t1, PersonWithCourses t2) {
                return t2.getCourses().size() - t1.getCourses().size();
            }
        });

        favoriteClassmates = new ArrayList<>();

        for(int i = 0; i < classMates.size(); i++){
            if(classMates.get(i).getFavorite()){
                favoriteClassmates.add(classMates.get(i));
            }
        }

        personsRecyclerView = findViewById(R.id.persons_view);

        //send the info to the viewAdapter
        personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);

        personsViewAdapter = new PersonsViewAdapter(favoriteClassmates, db);
        personsRecyclerView.setAdapter(personsViewAdapter);
    }

    @Override
    protected void onResume(){
        super.onResume();
        setContentView(R.layout.activity_favorite_list);
        setTitle("Favorite BoFs");

        for(int i = 0; i < favoriteClassmates.size(); i++){
            favoriteClassmates.set(i, db.personWithCoursesDao().get(favoriteClassmates.get(i).getId()));
        }

        personsRecyclerView = findViewById(R.id.persons_view);

        // update recyclerview
        personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);

        personsViewAdapter = new PersonsViewAdapter(favoriteClassmates, db);
        personsRecyclerView.setAdapter(personsViewAdapter);
    }


    public void onNotFavoriteClicked(View view) {
        finish();
    }
}
