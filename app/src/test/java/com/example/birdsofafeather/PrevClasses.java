package com.example.birdsofafeather;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;
import com.example.birdsofafeather.model.db.Person;

import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PrevClasses {
    @Rule
    public ActivityScenarioRule<PrevCourseActivity> scenarioRule = new ActivityScenarioRule<>(PrevCourseActivity.class);

    @Before
    public void setupDb() {
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase.useTestDatabase(context);
    }

    @After
    public void cleanupDb() {
        Context context = ApplicationProvider.getApplicationContext();
        AppDatabase.singleton(context).close();
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void test_no_repeat_courses() {
        ActivityScenario<PrevCourseActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            EditText newSubjectTextView = activity.findViewById(R.id.subject_view);
            Spinner newYearSpinnerView = activity.findViewById(R.id.year_spinner);
            Spinner newQuarterSpinnerView = activity.findViewById(R.id.quarter_spinner);
            EditText newCourseNumTextView = activity.findViewById(R.id.course_number_view);
            Button addButton = activity.findViewById(R.id.add_prev_course_button);

            newSubjectTextView.setText("CSE");
            newYearSpinnerView.setSelection(10);
            newQuarterSpinnerView.setSelection(1);
            newCourseNumTextView.setText("110");
            addButton.performClick();

            newSubjectTextView.setText("CSE");
            newYearSpinnerView.setSelection(10);
            newQuarterSpinnerView.setSelection(1);
            newCourseNumTextView.setText("110");
            addButton.performClick();


            assertEquals(1, activity.getCourseCount());
        });
    }

    @Test
    public void test_add_class() {
        ActivityScenario<PrevCourseActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            EditText newSubjectTextView = activity.findViewById(R.id.subject_view);
            Spinner newYearSpinnerView = activity.findViewById(R.id.year_spinner);
            Spinner newQuarterSpinnerView = activity.findViewById(R.id.quarter_spinner);
            EditText newCourseNumTextView = activity.findViewById(R.id.course_number_view);
            Button addButton = activity.findViewById(R.id.add_prev_course_button);

            newSubjectTextView.setText("CSE");
            newYearSpinnerView.setSelection(10);
            newQuarterSpinnerView.setSelection(1);
            newCourseNumTextView.setText("110");
            addButton.performClick();

            List<Course> courses = activity.getCourses();

            assertEquals(1, courses.size());
            assertEquals("Winter 2022 CSE 110", courses.get(0).text);
        });
    }
}