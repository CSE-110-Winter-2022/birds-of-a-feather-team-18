package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Person;
import com.example.birdsofafeather.model.db.Session;

public class NameLoginActivity extends AppCompatActivity {
    protected AppDatabase db;
    public AlertDialog alertDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.app_title);
        db = AppDatabase.singleton(this);

        //create a favorites session
        if(db.sessionsDao().count() == 0){
            Session favoritesSession = new Session("favoritesSession", "Favorites");
            db.sessionsDao().insert(favoritesSession);
        }
    }

    public void onConfirmClicked(View view){
        TextView selfNameTextView = findViewById(R.id.self_name_textview);
        String selfName = selfNameTextView.getText().toString();

        //display an error if no name is entered
        if(selfName.equals("")){
            alertDialog = Utilities.showAlert(this, "No Name Entered");
        } else {
            //create a person and insert it into the database with an id of 1 and with the default photo
            Person newSelf = new Person( "1", selfName, "https://st3.depositphotos.com/4111759/13425/v/600/depositphotos_134255532-stock-illustration-profile-placeholder-male-default-profile.jpg", false);
            db.personWithCoursesDao().insert(newSelf);

            //start PhotoURLActivity
            Intent intent = new Intent(this, PhotoURLActivity.class);
            startActivity(intent);

            finish();
        }
    }

    public AlertDialog getDialog() {
        return alertDialog;
    }
}
