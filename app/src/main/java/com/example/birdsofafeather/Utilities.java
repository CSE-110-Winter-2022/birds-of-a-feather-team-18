package com.example.birdsofafeather;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import java.util.Optional;

public class Utilities {
    //use to show an error message
    public static AlertDialog showAlert(Context context, String message) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        alertBuilder
                .setTitle("Error!")
                .setMessage(message)
                .setPositiveButton("Ok", (dialog, id) -> {
                    dialog.cancel();
                })
                .setCancelable(true);

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
        return alertDialog;
    }
}
