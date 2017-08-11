package com.evented.events.data;

import android.os.SystemClock;
import android.support.annotation.NonNull;

import com.evented.tickets.Ticket;
import com.evented.utils.FileUtils;
import com.evented.utils.GenericUtils;
import com.evented.utils.PLog;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.exception.QRGenerationException;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;

import static com.evented.tickets.Ticket.FIELD_DATE_PURCHASED;
import static com.evented.tickets.Ticket.FIELD_TICKET_NUMBER;

/**
 * Created by yaaminu on 8/9/17.
 */

public class EventManager {
    private static final String TAG = "EventManager";

    private static int ticketNumber = 0;
    @NonNull
    private final UserManager usermanager;

    private EventManager(@NonNull UserManager manager) {
        this.usermanager = manager;
    }

    public static EventManager create(@NonNull UserManager usermanager) {
        return new EventManager(usermanager);
    }

    public Observable<byte[]> qrCode(@NonNull final Ticket ticket) {
        return Observable.create(new Observable.OnSubscribe<byte[]>() {
            @Override
            public void call(Subscriber<? super byte[]> subscriber) {
                subscriber.onStart();
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(Ticket.FIELD_EVENT_ID, ticket.getEventId())
                            .put(Ticket.FIELD_EVENT_NAME, ticket.getEventName())
                            .put(Ticket.FIELD_ticketId, ticket.getTicketId())
                            .put(Ticket.FIELD_TICKET_COST, ticket.getTicketCost())
                            .put(FIELD_TICKET_NUMBER, ticket.getTicketNumber())
                            .put(FIELD_DATE_PURCHASED, ticket.getDatePurchased())
                            .put(Ticket.FIELD_TICKET_SIGNATURE, ticket.getTicketSignature());
                    PLog.d(TAG, "qrcode data: %s", jsonObject);
                    final byte[] qrCodeAsByteStream = QRCode.from(jsonObject.toString())
                            .withSize(512, 512)
                            .withHint(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M)
                            .stream().toByteArray();
                    subscriber.onNext(qrCodeAsByteStream);
                    subscriber.onCompleted();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (QRGenerationException e) {
                    // TODO: 8/11/17 handle error
                    subscriber.onError(e);
                    throw new AssertionError();
                }
            }
        });
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

    public Observable<Ticket> bookTicket(final String eventId, final String eventName, final String billingPhoneNumber, final String buyForNumber,
                                         final long cost,
                                         final String verificationCode) {
        return rx.Observable.create(new Observable.OnSubscribe<Ticket>() {
            @Override
            public void call(Subscriber<? super Ticket> subscriber) {
                subscriber.onStart();
                SystemClock.sleep(3000);
                if ("12345".equals(verificationCode)) {
                    Realm realm = Realm.getDefaultInstance();
                    try {
                        final Ticket ticket = new Ticket(System.currentTimeMillis() + "", eventId, billingPhoneNumber, buyForNumber,
                                FileUtils.sha1(System.currentTimeMillis() + ""),
                                System.currentTimeMillis(), cost, ++ticketNumber, eventName);
                        PLog.d(TAG, "ticket bought %s", ticket);
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(ticket);
                        realm.commitTransaction();
                        subscriber.onNext(ticket);
                        subscriber.onCompleted();
                    } finally {
                        realm.close();
                    }
                } else {
                    subscriber.onError(new Exception("Verification code invalid"));
                }
            }
        });
    }
}
