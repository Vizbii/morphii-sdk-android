package com.morphii.morphiisampleapp;

import android.app.Application;

import com.morphii.sdk.BasicViewConfiguration;

/**
 * Created by coryciepiela on 2/17/17.
 */

public class DefaultApplication extends Application {

    static BasicViewConfiguration mBasicViewConfiguration;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
