package me.rorschach.schoolcontacts;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by lei on 16-4-10.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
        JodaTimeAndroid.init(this);
    }
}
