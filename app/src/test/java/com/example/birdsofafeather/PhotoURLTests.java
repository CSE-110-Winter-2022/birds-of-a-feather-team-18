package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.Lifecycle;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Person;

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
public class PhotoURLTests {
    @Rule
    public ActivityScenarioRule<PhotoURLActivity> scenarioRule = new ActivityScenarioRule<>(PhotoURLActivity.class);

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
    public void test_no_URL() {
        ActivityScenario<PhotoURLActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            //simulate entering a name in NameLoginActivity
            activity.db.personWithCoursesDao().insert(new Person(1, "Cabernet", "https://st3.depositphotos.com/4111759/13425/v/600/depositphotos_134255532-stock-illustration-profile-placeholder-male-default-profile.jpg"));

            EditText newPhotoTextView = activity.findViewById(R.id.photo_url_textview);
            Button submitButton = activity.findViewById(R.id.submit_button);

            newPhotoTextView.setText("");
            submitButton.performClick();

            assertEquals("https://st3.depositphotos.com/4111759/13425/v/600/depositphotos_134255532-stock-illustration-profile-placeholder-male-default-profile.jpg",
                    activity.db.personWithCoursesDao().get(1).getPhoto());
        });
    }

    @Test
    public void test_invalid_URL() {
        ActivityScenario<PhotoURLActivity> scenario = scenarioRule.getScenario();

        scenario.moveToState(Lifecycle.State.CREATED);

        scenario.onActivity(activity -> {
            //simulate entering a name in NameLoginActivity
            activity.db.personWithCoursesDao().insert(new Person(1, "Cabernet", "https://st3.depositphotos.com/4111759/13425/v/600/depositphotos_134255532-stock-illustration-profile-placeholder-male-default-profile.jpg"));

            EditText newNameTextView = activity.findViewById(R.id.photo_url_textview);
            Button submitButton = activity.findViewById(R.id.submit_button);

            newNameTextView.setText("Not a photo");
            submitButton.performClick();

            AlertDialog a = activity.getDialog();
            assertNotEquals(null, a.getButton(DialogInterface.BUTTON_POSITIVE));
            assertEquals("https://st3.depositphotos.com/4111759/13425/v/600/depositphotos_134255532-stock-illustration-profile-placeholder-male-default-profile.jpg",
                    activity.db.personWithCoursesDao().get(1).getPhoto());
        });
    }

}