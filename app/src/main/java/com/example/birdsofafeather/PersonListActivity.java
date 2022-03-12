package com.example.birdsofafeather;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;
import com.example.birdsofafeather.model.db.PersonWithCourses;
import com.example.birdsofafeather.model.db.Session;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

//set up the student taken common course list
public class PersonListActivity extends AppCompatActivity {
    private static final String TAG = "Bofs-Nearby";
    protected RecyclerView personsRecyclerView;
    protected RecyclerView.LayoutManager personsLayoutManager;
    protected PersonsViewAdapter personsViewAdapter;
    //set up data base
    private AppDatabase db;

    public List<PersonWithCourses> classMatesByNumCourses;
    private List<PersonWithCourses> classMatesByCourseSize;
    private List<PersonWithCourses> classMatesByRecentCourses;
    private Session currSession;

    private String newSortText;

    private Message mActiveMessage;
    private MessageListener messageListener;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10000;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_person_list);
        setTitle(R.string.app_title);



        personsRecyclerView = findViewById(R.id.persons_view);
        Spinner sortSpinner = findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> sortArrAdapter = ArrayAdapter.createFromResource
                (this,R.array.sort, android.R.layout.simple_spinner_item);
        sortArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sortSpinner.setAdapter(sortArrAdapter);
        // get default spinner value
        newSortText = sortSpinner.getSelectedItem().toString();

        db = AppDatabase.singleton(getApplicationContext());

        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        currSession = db.sessionsDao().get(preferences.getString("currSession",""));

        //Use a sorting to sort the number of common course
        //more common course, higher priority
        updateAllLists();

        //send the info to the viewAdapter
        updateView(classMatesByNumCourses);

        messageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.d(TAG, "Found message: " + new String(message.getContent()));
            }

            @Override
            public void onLost(Message message) {
                Log.d(TAG, "Lost sight of message: " + new String(message.getContent()));
            }
        };

        mActiveMessage = new Message("Hello World".getBytes());


    }

    public void onApplySortClicked(View view) {
        Spinner newSortSpinnerView = findViewById(R.id.sort_spinner);
        newSortText = newSortSpinnerView.getSelectedItem().toString();
        updateAllLists();
        switch (newSortText){
            case "Most Shared Classes":

                //send the info to the viewAdapter
                updateView(classMatesByNumCourses);
                break;
            case "Most Recent Classes":

                //send the info to the viewAdapter
                updateView(classMatesByRecentCourses);
                break;
            case "Smallest Classes":

                //send the info to the viewAdapter
                updateView(classMatesByCourseSize);
                break;
        }
    }

    @Override
    protected void onResume(){
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                updateAllListsAndCurrView();
                Log.i(TAG, "Updating view.");
            }
        }, delay);

        super.onResume();
        setContentView(R.layout.activity_person_list);
        setTitle(R.string.app_title);

        // Sort Selection Drop-down functionality
        Spinner sortSpinner = findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> sortArrAdapter = ArrayAdapter.createFromResource
                (this,R.array.sort, android.R.layout.simple_spinner_item);
        sortArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        sortSpinner.setAdapter(sortArrAdapter);
        sortSpinner.setSelection(sortArrAdapter.getPosition(newSortText));

        personsRecyclerView = findViewById(R.id.persons_view);

        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        currSession = db.sessionsDao().get(preferences.getString("currSession",""));

        updateAllListsAndCurrView();
    }
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
    }

    public void updateAllListsAndCurrView() {

        updateAllLists();

        switch (newSortText){
            case "Most Shared Classes":
                // refresh recyclerview with updated database
                updateView(classMatesByNumCourses);
                break;
            case "Most Recent Classes":
                // refresh recyclerview with updated database
                updateView(classMatesByRecentCourses);
                break;
            case "Smallest Classes":
                // refresh recyclerview with updated database
                updateView(classMatesByCourseSize);
                break;
        }
    }


    public void updateView(List<PersonWithCourses> sortedList) {
        personsLayoutManager = new LinearLayoutManager(this);
        personsRecyclerView.setLayoutManager(personsLayoutManager);

        personsViewAdapter = new PersonsViewAdapter(sortedList, db);
        personsRecyclerView.setAdapter(personsViewAdapter);
    }

    public void updateAllLists() {

        classMatesByNumCourses = new ArrayList<PersonWithCourses>();
        classMatesByCourseSize = new ArrayList<PersonWithCourses>();
        classMatesByRecentCourses = new ArrayList<PersonWithCourses>();
        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        currSession = db.sessionsDao().get(preferences.getString("currSession",""));
        if(currSession!=null) {
            List<String> ids = currSession.peopleIDs;
            for (int i = 0; i < ids.size(); i++) {
                classMatesByNumCourses.add(db.personWithCoursesDao().get(ids.get(i)));
                classMatesByCourseSize.add(db.personWithCoursesDao().get(ids.get(i)));
                classMatesByRecentCourses.add(db.personWithCoursesDao().get(ids.get(i)));
            }
        }

        classMatesByNumCourses.sort(new Comparator<PersonWithCourses>() {
            @Override
            public int compare(PersonWithCourses t1, PersonWithCourses t2) {
                if(!t1.getWavingToUs() && !t2.getWavingToUs()) {
                    return t2.getCourses().size() - t1.getCourses().size();
                }
                else if (t1.getWavingToUs()){
                    return -1;
                }
                else {
                    return 1;
                }
            }
        });

        classMatesByCourseSize.sort(new Comparator<PersonWithCourses>() {
            @Override
            public int compare(PersonWithCourses t1, PersonWithCourses t2) {
                if(!t1.getWavingToUs() && !t2.getWavingToUs()) {
                    if(t2.person.sizePriority>t1.person.sizePriority){
                        return 1;
                    } else if(t2.person.sizePriority<t1.person.sizePriority){
                        return -1;
                    }
                    return 0;
                }
                else if (t1.getWavingToUs()){
                    return -1;
                }
                else {
                    return 1;
                }
            }

        });

        classMatesByRecentCourses.sort(new Comparator<PersonWithCourses>() {
            @Override
            public int compare(PersonWithCourses t1, PersonWithCourses t2) {
                if(!t1.getWavingToUs() && !t2.getWavingToUs()) {
                    return t2.person.recentPriority - t1.person.recentPriority;
                }
                else if (t1.getWavingToUs()){
                    return -1;
                }
                else {
                    return 1;
                }

            }
        });
    }

    public String createCSV() {
        String selfName = db.personWithCoursesDao().get("1").getName();
        String selfPhotoURL = db.personWithCoursesDao().get("1").getPhoto();
        String selfString = selfName + selfPhotoURL;
        String selfId = UUID.nameUUIDFromBytes(selfString.getBytes()).toString();
        List<Course> selfCourses = db.coursesDao().getForPerson("1");
        String allCourses = "";
        for(int i = 0; i < selfCourses.size(); i++) {
            Course currCourse = selfCourses.get(i);
            String[] textInfo = currCourse.text.split(" ");
            allCourses += currCourse.year + "," + currCourse.quarter + "," + textInfo[1] + "," + textInfo[2] + "," + currCourse.size;
            if(i != selfCourses.size() - 1) {
                allCourses += "\n";
            }
        }
        List<PersonWithCourses> peopleWavingTo = db.personWithCoursesDao().getAllWavingToThem();
        String allWaves = "";
        for(int i = 0; i < peopleWavingTo.size(); i++) {
            PersonWithCourses currPerson = peopleWavingTo.get(i);
            allWaves += currPerson.getId() + ",wave" + ",,,";
            if(i != peopleWavingTo.size() - 1) {
                allWaves += "\n";
            }
        }
        String selfCSV = "";

        if(peopleWavingTo.isEmpty()) {
            selfCSV = selfId + ",,,,\n" + selfName + ",,,,\n" + selfPhotoURL + ",,,,\n" + allCourses;
        }
        else {
            selfCSV = selfId + ",,,,\n" + selfName + ",,,,\n" + selfPhotoURL + ",,,,\n" + allCourses + "\n" + allWaves;
        }

        return selfCSV;
    }

    public void onStartClicked(View view) throws InterruptedException {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        //display message asking to choose previous or new session
        alertBuilder.setTitle("Starting Session")
                .setMessage("Resume a previous session or start a new one?")
                .setCancelable(true)
                .setPositiveButton("New", (dialog,id) -> {
                    //Create a new session and name it with the current date and time and start searching
                    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mmaa");
                    String dateString = dateFormat.format(new Date()).toString();
                    Session newSession = new Session(UUID.randomUUID().toString(), dateString);
                    SharedPreferences preferences = getSharedPreferences("session",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("currSession", newSession.sessionId);
                    editor.apply();
                    db.sessionsDao().insert(newSession);

                    Intent intent = new Intent(PersonListActivity.this, SearchService.class);
                    startService(intent);
                    Log.d(TAG, "Start Clicked, service start");
                    dialog.cancel();

                })
                .setNegativeButton("Previous", (dialog,id) -> {
                    //go to previous session
                    Intent intent = new Intent(PersonListActivity.this, SessionListActivity.class);
                    startActivity(intent);
                });
        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

        publish(createCSV());
        subscribe();
    }

    //bind the button to stop service
    public void onStopClicked(View view) {
        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        Session s = db.sessionsDao().get(preferences.getString("currSession",""));
        if(isMyServiceRunning(SearchService.class)) {
            showNameSessionDialog(this);
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("currSession", "none");
        editor.apply();

        updateAllListsAndCurrView();

        Intent intent = new Intent(PersonListActivity.this, SearchService.class);
        stopService(intent);
        Log.d(TAG, "Stop Clicked, service stop");
    }

    public void showNameSessionDialog(Context c) {
        // Get all courses for this quarter as
        CharSequence[] myCoursesCS = getCurrentCourses();

        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        Session s = db.sessionsDao().get(preferences.getString("currSession",""));
        final EditText taskEditText = new EditText(c);
        android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(c)
                .setTitle("Name Session")
                .setItems(myCoursesCS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = (String)myCoursesCS[i];
                        db.sessionsDao().updateSessionName(name, s.sessionId);
                        Log.d(TAG, "Session Name: " + db.sessionsDao().get(s.sessionId).sessionName);
                        dialogInterface.cancel();
                    }
                })
                .setView(taskEditText)
                .setPositiveButton("Name", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = String.valueOf(taskEditText.getText());
                        if(!name.equals("")) {
                            db.sessionsDao().updateSessionName(name, s.sessionId);
                        }
                        Log.d(TAG, "Session Name: " + db.sessionsDao().get(s.sessionId).sessionName);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d(TAG, "Session Name: " + db.sessionsDao().get(s.sessionId).sessionName);
                        dialogInterface.cancel();
                    }
                })
                .create();
        dialog.show();

        //unpublish and unsubscribe the message
        unpublish();
        unsubscribe();
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

    // Get courses for this quarter
    private CharSequence[] getCurrentCourses(){
        // Get List of all my courses
        List<String> myCourses = db.personWithCoursesDao().get("1").getCourses();
        // Get current Year, Month, Week
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        String thisQuarter;
        int thisMonth = Calendar.getInstance().get(Calendar.MONTH);
        int thisWeek = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
        // Determine the current Quarter
        if (thisMonth > 9 || (thisMonth == 9 && thisWeek >= 3)) {
            thisQuarter = "FA";
        } else if (thisMonth < 3 || (thisMonth == 3 && thisWeek <= 3)) {
            thisQuarter = "WI";
        } else if ((thisMonth == 3 && thisWeek >= 4) || (thisMonth > 3 && thisMonth < 6) || (thisMonth == 6 && thisWeek <= 2)) {
            thisQuarter = "SP";
        } else if ((thisMonth == 6 && thisWeek >= 3) || thisMonth == 7) {
            thisQuarter = "SS1";
        } else if((thisMonth == 8 && thisWeek >= 1) || (thisWeek == 1 && thisMonth == 9)){
            thisQuarter = "SS2";
        } else {
            thisQuarter = "SSS";
        }

        // Current Quarter + Year (i.e WI2022)
        String currQuarterYear = thisQuarter + String.valueOf(thisYear);

        // Find all Current Courses
        List<String> currCoursesList = new ArrayList<>();
        for(int i = 0; i < myCourses.size(); i++){
            int indexOfSpace = myCourses.get(i).indexOf(" ");
            String checkCourseQuarter = myCourses.get(i).substring(0, indexOfSpace);
            if(currQuarterYear.equals(checkCourseQuarter)) {
                currCoursesList.add(myCourses.get(i));
            }
        }

        // Return current Courses as CharSequence[] so AlertDialog can use
        CharSequence[] myCoursesCS = currCoursesList.toArray(new CharSequence[currCoursesList.size()]);
        return myCoursesCS;
    }

    private void publish(String message) {
        Log.i(TAG, "Publishing message: " + message);

        mActiveMessage = new Message(message.getBytes());
        Nearby.getMessagesClient(this).publish(mActiveMessage);
    }

    private void unpublish() {
        Log.i(TAG, "Unpublishing.");
        if (mActiveMessage != null) {
            Nearby.getMessagesClient(this).unpublish(mActiveMessage);
            mActiveMessage = null;
        }
    }

    private void subscribe() {
        Log.i(TAG, "Subscribing.");
        Nearby.getMessagesClient(this).subscribe(messageListener);
    }

    private void unsubscribe() {
        Log.i(TAG, "Unsubscribing.");
        Nearby.getMessagesClient(this).unsubscribe(messageListener);
    }
}
