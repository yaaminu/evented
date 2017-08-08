package com.evented.events;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.evented.evented.R;

public class CreateEventActivity extends AppCompatActivity {

    int stage = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        if (savedInstanceState != null) {
            stage = savedInstanceState.getInt("stage", 0);
        } else {
            stage = 0;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        next();
    }

    void next() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, getFragment(stage))
                .commit();
    }

    private Fragment getFragment(int stage) {
        switch (stage) {
            case 0:
                return new CreateEventFragment1();
            case 1:
                break;
            case 2:
                break;
            default:
                throw new AssertionError();
        }
        return null;
    }

    void onNext() {
        stage++;
        next();
    }
}
