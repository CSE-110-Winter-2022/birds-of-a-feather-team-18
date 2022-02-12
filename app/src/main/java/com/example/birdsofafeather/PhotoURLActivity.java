package com.example.birdsofafeather;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Person;
import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotoURLActivity extends AppCompatActivity {
    protected AppDatabase db;
    String photoUrl;
    //private ExecutorService backgroundThreadExecutor = Executors.newSingleThreadExecutor();
    //private Future<Void> future;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_url);
        setTitle(R.string.app_title);
        //default profile photo
        photoUrl = "https://st3.depositphotos.com/4111759/13425/v/600/depositphotos_134255532-stock-illustration-profile-placeholder-male-default-profile.jpg";
        //db = AppDatabase.singleton(this);

        //StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        //StrictMode.setThreadPolicy(policy);
    }

    public void onSubmitClicked(View view) {
        TextView photoUrlTextView = findViewById(R.id.photo_url_textview);
        photoUrl = photoUrlTextView.getText().toString();
        ImageView imageView = findViewById(R.id.testImageView);

        //TODO: add photo to database (as field in Person)

        Picasso.get().load(photoUrl).into(imageView);

        //Intent intent = new Intent(this, PrevCourseActivity.class);
        //startActivity(intent);
        //finish();
    }

}

