package com.evented.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.evented.R;
import com.evented.events.data.Event;

/**
 * Created by yaaminu on 9/6/17.
 */

public class FavoriteEventsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_events);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, EventsListFragment.create(-1, Event.CATEGORY_ALL, true))
                .commit();

    }
}
