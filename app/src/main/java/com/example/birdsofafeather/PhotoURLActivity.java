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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_url);
        setTitle(R.string.app_title);
        db = AppDatabase.singleton(this);
    }

    public void onSubmitClicked(View view) throws IOException {
        TextView photoUrlTextView = findViewById(R.id.photo_url_textview);

        if(!photoUrlTextView.getText().toString().equals("")) {
            //imageView = findViewById(R.id.testImageView);

            try {
                //create URL to check if the entered URL is valid
                URL url = new URL(photoUrlTextView.getText().toString());

                //update the user's photo with the new URL if it is valid
                photoUrl = photoUrlTextView.getText().toString();
                db.personWithCoursesDao().updatePhoto(photoUrl, 1);

                //start PrevCourseActivity
                Intent intent = new Intent(this, PrevCourseActivity.class);
                startActivity(intent);
                finish();
            } catch (MalformedURLException e){
                //show error message if the photo URL is invalid
                alertDialog = Utilities.showAlert(this, "Invalid Photo URL");
            }

        } else {
            //if no URL is entered, do not change anything and move on to PrevCourseActivity
            Intent intent = new Intent(this, PrevCourseActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public AlertDialog getDialog() {
        return alertDialog;
    }
}

