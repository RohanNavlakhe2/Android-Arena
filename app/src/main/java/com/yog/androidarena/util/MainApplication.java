package com.yog.androidarena.util;

import android.app.Application;

import com.yog.androidarena.BuildConfig;

import timber.log.Timber;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }

       /* new Instabug.Builder(this, "f30e8db626e761f6e612486e4acf0ce4")
                .setInvocationEvents(
                        InstabugInvocationEvent.SHAKE,
                        InstabugInvocationEvent.FLOATING_BUTTON)
                .build();*/

    }




}
