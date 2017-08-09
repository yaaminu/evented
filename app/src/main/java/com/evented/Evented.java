package com.evented;

import android.app.Application;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.evented.ui.JobRunnerImpl;
import com.evented.utils.Config;
import com.evented.utils.TaskManager;
import com.evented.utils.TaskSerializer;

import io.realm.Realm;

/**
 * Created by yaaminu on 8/9/17.
 */

public class Evented extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        Config.init(this, "Evented", BuildConfig.VERSION_CODE, BuildConfig.DEBUG);
        JobManager manager = new JobManager(new Configuration.Builder(this)
                .jobSerializer(new TaskSerializer())
                .build());
        TaskManager.init(new JobRunnerImpl(manager));
    }
}
