package com.evented.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.events.data.EventBuilder;
import com.evented.events.data.User;
import com.evented.events.data.UserManager;
import com.evented.events.ui.CreateEventActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by yaaminu on 8/7/17.
 */

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.fab_create_event)
    View fab;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.fab_create_event)
    void onClick() {
        Intent intent = new Intent(this, CreateEventActivity.class);
        startActivity(intent);
    }


    static void createEvents() {
        new Thread() {
            @Override
            public void run() {
                final User currentUser = UserManager.getInstance().getCurrentUser();
                Realm realm = Realm.getDefaultInstance();
                createToday(currentUser, realm);
                realm.close();
            }

            private void createToday(User currentUser, Realm realm) {
                List<Event> events = new ArrayList<>(20);
                for (int i = 0; i < 20; i++) {
                    events.add(new EventBuilder()
                            .setCreatedBy(currentUser.userId)
                            .setDateCreated(System.currentTimeMillis())
                            .setDateUpdated(System.currentTimeMillis())
                            .setStartDate(System.currentTimeMillis())
                            .setEndDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(4))
                            .setEventId("eventId" + i)
                            .setDescription("description of event")
                            .setMaxSeats(1000)
                            .setPublicity(Event.PUBLICITY_PUBLIC)
                            .setEntranceFee(10000)
                            .setName("Event name " + i)
                            .setVenue("Event venue" + i)
                            .createEvent());
                }
                realm.copyToRealmOrUpdate(events);
            }

            private void createTomorrow(Realm realm, User currentUser) {
                List<Event> events = new ArrayList<>(20);
                for (int i = 0; i < 20; i++) {
                    events.add(new EventBuilder()
                            .setCreatedBy(currentUser.userId)
                            .setDateCreated(System.currentTimeMillis())
                            .setDateUpdated(System.currentTimeMillis())
                            .setStartDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1))
                            .setEndDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(4))
                            .setEventId("eventId" + i)
                            .setDescription("description of event")
                            .setMaxSeats(1000)
                            .setPublicity(Event.PUBLICITY_PUBLIC)
                            .setEntranceFee(10000)
                            .setName("Event name " + i)
                            .setVenue("Event venue" + i)
                            .createEvent());
                }
                realm.copyToRealmOrUpdate(events);
            }

            private void createEventNextWeek(Realm realm, User currentUser) {
                List<Event> events = new ArrayList<>(20);
                for (int i = 0; i < 20; i++) {
                    events.add(new EventBuilder()
                            .setCreatedBy(currentUser.userId)
                            .setDateCreated(System.currentTimeMillis())
                            .setDateUpdated(System.currentTimeMillis())
                            .setStartDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3))
                            .setEndDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(11))
                            .setEventId("eventId" + i)
                            .setDescription("description of event")
                            .setMaxSeats(1000)
                            .setPublicity(Event.PUBLICITY_PUBLIC)
                            .setEntranceFee(10000)
                            .setName("Event name " + i)
                            .setVenue("Event venue" + i)
                            .createEvent());
                }
                realm.copyToRealmOrUpdate(events);
            }

            private void createEventNextMonth(Realm realm, User currentUser) {
                List<Event> events = new ArrayList<>(20);
                for (int i = 0; i < 20; i++) {
                    events.add(new EventBuilder()
                            .setCreatedBy(currentUser.userId)
                            .setDateCreated(System.currentTimeMillis())
                            .setDateUpdated(System.currentTimeMillis())
                            .setStartDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(33))
                            .setEndDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(44))
                            .setEventId("eventId" + i)
                            .setDescription("description of event")
                            .setMaxSeats(1000)
                            .setPublicity(Event.PUBLICITY_PUBLIC)
                            .setEntranceFee(10000)
                            .setName("Event name " + i)
                            .setVenue("Event venue" + i)
                            .createEvent());
                }
                realm.copyToRealmOrUpdate(events);
            }
        }.start();
    }
}
