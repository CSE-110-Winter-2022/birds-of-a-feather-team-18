package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;
import com.example.birdsofafeather.model.db.Person;
import com.google.android.gms.nearby.messages.MessageListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class EnterCSVTextBoxTest {
    AppDatabase testDB;
    final String DEFAULT_PHOTO = "https://st3.depositphotos.com/4111759/13425/v/600/depositphotos_134255532-stock-illustration-profile-placeholder-male-default-profile.jpg";

    @Rule
    public ActivityScenarioRule<MainActivity> scenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Before
    public void setupDb() {
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase.useTestDatabase(context);
        testDB = AppDatabase.singleton(context);
        //insert user with courses
        Person user = new Person(1, "user", DEFAULT_PHOTO);
        testDB.personWithCoursesDao().insert(user);
        Course c1 = new Course(testDB.coursesDao().maxId()+1, 1, "FA2021 CSE 210");
        testDB.coursesDao().insert(c1);
        Course c2 = new Course(testDB.coursesDao().maxId()+1, 1, "SP2022 CSE 110");
        testDB.coursesDao().insert(c2);
        Course c3 = new Course(testDB.coursesDao().maxId()+1, 1, "WI2022 CSE 110");
        testDB.coursesDao().insert(c3);
    }

    @After
    public void cleanupDb() {
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase.singleton(context).close();
    }

    @Test
    public void testEmptyTextAfterEnter() {
        ActivityScenario<MainActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            EditText studentProfile = activity.findViewById(R.id.student_profile);
            Button enterButton = activity.findViewById(R.id.enter_button);

            studentProfile.setText("Bill,,,\n " +
                    "https://lh3.googleusercontent.com/pw/AM-JKLXQ2ix4dg-PzLrPOSMOOy6M3PSUrijov9jCLXs4IGSTwN73B4kr-F6Nti_4KsiUU8LzDSGPSWNKnFdKIPqCQ2dFTRbARsW76pevHPBzc51nceZDZrMPmDfAYyI4XNOnPrZarGlLLUZW9wal6j-z9uA6WQ=w854-h924-no?authuser=0,,,\n" +
                    "2021,FA,CSE,211\n2022,WI,CSE,111\n2022,SP,CSE,111");
            enterButton.performClick();

            assertEquals("", studentProfile.getText().toString());
        });
    }
}