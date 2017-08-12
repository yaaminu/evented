package com.evented.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.evented.BuildConfig;

/**
 * Created by yaaminu on 8/12/17.
 */

public class LauncherActivity extends AppCompatActivity {

     static final String FLAVOR_ATTENDEES = "attendees";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.FLAVOR.equals(FLAVOR_ATTENDEES)) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, EventManagerActivity.class));
        }
        finish();
    }
}
