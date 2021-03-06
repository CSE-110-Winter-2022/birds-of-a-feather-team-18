package com.example.birdsofafeather;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.birdsofafeather.model.db.AppDatabase;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;



public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Bofs-Nearby";
    private MessageListener messageListener;
    private AppDatabase db;
    private final int adCode = 621;
    private final int scanCode = 345;
    private final int connectCode = 123784;
    private Message message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_title);

        db = AppDatabase.singleton(this);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(MainActivity.this, "BLUETOOTH_SCAN Permission granted!", Toast.LENGTH_SHORT).show();
        else
            requestScanPermission();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(MainActivity.this, "BLUETOOTH_ADVERTISE Permission granted!", Toast.LENGTH_SHORT).show();
        else
            requestAdPermission();

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED)
            Toast.makeText(MainActivity.this, "BLUETOOTH_CONNECT Permission granted!", Toast.LENGTH_SHORT).show();
        else
            requestConnectPermission();

        //if the database is empty, start the login activity
        if (db.personWithCoursesDao().count() == 0) {
            Intent intent = new Intent(this, NameLoginActivity.class);
            startActivity(intent);
        }

        // Instantiate message listener

        MessageListener realListener = new MessageListener() {};

        TextView profile = findViewById(R.id.student_profile);
        String test = profile.getText().toString();
        AppDatabase db = AppDatabase.singleton(this);
        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        //When app starts, no session is active
        editor.putString("currSession", "none");
        editor.apply();
        String currSessionID = preferences.getString("currSession", "");
        this.messageListener = new FakedMessageListener(realListener, test, db, currSessionID);
        message = new Message("Hello".getBytes());
    }

    //check if search service is on or off
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            //if service is on, return true
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void onTestClicked(View view) {
        // Start personListActivity
        Intent intent = new Intent(this, PersonListActivity.class);
        startActivity(intent);
    }

    public void onMockClicked(View view) {
        // Get CSV file
        TextView profile = findViewById(R.id.student_profile);
        String test = profile.getText().toString();

        //if the search is on, send the message to database
        if (isMyServiceRunning(SearchService.class)) {
            MessageListener realListener = new MessageListener() {

                // Log to show that message is received
                @Override
                public void onFound(@NonNull Message message) {
                    Log.d(TAG, "Found Profile: " + new String(message.getContent()));
                }

                // Log to show that profile from CSV saved to database
                @Override
                public void onLost(@NonNull Message message) {
                    Log.d(TAG, "Profile loaded in DataBase " + new String(message.getContent()));

                    // Log to get number of courses and number of common courses
                    Log.d(TAG, "Number of classmates: " + new String(String.valueOf(db.personWithCoursesDao().count() - 1)));
                    //Log.d(TAG, "Number of common courses: " + new String(String.valueOf(db.coursesDao().getForPerson(db.personWithCoursesDao().maxId()).size())));
                }
            };

            AppDatabase db = AppDatabase.singleton(this);
            SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
            String currSessionID = preferences.getString("currSession", "");
            //Use messageListener to save profile in database
            this.messageListener = new FakedMessageListener(realListener, test, db, currSessionID);
        } else {
            //if service is not on, will show a toast message
            Toast.makeText(this,"Service is not on", Toast.LENGTH_SHORT).show();
        }
        profile.setText("");

        if (isMyServiceRunning(SearchService.class)){

            Log.d(TAG, "Mock Clicked, service on: should work");
        }
        else{

            Log.d(TAG, "Mock Clicked, service off: shouldn't work");
        }
    }

    private void requestScanPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_SCAN)) {
            new AlertDialog.Builder(this).setTitle("BLUETOOTH_SCAN Permission needed").setMessage("This permission is needed to search for other BoFs.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.BLUETOOTH_SCAN}, scanCode);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.BLUETOOTH_SCAN}, scanCode);
        }
    }

    private void requestAdPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_ADVERTISE)) {
            new AlertDialog.Builder(this).setTitle("BLUETOOTH_CONNECT Permission needed").setMessage("This permission is needed to search for other BoFs.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.BLUETOOTH_ADVERTISE}, adCode);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.BLUETOOTH_ADVERTISE}, adCode);
        }
    }

    private void requestConnectPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_CONNECT)) {
            new AlertDialog.Builder(this).setTitle("BLUETOOTH_CONNECT Permission needed").setMessage("This permission is needed to search for other BoFs.")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.BLUETOOTH_CONNECT}, connectCode);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).create().show();
        }
        else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.BLUETOOTH_CONNECT}, connectCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == scanCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "BLUETOOTH_SCAN Permission is granted", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == adCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "BLUETOOTH_ADVERTISE Permission is granted", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == connectCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "BLUETOOTH_CONNECT Permission is granted", Toast.LENGTH_SHORT).show();
        }
    }



    // Starting bluetooth
    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        Nearby.getMessagesClient(this).publish(message);
        Nearby.getMessagesClient(this).subscribe(messageListener);
    }

    // Stop bluetooth
    @Override
    protected void onStop() {
        super.onStop();
        Nearby.getMessagesClient(this).unpublish(message);
        Nearby.getMessagesClient(this).unsubscribe(messageListener);
    }
}
