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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;
import com.example.birdsofafeather.model.db.Person;
import com.example.birdsofafeather.model.db.PersonWithCourses;
import com.example.birdsofafeather.model.db.Session;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

//set up the student taken common course list
public class PersonListActivity extends AppCompatActivity {
    private static final String TAG = "Bofs-Nearby";
    protected RecyclerView personsRecyclerView;
    protected RecyclerView.LayoutManager personsLayoutManager;
    protected PersonsViewAdapter personsViewAdapter;
    //set up data base
    private AppDatabase db;

    private static final int TTL_SECONDS = 20;
    private static final Strategy PUB_STRATEGY = new Strategy.Builder().setTtlSeconds(TTL_SECONDS).build();

    public List<PersonWithCourses> classMatesByNumCourses;
    private List<PersonWithCourses> classMatesByCourseSize;
    private List<PersonWithCourses> classMatesByRecentCourses;
    private Session currSession;

    private String newSortText;

    private Message mActiveMessage;
    private MessageListener messageListener;

    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5000;

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
            public void onFound(final Message message) {
                String currSessionID = preferences.getString("currSession", "");
                String messageStr = new String(message.getContent());
                //If there is no message coming, we will put it to the database
                if (messageStr == null || messageStr.length() == 0) {
                    System.out.println("No message");
                } //else if the messageStr is not null, we start implement then to the database
                else {
                    Log.d(TAG, "Found message: " + new String(message.getContent()));
                    //use a scanner to scan the line from CSV profile
                    Scanner scanner = new Scanner(messageStr);
                    //String line = "";
                    String csvSplitBy = ",";

                    //get selfID
                    String selfString = db.personWithCoursesDao().get("1").getName() + db.personWithCoursesDao().get("1").getPhoto();
                    String selfId = UUID.nameUUIDFromBytes(selfString.getBytes()).toString();

                    //count means the line of the profile, 0 means uuid, 1 means name, 2 means Photo Url
                    int count = 0;
                    String name = null;
                    String photoId = null;
                    String year;
                    String quarter;
                    String courseType;
                    String courseNum;
                    String text;
                    String courseSize;
                    Boolean isWave = false;
                    //get the user courses from db
                    List<Course> userCourses = db.coursesDao().getForPerson("1");
                    //create a list string to store the courses
                    List<String> userCourseText = new ArrayList<>();
                    //start implement the user courses, then we can make comparison
                    for(int i = 0; i < userCourses.size(); i++){
                        userCourseText.add(userCourses.get(i).text);
                    }

                    String personId = "";
                    //start use scanner to scan each line
                    while (scanner.hasNextLine()) {
                        String[] array = scanner.nextLine().split(csvSplitBy);
                        //set UUID
                        if(count == 0) {
                            personId = array[0];
                            count++;
                        }
                        //set profile name
                        else if (count == 1) {
                            name = array[0];
                            count++;
                            //set profile photo url
                        } else if (count == 2) {
                            photoId = array[0];
                            count++;
                            //set profile courses

                        }
                        else if (array[0].equals(selfId) || array[0].equals("4b295157-ba31-4f9f-8401-5d85d9cf659a")) {
                            isWave = true;
                            if(db.personWithCoursesDao().exists(personId)) {
                                db.personWithCoursesDao().updateWavingToUs(true, personId);
                            }
                        }
                        //create this person's courses
                        else if (array.length == 5){
                            year = array[0];
                            quarter = array[1];
                            courseType = array[2];
                            courseNum = array[3];
                            courseSize = array[4];
                            //set the course to the official version
                            text = quarter + year + ' ' + courseType + ' ' + courseNum;
                            int courseId = db.coursesDao().maxId() + 1;

                            //Create the course, but only insert it into the db if the user also has this course
                            Course c = new Course(courseId, personId, text, year,quarter,courseSize);
                            if(userCourseText.contains(c.text) && !db.personWithCoursesDao().exists(personId)){
                                db.coursesDao().insert(c);
                            }
                        }
                    }

                    //If the person doesn't already exist in the database, add them to it
                    if(!db.personWithCoursesDao().exists(personId)){
                        //set the student profile id
                        Person newPerson = new Person(personId, name, photoId, false);
                        newPerson.wavingToUs = isWave;
                        //only add the person when there are common course with user
                        List<Course> newPersonCourses = db.coursesDao().getForPerson(personId);
                        if (newPersonCourses.size() != 0) {
                            //calculate the size and recency priorities for this new person
                            float sizePrio = 0;
                            int recentPrio = 0;
                            int thisYear = Calendar.getInstance().get(Calendar.YEAR);
                            int thisQuarter;
                            int thisMonth = Calendar.getInstance().get(Calendar.MONTH);
                            int thisWeek = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
                            if (thisMonth > 9 || (thisMonth == 9 && thisWeek >= 3)) {
                                thisQuarter = 1;
                            } else if (thisMonth < 3 || (thisMonth == 3 && thisWeek <= 3)) {
                                thisQuarter = 2;
                            } else if ((thisMonth == 3 && thisWeek >= 4) || (thisMonth > 3 && thisMonth < 6) || (thisMonth == 6 && thisWeek <= 2)) {
                                thisQuarter = 3;
                            } else if ((thisMonth == 6 && thisWeek >= 3) || thisMonth == 7) {
                                thisQuarter = 4;
                            } else {
                                thisQuarter = 4;
                            }
                            for (int i = 0; i < newPersonCourses.size(); i++) {
                                int yearAge = (thisYear - Integer.parseInt(newPersonCourses.get(i).year)) * 4;
                                String courseQuarter = newPersonCourses.get(i).quarter;
                                int courseQ = 0;
                                if (courseQuarter.equals("FA")) {
                                    courseQ = 1;
                                } else if (courseQuarter.equals("WI")) {
                                    courseQ = 2;
                                } else if (courseQuarter.equals("SP")) {
                                    courseQ = 3;
                                } else if (courseQuarter.equals("SS1") || courseQuarter.equals("SS2") || courseQuarter.equals("SSS")) {
                                    courseQ = 4;
                                }
                                int courseAge = thisQuarter - courseQ;
                                int age = yearAge + courseAge;
                                recentPrio = Integer.max(5 - age, 1);
                                switch (newPersonCourses.get(i).size) {
                                    case "Tiny":
                                        sizePrio += 1;
                                        break;
                                    case "Small":
                                        sizePrio += 0.33;
                                        break;
                                    case "Medium":
                                        sizePrio += 0.18;
                                        break;
                                    case "Large":
                                        sizePrio += 0.10;
                                        break;
                                    case "Huge":
                                        sizePrio += 0.06;
                                        break;
                                    case "Gigantic":
                                        sizePrio += 0.03;
                                        break;
                                }
                            }
                            newPerson.sizePriority = sizePrio;
                            newPerson.recentPriority = recentPrio;
                            db.personWithCoursesDao().insert(newPerson);
                        }
                    }

                    //add person into the current session
                    List<String> peopleInSession = db.sessionsDao().get(currSessionID).peopleIDs;
                    if(db.personWithCoursesDao().exists(personId) && !peopleInSession.contains(personId)) {
                        peopleInSession.add(personId);
                        Session updatedSession = new Session(currSessionID, db.sessionsDao().get(currSessionID).sessionName);
                        updatedSession.peopleIDs = peopleInSession;
                        db.sessionsDao().delete(db.sessionsDao().get(currSessionID));
                        db.sessionsDao().insert(updatedSession);
                    }

                    scanner.close();
                    //send back the message that the profile is set up
                }
            }

            @Override
            public void onLost(Message message) {
                Log.d(TAG, "Lost sight of message: " + new String(message.getContent()));
            }
        };
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

                subscribe();
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
        PublishOptions options = new PublishOptions.Builder().setStrategy(PUB_STRATEGY).setCallback(new PublishCallback() {
            @Override
            public void onExpired() {
                super.onExpired();
                Log.i(TAG, "No longer published");
            }
        }).build();
        mActiveMessage = new Message(message.getBytes());
        Nearby.getMessagesClient(this).publish(mActiveMessage, options);
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
        SubscribeOptions options = new SubscribeOptions.Builder().setStrategy(PUB_STRATEGY).setCallback(new SubscribeCallback() {
            @Override
            public void onExpired() {
                super.onExpired();
                Log.i(TAG, "No longer subscribe");
            }
        }).build();
        Nearby.getMessagesClient(this).subscribe(messageListener, options);
    }

    private void unsubscribe() {
        Log.i(TAG, "Unsubscribing.");
        Nearby.getMessagesClient(this).unsubscribe(messageListener);
    }
}
