package com.lagotronicsprojects.birdslight;

import android.app.Application;

import io.realm.Realm;

public class BirdsLightApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
