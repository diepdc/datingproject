package com.example.hoang.datingproject.Utilities;

import android.app.Application;

import com.firebase.client.Firebase;

/**
 * Created by hoang on 6/9/2016.
 */
public class DatingApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(getApplicationContext());
    }
}
