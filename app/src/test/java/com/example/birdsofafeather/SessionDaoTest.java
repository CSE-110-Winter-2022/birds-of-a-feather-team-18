package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;
import com.example.birdsofafeather.model.db.Person;
import com.example.birdsofafeather.model.db.Session;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class SessionDaoTest {
    AppDatabase testDB;

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

    //
    @Test
    public void sessionDaoTest() {
        Session session1 = new Session("1", "first");
        Session session2 = new Session("2", "second");
        session1.peopleIDs.add("personID1");
        session1.peopleIDs.add("personID2");
        session1.peopleIDs.add("personID3");
        session2.peopleIDs.add("personID1");

        testDB.sessionsDao().insert(session1);
        testDB.sessionsDao().insert(session2);

        testDB.sessionsDao().updateSessionName("newName", "1");

        assertEquals(2, testDB.sessionsDao().count());
        assertEquals(2, testDB.sessionsDao().getAll().size());

        assertEquals("newName", testDB.sessionsDao().get("1").sessionName);
        assertEquals("second", testDB.sessionsDao().get("2").sessionName);

        testDB.sessionsDao().delete(session1);
        assertEquals(null, testDB.sessionsDao().get("1"));
        assertEquals(1, testDB.sessionsDao().count());
    }

}
