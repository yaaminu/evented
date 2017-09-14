package com.evented.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.events.ui.CreateEventActivity;
import com.evented.utils.Config;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by yaaminu on 8/12/17.
 */

public class EventManagerActivity extends AppCompatActivity implements SimpleEventListFragment.callbacks {

    Realm realm;

    @Nullable
    RealmResults<Event> results;
    private RealmChangeListener<RealmResults<Event>> listener = new RealmChangeListener<RealmResults<Event>>() {
        @Override
        public void onChange(RealmResults<Event> events) {
            fragment.updateResults(results);
        }
    };
    private SimpleEventListFragment fragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        setContentView(R.layout.activity_event_manager);
        ButterKnife.bind(this);
        fragment = new SimpleEventListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, "manageEvents")
                .commit();

        final RealmQuery<Event> eventsQuery = realm.where(Event.class);
        results = eventsQuery
                .findAllSortedAsync(Event.FIELD_START_DATE);
        results.addChangeListener(listener);
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onResume() {
        if (results != null) {
            results.addChangeListener(listener);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (results != null) {
            realm.removeAllChangeListeners();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    @Override
    public void onItemClicked(final Event event) {
        final SimpleBottomSheetDialogFragment dialogFragment = new SimpleBottomSheetDialogFragment();
        Bundle args = new Bundle(2);
        args.putString(SimpleBottomSheetDialogFragment.EXTRA_EVENT_ID, event.getEventId());
        args.putString(SimpleBottomSheetDialogFragment.EXTRA_EVENT_NAME, event.getName());
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    @OnClick(R.id.fab_create_event)
    void onClick() {
        startActivity(new Intent(this, CreateEventActivity.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().toString().startsWith("switch")) {
            Config.setManagement(false);
            startActivity(new Intent(this, LauncherActivity.class));
            finish();
        } else {
            TestDataGenerator.createEvents();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("switch to management");
        menu.add("Seed Test Data");
        return super.onCreateOptionsMenu(menu);
    }
}
