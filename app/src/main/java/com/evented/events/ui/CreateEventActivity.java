package com.evented.events.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.evented.evented.R;
import com.evented.events.data.Event;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;

public class CreateEventActivity extends AppCompatActivity {

    int stage = 0;
    ProgressDialog dialog;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         event = new Event();
        setContentView(R.layout.activity_create_event);
        if (savedInstanceState != null) {
            stage = savedInstanceState.getInt("stage", 0);
        } else {
            stage = 0;
        }
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Creating event, Please wait");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("stage", stage);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        next();
    }

    private void next() {
        if (stage == 3) {
            createEvent();
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, getFragment(stage))
                    .commit();
        }
    }

    private void createEvent() {
        dialog.show();
        new Handler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content), event.toString(), Snackbar.LENGTH_LONG)
                                .show();
                    }
                }, TimeUnit.SECONDS.toMillis(30));
    }

    private Fragment getFragment(int stage) {
        switch (stage) {
            case 0:
                return new CreateEventFragment1();
            case 1:
                return new CreateEventFragment2();
            case 2:
                return new CreateEventFragment3();
            default:
                throw new AssertionError();
        }
    }

    void onNext() {
        stage++;
        next();
    }

    Event getEvent() {
        return event;
    }
}
