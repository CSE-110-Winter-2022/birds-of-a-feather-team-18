package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PhotoURLActivity extends AppCompatActivity {
    protected AppDatabase db;
    String photoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_url);
        setTitle(R.string.app_title);
        //default profile photo
        photoUrl = "https://st3.depositphotos.com/4111759/13425/v/600/depositphotos_134255532-stock-illustration-profile-placeholder-male-default-profile.jpg";
        //db = AppDatabase.singleton(this);
    }

    public void onSubmitClicked(View view) throws IOException {
        TextView photoUrlTextView = findViewById(R.id.photo_url_textview);

        if(!photoUrlTextView.getText().toString().equals("")) {
            ImageView imageView = findViewById(R.id.testImageView);

            //TODO: add photo to database (as field in Person)


            try {
                URL url = new URL(photoUrlTextView.getText().toString());
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();

                int responseCode = huc.getResponseCode();

                if(responseCode==HttpURLConnection.HTTP_NOT_FOUND){
                    Utilities.showAlert(this, "Invalid Photo URL");
                } else {
                    photoUrl = photoUrlTextView.getText().toString();
                    Picasso.get().load(photoUrl).into(imageView);
                    Intent intent = new Intent(this, PrevCourseActivity.class);
                    startActivity(intent);
                    finish();
                }
            } catch (MalformedURLException e){
                Utilities.showAlert(this, "Invalid Photo URL");
            }

        } else {
            Intent intent = new Intent(this, PrevCourseActivity.class);
            startActivity(intent);
            finish();
        }
    }

}

