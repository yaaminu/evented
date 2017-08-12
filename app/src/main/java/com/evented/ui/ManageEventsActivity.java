package com.evented.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.evented.R;
import com.evented.events.data.Event;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by yaaminu on 8/12/17.
 */

public class ManageEventsActivity extends AppCompatActivity implements SimpleEventListFragment.callbacks {


    private SimpleEventListFragment fragment;

    Realm realm;

    @Nullable
    RealmResults<Event> results;
    private RealmChangeListener<RealmResults<Event>> listener = new RealmChangeListener<RealmResults<Event>>() {
        @Override
        public void onChange(RealmResults<Event> events) {
            fragment.updateResults(results);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        setContentView(R.layout.activity_event_list);
        fragment = new SimpleEventListFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment, "tag")
                .commit();
        final RealmQuery<Event> eventsQuery = realm.where(Event.class);
        results = eventsQuery
                .findAllSortedAsync(Event.FIELD_START_DATE);
        results.addChangeListener(listener);

        getSupportActionBar()
                .setDisplayHomeAsUpEnabled(true);
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
        String[] items = new String[3];
        items[0] = getString(R.string.verify_tickets);
        items[1] = getString(R.string.update_event);
        items[2] = getString(R.string.view_tickets);
        new AlertDialog.Builder(this)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Intent intent = new Intent(ManageEventsActivity.this, VerifyTicketActivity.class);
                            intent.putExtra(VerifyTicketActivity.EXTRA_EVENT_ID, event.getEventId());
                            startActivity(intent);
                        } else if (i == 2) {
                            Intent intent = new Intent(ManageEventsActivity.this, TicketsListActivity.class);
                            intent.putExtra(TicketsListActivity.EXTRA_EVENT_ID, event.getEventId());
                            intent.putExtra(TicketsListActivity.EXTRA_EVENT_TITLE, getString(R.string.tickets_list, event.getName()));
                            startActivity(intent);
                        }
                    }
                }).create().show();
    }

}
