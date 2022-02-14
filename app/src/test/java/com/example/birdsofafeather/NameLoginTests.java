package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;

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
public class NameLoginTests {
    @Rule
    public ActivityScenarioRule<NameLoginActivity> scenarioRule = new ActivityScenarioRule<>(NameLoginActivity.class);

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
    public void test_no_name() {
        ActivityScenario<NameLoginActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            EditText newNameTextView = activity.findViewById(R.id.self_name_textview);
            Button confirmButton = activity.findViewById(R.id.confirm_button);

            newNameTextView.setText("");
            confirmButton.performClick();

            AlertDialog a = activity.getDialog();
            assertNotEquals(null, a.getButton(DialogInterface.BUTTON_POSITIVE));
            assertEquals(0, activity.db.personWithCoursesDao().count());
        });
    }

    @Test
    public void test_valid_name() {
        ActivityScenario<NameLoginActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            EditText newNameTextView = activity.findViewById(R.id.self_name_textview);
            Button confirmButton = activity.findViewById(R.id.confirm_button);

            newNameTextView.setText("Cabernet");
            confirmButton.performClick();

            assertEquals(1, activity.db.personWithCoursesDao().count());
            assertEquals("Cabernet", activity.db.personWithCoursesDao().get(1).person.name);
        });
    }

}