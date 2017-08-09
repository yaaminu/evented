package com.evented.events.data;

import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.evented.utils.GenericUtils;

import rx.Subscriber;

/**
 * Created by yaaminu on 8/9/17.
 */

public class EventManager {

    @NonNull
    private final UserManager usermanager;

    private EventManager(@NonNull UserManager manager) {
        this.usermanager = manager;
    }

    public static EventManager create(@NonNull UserManager usermanager) {
        return new EventManager(usermanager);
    }

    public rx.Observable<Event> createEvent(final Event event) {
        return rx.Observable.create(new rx.Observable.OnSubscribe<Event>() {
            @Override
            public void call(Subscriber<? super Event> subscriber) {
                subscriber.onStart();
                SystemClock.sleep(5000);
                GenericUtils.assertThat(usermanager.getCurrentUser() != null, "no user logged in");
                // TODO: 8/9/17 validate event
                event.setCreatedBy(usermanager.getCurrentUser().userId);
                event.setEventId("ldjafoiafalkf");
                final long dateCreated = System.currentTimeMillis();
                event.setDateCreated(dateCreated);
                event.setDateUpdated(dateCreated);
                subscriber.onNext(event);
                subscriber.onCompleted();
            }
        });
    }
}
