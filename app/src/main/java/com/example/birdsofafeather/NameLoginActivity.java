package com.example.birdsofafeather;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Person;

public class NameLoginActivity extends AppCompatActivity {
    protected AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(R.string.app_title);
        db = AppDatabase.singleton(this);
    }

    public void onConfirmClicked(View view){
        TextView selfNameTextView = findViewById(R.id.self_name_textview);
        String selfName = selfNameTextView.getText().toString();

        if(selfName.equals("")){
            Utilities.showAlert(this, "No Name Entered");
        } else {
            Person newSelf = new Person(db.personWithCoursesDao().maxId() + 1, selfName);
            db.personWithCoursesDao().insert(newSelf);

            Intent intent = new Intent(this, PhotoURLActivity.class);
            startActivity(intent);

            finish();
        }
    }
}
