package com.example.birdsofafeather;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Course;
import com.example.birdsofafeather.model.db.Person;
import com.example.birdsofafeather.model.db.PersonWithCourses;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class FakedMessageListener extends MessageListener {
    private final MessageListener messageListener;
    //instantiate executor to execute messages
    private final ScheduledExecutorService executor;

    public FakedMessageListener(MessageListener realMessageListener, String messageStr, AppDatabase db) {
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

                //count means the line of the profile, 0 means name, 1 means Photo Url
                int count = 0;
                String name = null;
                String photoId = null;
                String year;
                String quarter;
                String courseType;
                String courseNum;
                String text;
                //get the user courses from db
                List<Course> userCourses = db.coursesDao().getForPerson(1);
                //create a list string to store the courses
                List<String> userCourseText = new ArrayList<>();
                //start implement the user courses, then we can make comparison
                for(int i = 0; i < userCourses.size(); i++){
                    userCourseText.add(userCourses.get(i).text);
                }
                //set the student profile id
                int personId = db.personWithCoursesDao().maxId() + 1;
                //start use scanner to scan each line
                while (scanner.hasNextLine()) {
                    String[] array = scanner.nextLine().split(csvSplitBy);
                    //set profile name
                    if (count == 0) {
                        name = array[0];
                        count++;
                    //set profile photo url
                    } else if (count == 1) {
                        photoId = array[0];
                        count++;
                    //set profile courses
                    } else {
                        year = array[0];
                        quarter = array[1];
                        courseType = array[2];
                        courseNum = array[3];
                        //set the course to the official version
                        text = quarter + year + ' ' + courseType + ' ' + courseNum;
                        int courseId = db.coursesDao().maxId() + 1;

                        //TODO: Put size functionality for mocked bluetooth
                        Course c = new Course(courseId, personId, text, year,quarter,"");
                        if(userCourseText.contains(c.text)){
                            db.coursesDao().insert(c);
                        }
                    }
                }
                Person newPerson = new Person(personId, name, photoId);
                //only add the person when there are common course with user
                if (db.coursesDao().getForPerson(personId).size() != 0){
                    db.personWithCoursesDao().insert(newPerson);
                }
                scanner.close();

                //send back the message that the profile is set up
                this.messageListener.onLost(message);

            }
        });
    }
}
