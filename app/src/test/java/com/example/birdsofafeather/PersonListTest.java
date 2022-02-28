package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;
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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PersonListTest {
    @Rule
    public ActivityScenarioRule<PrevCourseActivity> scenarioRule = new ActivityScenarioRule<>(PrevCourseActivity.class);

    AppDatabase testDB;
    final String DEFAULT_PHOTO = "https://st3.depositphotos.com/4111759/13425/v/600/depositphotos_134255532-stock-illustration-profile-placeholder-male-default-profile.jpg";

    @Before
    public void setupDb() {
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase.useTestDatabase(context);
        testDB = AppDatabase.singleton(context);
    }

    @After
    public void cleanupDb() {
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase.singleton(context).close();
    }

    @Test
    public void test_sorts() {
        Person person1 = new Person(1, "User", DEFAULT_PHOTO);
        Person person2 = new Person(2, "Small Simon", DEFAULT_PHOTO);
        Person person3 = new Person(3, "Freshman Fabio", DEFAULT_PHOTO);
        Person person4 = new Person(4, "Tryhard Tyler", DEFAULT_PHOTO);
        //user's courses
        Course course1 = new Course(1, 1, "LTEA 138", "22", "WI", "Huge");
        Course course2 = new Course(2, 1, "CSE 110", "21", "WI", "Gigantic");
        Course course3 = new Course(3, 1, "CSE 101", "21", "FA", "Medium");
        Course course4 = new Course(4, 1, "CSE 120", "21", "SP", "Small");
        Course course5 = new Course(5, 1, "ECON 120", "2013", "SS1", "Small");
        //Simon's courses
        Course course6 = new Course(6, 2, "CSE 120", "21", "SP", "Small");
        Course course7 = new Course(7, 2, "ECON 120", "2013", "SS1", "Small");
        //Fabio's courses
        Course course8 = new Course(8, 3, "LTEA 138", "22", "WI", "Huge");
        Course course9 = new Course(9, 3, "CSE 110", "21", "WI", "Gigantic");
        //Tyler's courses
        Course course10 = new Course(10, 4, "CSE 110", "21", "WI", "Gigantic");
        Course course11 = new Course(11, 4, "CSE 101", "21", "FA", "Medium");
        Course course12 = new Course(12, 4, "CSE 120", "21", "SP", "Small");

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


        ActivityScenario<PrevCourseActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            Spinner sortOptionSpinnerView = activity.findViewById(R.id.sort_spinner);
            Button sortButton = activity.findViewById(R.id.apply_sort_button);
            sortButton.performClick();

            RecyclerView personRecyclerView = activity.findViewById(R.id.persons_view);
            PersonsViewAdapter personViewAdapter = (PersonsViewAdapter) personRecyclerView.getAdapter();
            List<PersonWithCourses> personList = (List<PersonWithCourses>) personViewAdapter.getPersons();
            assertEquals(4, personList.get(0).getId());


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

        });
    }
}