package com.evented.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.evented.R;
import com.evented.events.ui.EventDetailsFragment;

public class EventDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_EVENT_ID = "eventId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        android.support.v4.app.Fragment fragment = new EventDetailsFragment();
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }


}
