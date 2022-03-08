package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.model.IPerson;
import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;
import com.example.birdsofafeather.model.db.Person;
import com.example.birdsofafeather.model.db.PersonWithCourses;
import com.example.birdsofafeather.model.db.Session;
import com.squareup.picasso.Picasso;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PersonListTest {

    AppDatabase testDB;
    final String DEFAULT_PHOTO = "https://st3.depositphotos.com/4111759/13425/v/600/depositphotos_134255532-stock-illustration-profile-placeholder-male-default-profile.jpg";

    @Before
    public void setupDb() {
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase.useTestDatabase(context);
        testDB = AppDatabase.singleton(context);

        Person person1 = new Person("1", "User", DEFAULT_PHOTO, false);
        Person person2 = new Person("2", "Small Simon", DEFAULT_PHOTO, false);
        Person person3 = new Person("3", "Freshman Fabio", DEFAULT_PHOTO, false);
        Person person4 = new Person("4", "Tryhard Tyler", DEFAULT_PHOTO, false);

        //user's courses
        Course course1 = new Course(1, "1", "LTEA 138", "22", "WI", "Huge");
        Course course2 = new Course(2, "1", "CSE 110", "21", "WI", "Gigantic");
        Course course3 = new Course(3, "1", "CSE 101", "21", "FA", "Medium");
        Course course4 = new Course(4, "1", "CSE 120", "21", "SP", "Small");
        Course course5 = new Course(5, "1", "ECON 120", "2013", "SS1", "Small");
        //Simon's courses
        Course course6 = new Course(6, "2", "CSE 120", "21", "SP", "Small");
        Course course7 = new Course(7, "2", "ECON 120", "2013", "SS1", "Small");
        //Fabio's courses
        Course course8 = new Course(8, "3", "LTEA 138", "22", "WI", "Huge");
        Course course9 = new Course(9, "3", "CSE 110", "21", "WI", "Gigantic");
        //Tyler's courses

        Course course10 = new Course(10, "4", "CSE 110", "21", "WI", "Gigantic");
        Course course11 = new Course(11, "4", "CSE 101", "21", "FA", "Medium");
        Course course12 = new Course(12, "4", "CSE 120", "21", "SP", "Small");
        //Simon's sizePriority: 2 small courses --> 2*0.33=0.66
        person2.sizePriority = (float) 0.66;
        //Simon's recentPriority: SP21 & SS1 2013 --> age 2 & age 4+ --> 3+1=4
        person2.recentPriority = 4;
        //Fabio's sizePriority: 1 Huge, 1 Gigantic --> 0.06 + 0.03 = 0.09
        person3.sizePriority = (float) 0.09;
        //Fabio's recentPriority: WI22 & WI21 --> age 0 & age 3 --> 5+2=7
        person3.recentPriority = 7;
        //Tyler's sizePriority: 1 Small, 1 Medium, 1 Gigantic --> 0.33 + 0.18 + 0.03 = 0.54
        person4.sizePriority = (float) 0.54;
        //Tyler's recentPriority: WI21 & FA21 & SP21 --> age 3 & age 4 & age 2 --> 2+1+3=6
        person4.recentPriority = 6;

        //Adding all of them to the same session

        testDB.personWithCoursesDao().insert(person1);
        testDB.personWithCoursesDao().insert(person2);
        testDB.personWithCoursesDao().insert(person3);
        testDB.personWithCoursesDao().insert(person4);
        testDB.coursesDao().insert(course1);
        testDB.coursesDao().insert(course2);
        testDB.coursesDao().insert(course3);
        testDB.coursesDao().insert(course4);
        testDB.coursesDao().insert(course5);
        testDB.coursesDao().insert(course6);
        testDB.coursesDao().insert(course7);
        testDB.coursesDao().insert(course8);
        testDB.coursesDao().insert(course9);
        testDB.coursesDao().insert(course10);
        testDB.coursesDao().insert(course11);
        testDB.coursesDao().insert(course12);
    }

    @After
    public void cleanupDb() {
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase.singleton(context).close();
    }


    @Test
    public void test_sorts() {
        ActivityScenario<PersonListActivity> scenario = ActivityScenario.launch(PersonListActivity.class);
        Context context = ApplicationProvider.getApplicationContext();
        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            List<String> peopleInSession = new ArrayList<>();
            peopleInSession.add("2");
            peopleInSession.add("3");
            peopleInSession.add("4");
            Session newSession = new Session(UUID.randomUUID().toString(), "test");
            newSession.peopleIDs = peopleInSession;
            SharedPreferences preferences = context.getSharedPreferences("session",context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("currSession", newSession.sessionId);
            editor.apply();
            testDB.sessionsDao().insert(newSession);

            activity.updateAllLists();
            List<PersonWithCourses> list = activity.classMatesByNumCourses;

            Spinner sortOptionSpinnerView = activity.findViewById(R.id.sort_spinner);
            Button sortButton = activity.findViewById(R.id.apply_sort_button);
            sortButton.performClick();

            RecyclerView personRecyclerView = activity.findViewById(R.id.persons_view);
            PersonsViewAdapter personViewAdapter = (PersonsViewAdapter) personRecyclerView.getAdapter();
            List<PersonWithCourses> personList = (List<PersonWithCourses>) personViewAdapter.getPersons();
            assertEquals("4", personList.get(0).getId());


            sortOptionSpinnerView.setSelection(1);
            sortButton.performClick();
            personViewAdapter = (PersonsViewAdapter) personRecyclerView.getAdapter();
            personList = (List<PersonWithCourses>) personViewAdapter.getPersons();

            assert(personList.get(0).person.recentPriority > personList.get(1).person.recentPriority);
            assert(personList.get(1).person.recentPriority > personList.get(2).person.recentPriority);

            sortOptionSpinnerView.setSelection(2);
            sortButton.performClick();
            personViewAdapter = (PersonsViewAdapter) personRecyclerView.getAdapter();
            personList = (List<PersonWithCourses>) personViewAdapter.getPersons();
            assert(personList.get(0).person.sizePriority > personList.get(1).person.sizePriority);
            assert(personList.get(1).person.sizePriority > personList.get(2).person.sizePriority);

            Button stopButton = activity.findViewById(R.id.stopBtn);
            stopButton.performClick();
        });
    }

}