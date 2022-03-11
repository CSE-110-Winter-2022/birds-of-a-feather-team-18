package com.example.birdsofafeather;

import android.content.SharedPreferences;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;
import com.example.birdsofafeather.model.db.Person;
import com.example.birdsofafeather.model.db.PersonWithCourses;
import com.example.birdsofafeather.model.db.Session;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class FakedMessageListener extends MessageListener {
    private final MessageListener messageListener;
    //instantiate executor to execute messages
    private final ScheduledExecutorService executor;

    public FakedMessageListener(MessageListener realMessageListener, String messageStr, AppDatabase db, String sessionID) {
        this.messageListener = realMessageListener;
        this.executor = Executors.newSingleThreadScheduledExecutor();

        //Here we execute what we got from text
        executor.execute(() -> {
            //Set up message
            Message message = new Message(messageStr.getBytes(StandardCharsets.UTF_8));
            //If there is no message coming, we will put it to the database
            if (messageStr == null || messageStr.length() == 0) {
                System.out.println("No message");
            } //else if the messageStr is not null, we start implement then to the database
            else {
                this.messageListener.onFound(message);

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
                    String firstValue = array[0];
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

                    }                    //TODO: Here should put the real ourself UUID
                    else if (array[0].equals(selfId) || array[0].equals("4b295157-ba31-4f9f-8401-5d85d9cf659a")) {
                        //TODO: set the waving to ourself to true
                        isWave = true;
                    }
                    else if (array.length == 5){
                        year = array[0];
                        quarter = array[1];
                        courseType = array[2];
                        courseNum = array[3];
                        courseSize = array[4];
                        //set the course to the official version
                        text = quarter + year + ' ' + courseType + ' ' + courseNum;
                        int courseId = db.coursesDao().maxId() + 1;

                        //String personString = name + photoId;
                        //personId = UUID.nameUUIDFromBytes(personString.getBytes()).toString();

                        Course c = new Course(courseId, personId, text, year,quarter,courseSize);
                        if(userCourseText.contains(c.text) && !db.personWithCoursesDao().exists(personId)){
                            db.coursesDao().insert(c);
                        }
                    }
                }

                boolean s = db.personWithCoursesDao().exists(personId);
                if(!db.personWithCoursesDao().exists(personId)){
                    //set the student profile id
                    //TODO: implement priority of waving send
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

                        List<String> peopleInSession = db.sessionsDao().get(sessionID).peopleIDs;
                        if(!peopleInSession.contains(personId)) {
                            peopleInSession.add(personId);
                            Session updatedSession = new Session(sessionID, db.sessionsDao().get(sessionID).sessionName);
                            updatedSession.peopleIDs = peopleInSession;
                            db.sessionsDao().delete(db.sessionsDao().get(sessionID));
                            db.sessionsDao().insert(updatedSession);
                        }
                    }
                }
                scanner.close();
                //send back the message that the profile is set up
                this.messageListener.onLost(message);

            }
        });
    }
}
