package com.example.birdsofafeather;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.example.birdsofafeather.model.db.Person;

public class LoginActivity extends AppCompatActivity {
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

        Person newSelf = new Person(1, selfName);
        db.personWithCoursesDao().insert(newSelf);
        finish();
    }
}
