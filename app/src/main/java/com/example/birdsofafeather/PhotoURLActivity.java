package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Person;
import com.example.birdsofafeather.model.db.PersonWithCourses;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PhotoURLActivity extends AppCompatActivity {
    protected AppDatabase db;
    String photoUrl;
    AlertDialog alertDialog = null;
    //ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_url);
        setTitle(R.string.app_title);
        //default profile photo
        db = AppDatabase.singleton(this);
        //imageView = findViewById(R.id.testImageView);
        //Picasso.get().load(db.personWithCoursesDao().get(1).person.photo).into(imageView);
    }

    public void onSubmitClicked(View view) throws IOException {
        TextView photoUrlTextView = findViewById(R.id.photo_url_textview);

        if(!photoUrlTextView.getText().toString().equals("")) {
            //imageView = findViewById(R.id.testImageView);

            try {
                URL url = new URL(photoUrlTextView.getText().toString());
                //HttpURLConnection huc = (HttpURLConnection) url.openConnection();

                photoUrl = photoUrlTextView.getText().toString();
                db.personWithCoursesDao().updatePhoto(photoUrl, 1);
               // Picasso.get().load(db.personWithCoursesDao().get(1).person.photo).into(imageView);
                Intent intent = new Intent(this, PrevCourseActivity.class);
                startActivity(intent);
                finish();
            } catch (MalformedURLException e){
                alertDialog = Utilities.showAlert(this, "Invalid Photo URL");
            }

        } else {
            Intent intent = new Intent(this, PrevCourseActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public AlertDialog getDialog() {
        return alertDialog;
    }
}

