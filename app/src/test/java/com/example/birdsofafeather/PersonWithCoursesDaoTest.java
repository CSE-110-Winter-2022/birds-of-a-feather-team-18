package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;
import com.example.birdsofafeather.model.db.Person;
import com.example.birdsofafeather.model.db.PersonWithCourses;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class PersonWithCoursesDaoTest {
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

    //Tests insert, getAll, get, maxId, deleteExcept user and
    //deleteAll for personWithCoursesDao
    @Test
    public void personWithCoursesDaoTest() {
        Person person1 = new Person(1, "Bob", DEFAULT_PHOTO);
        Person person2 = new Person(2, "Joe", DEFAULT_PHOTO);
        Course course1 = new Course(1, 1, "course1");
        Course course2 = new Course(2, 1, "course2");
        Course course3 = new Course(3, 2, "course3");
        Course course4 = new Course(4, 2, "course4");

        testDB.personWithCoursesDao().insert(person1);
        testDB.personWithCoursesDao().insert(person2);
        testDB.coursesDao().insert(course1);
        testDB.coursesDao().insert(course2);
        testDB.coursesDao().insert(course3);
        testDB.coursesDao().insert(course4);

        List<PersonWithCourses> actualPeopleList = testDB.personWithCoursesDao().getAll();
        String actualPeople = "";
        for(int i = 0; i < actualPeopleList.size(); i++) {
            actualPeople += actualPeopleList.get(i).getName();
        }
        testDB.personWithCoursesDao().updatePhoto("new photo", 1);

        assertEquals("BobJoe", actualPeople);
        assertEquals(2, testDB.personWithCoursesDao().count());
        assertEquals("Bob", testDB.personWithCoursesDao().get(1).getName());
        assertEquals(2, testDB.personWithCoursesDao().maxId());
        assertEquals("new photo", testDB.personWithCoursesDao().get(1).getPhoto());
        testDB.personWithCoursesDao().deleteExceptUser(1);
        assertEquals(1, testDB.personWithCoursesDao().count());
        testDB.personWithCoursesDao().deleteAll();
        assertEquals(0, testDB.personWithCoursesDao().count());
    }

}
