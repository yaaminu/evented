package com.evented.events.data;

import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.evented.tickets.Ticket;
import com.evented.utils.FileUtils;
import com.evented.utils.GenericUtils;
import com.evented.utils.PLog;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by yaaminu on 8/9/17.
 */

public class EventManager {
    private static final String TAG = "EventManager";

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

    public Observable<String> verifyNumber(final String phoneNumber) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                // TODO: 8/10/17 send a real sms and store the number
                subscriber.onStart();
                SystemClock.sleep(3000);
                subscriber.onNext(phoneNumber);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Ticket> bookTicket(final String eventId, final String billingPhoneNumber, final String buyForNumber, final long cost,
                                         final String verificationCode) {
        return rx.Observable.create(new Observable.OnSubscribe<Ticket>() {
            @Override
            public void call(Subscriber<? super Ticket> subscriber) {
                subscriber.onStart();
                SystemClock.sleep(3000);
                if ("12345".equals(verificationCode)) {
                    final Ticket ticket = new Ticket(System.currentTimeMillis() + "", eventId, billingPhoneNumber, buyForNumber,
                            FileUtils.sha1("signature"), System.currentTimeMillis(), cost);
                    PLog.d(TAG, "ticket bought %s", ticket);
                    subscriber.onNext(ticket);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new Exception("Verification code invalid"));
                }
            }
        });
    }
}
