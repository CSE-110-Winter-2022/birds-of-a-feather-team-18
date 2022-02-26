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
    private final ScheduledExecutorService executor;

    public FakedMessageListener(MessageListener realMessageListener, String messageStr, AppDatabase db) {
        this.messageListener = realMessageListener;
        this.executor = Executors.newSingleThreadScheduledExecutor();

        executor.execute(() -> {
            Message message = new Message(messageStr.getBytes(StandardCharsets.UTF_8));
            if (messageStr == null || messageStr.length() == 0) {
                System.out.println("No message");
            } else {
                this.messageListener.onFound(message);

                Scanner scanner = new Scanner(messageStr);
                //String line = "";
                String csvSplitBy = ",";

                int count = 0;
                String name = null;
                String photoId = null;
                String year;
                String quarter;
                String courseType;
                String courseNum;
                String text;
                List<Course> userCourses = db.coursesDao().getForPerson(1);
                List<String> userCourseText = new ArrayList<>();
                for(int i = 0; i < userCourses.size(); i++){
                    userCourseText.add(userCourses.get(i).text);
                }
                int personId = db.personWithCoursesDao().maxId() + 1;
                while (scanner.hasNextLine()) {
                    String[] array = scanner.nextLine().split(csvSplitBy);
                    if (count == 0) {
                        name = array[0];
                        count++;
                    } else if (count == 1) {
                        photoId = array[0];
                        count++;

                    } else {
                        year = array[0];
                        quarter = array[1];
                        courseType = array[2];
                        courseNum = array[3];
                        text = quarter + year + ' ' + courseType + ' ' + courseNum;
                        int courseId = db.coursesDao().maxId() + 1;

                        //TODO: Put size functionality for mocked bluetooth
                        Course c = new Course(courseId, personId, text, "");
                        if(userCourseText.contains(c.text)){
                            db.coursesDao().insert(c);
                        }
                    }
                }
                Person newPerson = new Person(personId, name, photoId);
                if (db.coursesDao().getForPerson(personId).size() != 0){
                    db.personWithCoursesDao().insert(newPerson);
                }
                scanner.close();

                this.messageListener.onLost(message);

            }
        });
    }
}
