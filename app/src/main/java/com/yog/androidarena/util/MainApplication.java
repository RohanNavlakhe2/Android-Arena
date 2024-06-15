package com.yog.androidarena.util;

import android.app.Application;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.yog.androidarena.BuildConfig;

import timber.log.Timber;

public class MainApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Timber.d("Ads Sdk Initialization completed");
            }

        });

       /* new Instabug.Builder(this, "f30e8db626e761f6e612486e4acf0ce4")
                .setInvocationEvents(
                        InstabugInvocationEvent.SHAKE,
                        InstabugInvocationEvent.FLOATING_BUTTON)
                .build();*/

    }






}
