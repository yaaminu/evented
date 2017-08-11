package com.evented.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.tickets.Ticket;
import com.evented.tickets.TicketDetailsActivity;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by yaaminu on 8/11/17.
 */

public class MyEventListActivity extends AppCompatActivity implements SimpleEventListFragment.callbacks {

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
        List<Ticket> ticketsBought = realm.where(Ticket.class)
                .distinct(Ticket.FIELD_EVENT_ID);

        if (ticketsBought.size() > 0) {
            final RealmQuery<Event> eventsQuery = realm.where(Event.class);
            String[] eventIds = new String[ticketsBought.size()];
            for (int i = 0; i < ticketsBought.size(); i++) {
                eventIds[i] = ticketsBought.get(i).getEventId();
            }
            eventsQuery.in(Event.FIELD_EVENT_ID, eventIds);
            results = eventsQuery
                    .findAllSortedAsync(Event.FIELD_START_DATE);
            results.addChangeListener(listener);
        }
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
        items[0] = getString(R.string.view_ticket);
        items[1] = getString(R.string.view_event_details);
        items[2] = getString(R.string.set_a_reminder);
        new AlertDialog.Builder(this)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            viewTicket(event);
                        } else if (i == 1) {
                            viewEventDetails(event);
                        } else if (i == 2) {
                            Snackbar.make(getWindow().getDecorView(), "adding to calendar", Snackbar.LENGTH_LONG)
                                    .show();
                        }
                    }
                }).create().show();
    }

    private void viewTicket(Event event) {
        long count = realm.where(Ticket.class)
                .equalTo(Ticket.FIELD_EVENT_ID, event.getEventId())
                .count();
        if (count == 1) {
            Intent intent = new Intent(this, TicketDetailsActivity.class);
            intent.putExtra(TicketDetailsActivity.EXTRA_TICKET_ID, realm.where(Ticket.class).equalTo(Ticket.FIELD_EVENT_ID, event.getEventId())
                    .findFirst().getTicketId());
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, TicketsListActivity.class);
            intent.putExtra(TicketsListActivity.EXTRA_EVENT_ID, event.getEventId());
            intent.putExtra(TicketsListActivity.EXTRA_EVENT_TITLE, getString(R.string.tickets_list, event.getName()));
            startActivity(intent);
        }
    }

    private void viewEventDetails(Event event) {
        Intent intent = new Intent(this, EventDetailsActivity.class);
        intent.putExtra(EventDetailsActivity.EXTRA_EVENT_NAME, event.getName());
        intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, event.getEventId());
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
