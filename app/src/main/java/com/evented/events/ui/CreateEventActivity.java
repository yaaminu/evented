package com.evented.events.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.events.data.EventManager;
import com.evented.events.data.UserManager;
import com.evented.utils.PLog;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class CreateEventActivity extends AppCompatActivity {

    private static final String TAG = "CreateEventActivity";
    private static final String KEY_EVENT = "event";
    private static final String KEY_STAGE = "stage";
    private final CreateEventFragment1 createEventFragment1 = new CreateEventFragment1();
    private final CreateEventFragment2 createEventFragment2 = new CreateEventFragment2();
    private final CreateEventFragment3 createEventFragment3 = new CreateEventFragment3();
    private final CreateEventFragment3 createEventFragment31 = new CreateEventFragment3();

    private int stage = 0;
    private ProgressDialog dialog;
    private Event event;

    private EventManager eventManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventManager = EventManager.create(UserManager.getInstance());

        setContentView(R.layout.activity_create_event);
        if (savedInstanceState != null) {
            stage = savedInstanceState.getInt(KEY_STAGE, 0);
            event = savedInstanceState.getParcelable(KEY_EVENT);
        } else {
            event = new Event();
            stage = 0;
        }

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Creating event, Please wait");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(KEY_STAGE, stage);
        outState.putParcelable(KEY_EVENT, event);
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
        eventManager.createEvent(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Event>() {
                    @Override
                    public void call(Event event) {
                        dialog.dismiss();
                        PLog.d(TAG, "event: %s", event);
                        Snackbar.make(getWindow().getDecorView(), event.toString(), Snackbar.LENGTH_LONG)
                                .show();
                        finish();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        dialog.dismiss();
                        Snackbar.make(getWindow().getDecorView(), throwable.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private Fragment getFragment(int stage) {
        switch (stage) {
            case 0:
                return createEventFragment1;
            case 1:
                return createEventFragment2;
            case 2:
                return createEventFragment3;
            default:
                return createEventFragment31;
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
