package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import android.app.ActivityManager;
import android.content.Context;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Bofs-Nearby";
    private MessageListener messageListener;
    private AppDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_title);

        db = AppDatabase.singleton(this);

        //clear database of all persons and courses
        db.coursesDao().deleteExceptUser(1);
        db.personWithCoursesDao().deleteExceptUser(1);

        //if the database is empty, start the login activity
        if (db.personWithCoursesDao().count() == 0) {
            Intent intent = new Intent(this, NameLoginActivity.class);
            startActivity(intent);
        }

        // Instantiate message listener

        MessageListener realListener = new MessageListener() {};

        TextView profile = findViewById(R.id.student_profile);
        String test = profile.getText().toString();

        this.messageListener = new FakedMessageListener(realListener, test, db);
    }

    //check if search service is on or off
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            //if service is on, return true
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void onTestClicked(View view) {
        // Start personListActivity
        Intent intent = new Intent(this, PersonListActivity.class);
        startActivity(intent);
    }



    public void onMockClicked(View view) {
        // Get CSV file
        TextView profile = findViewById(R.id.student_profile);
        String test = profile.getText().toString();

        //if the search is on, send the message to database
        if (isMyServiceRunning(SearchService.class)) {
            MessageListener realListener = new MessageListener() {

                // Log to show that message is received
                @Override
                public void onFound(@NonNull Message message) {
                    Log.d(TAG, "Found Profile: " + new String(message.getContent()));
                }

                // Log to show that profile from CSV saved to database
                @Override
                public void onLost(@NonNull Message message) {
                    Log.d(TAG, "Profile loaded in DataBase " + new String(message.getContent()));

                    // Log to get number of courses and number of common courses
                    Log.d(TAG, "Number of classmates: " + new String(String.valueOf(db.personWithCoursesDao().count() - 1)));
                    Log.d(TAG, "Number of common courses: " + new String(String.valueOf(db.coursesDao().getForPerson(db.personWithCoursesDao().maxId()).size())));
                }
            };

            AppDatabase db = AppDatabase.singleton(this);
            //Use messageListener to save profile in database
            this.messageListener = new FakedMessageListener(realListener, test, db);
        } else {
            //if service is not on, will show a toast message
            Toast.makeText(this,"Service is not on", Toast.LENGTH_SHORT).show();
        }
        profile.setText("");

        if (isMyServiceRunning(SearchService.class)){

            Log.d(TAG, "Mock Clicked, service on: should work");
        }
        else{

            Log.d(TAG, "Mock Clicked, service off: shouldn't work");
        }
    }

    // Starting bluetooth
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        Nearby.getMessagesClient(this).subscribe(messageListener);
    }

    // Stop bluetooth
    @Override
    protected void onStop() {
        super.onStop();
        Nearby.getMessagesClient(this).unsubscribe(messageListener);
    }
}
