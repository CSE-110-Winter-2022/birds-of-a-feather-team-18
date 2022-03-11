package com.example.birdsofafeather;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;

import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PrevClassesTest {

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
    public void test_no_repeat_courses() {
        ActivityScenario<PrevCourseActivity> scenario = ActivityScenario.launch(PrevCourseActivity.class);

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            // Get EditText/Spinner for Quarter/Year/Subject/CourseNum
            EditText newSubjectTextView = activity.findViewById(R.id.subject_view);
            Spinner newYearSpinnerView = activity.findViewById(R.id.year_spinner);
            Spinner newQuarterSpinnerView = activity.findViewById(R.id.quarter_spinner);
            EditText newCourseNumTextView = activity.findViewById(R.id.course_number_view);

            //Get addButton
            Button addButton = activity.findViewById(R.id.add_prev_course_button);

            //Set Subject = CSE and CourseNum = 110
            newSubjectTextView.setText("CSE");
            newCourseNumTextView.setText("110");

            //Select year drop-down menu item 10 (item 10 = 2022)
            newYearSpinnerView.setSelection(10);
            //Select quarter drop-down menu item 1 (item 1 = WI)
            newQuarterSpinnerView.setSelection(1);
            //Press add button
            addButton.performClick();

            //Add a duplicate class
            newSubjectTextView.setText("CSE");
            newYearSpinnerView.setSelection(10);
            newQuarterSpinnerView.setSelection(1);
            newCourseNumTextView.setText("110");
            addButton.performClick();

            //No duplicate classes added
            assertEquals(1, activity.getCourseCount());
        });
    }

    @Test
    public void test_add_class() {
        ActivityScenario<PrevCourseActivity> scenario = ActivityScenario.launch(PrevCourseActivity.class);

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            // Get EditText/Spinner for Quarter/Year/Subject/CourseNum
            EditText newSubjectTextView = activity.findViewById(R.id.subject_view);
            Spinner newYearSpinnerView = activity.findViewById(R.id.year_spinner);
            Spinner newQuarterSpinnerView = activity.findViewById(R.id.quarter_spinner);
            EditText newCourseNumTextView = activity.findViewById(R.id.course_number_view);
            Button addButton = activity.findViewById(R.id.add_prev_course_button);

            //Set Subject = CSE and CourseNum = 110
            newSubjectTextView.setText("CSE");
            newCourseNumTextView.setText("110");

            //Select year drop-down menu item 10 (item 10 = 2022)
            newYearSpinnerView.setSelection(10);
            //Select quarter drop-down menu item 1 (item 1 = WI)
            newQuarterSpinnerView.setSelection(1);
            //Press add button
            addButton.performClick();

            List<Course> courses = activity.getCourses();

            //Test if course was added
            assertEquals(1, courses.size());
            //Test if course string is correct
            assertEquals("WI2022 CSE 110", courses.get(0).text);
        });
    }

    @Test
    public void testDropDown(){
        ActivityScenario<PrevCourseActivity> scenario = ActivityScenario.launch(PrevCourseActivity.class);

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            // Get EditText/Spinner for Quarter/Year/Subject/CourseNum
            EditText newSubjectTextView = activity.findViewById(R.id.subject_view);
            Spinner newYearSpinnerView = activity.findViewById(R.id.year_spinner);
            Spinner newQuarterSpinnerView = activity.findViewById(R.id.quarter_spinner);
            EditText newCourseNumTextView = activity.findViewById(R.id.course_number_view);
            Button addButton = activity.findViewById(R.id.add_prev_course_button);

            //Set Subject = CSE and CourseNum = 110
            newSubjectTextView.setText("CSE");
            newCourseNumTextView.setText("110");

            //press add button
            addButton.performClick();
            List<Course> courses = activity.getCourses();
            //default drop-down selection - year = 2012 / quarter = FA
            assertEquals(1, courses.size());
            assertEquals("FA2012 CSE 110", courses.get(0).text);

            //move both drop-down to last selection - year = 2022 / quarter = SSS
            newYearSpinnerView.setSelection(10);
            newQuarterSpinnerView.setSelection(5);
            addButton.performClick();
            courses = activity.getCourses();

            //additional course was added
            assertEquals(2, courses.size());
            //additional course string is correct
            assertEquals("SSS2022 CSE 110", courses.get(1).text);
        });
    }
}