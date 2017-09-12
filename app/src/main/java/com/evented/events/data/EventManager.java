package com.evented.events.data;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.telephony.SmsManager;

import com.evented.BuildConfig;
import com.evented.tickets.Ticket;
import com.evented.utils.FileUtils;
import com.evented.utils.GenericUtils;
import com.evented.utils.PLog;
import com.evented.utils.PhoneNumberNormaliser;
import com.evented.utils.TaskManager;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import net.glxn.qrgen.android.QRCode;
import net.glxn.qrgen.core.exception.QRGenerationException;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
import rx.exceptions.Exceptions;

/**
 * Created by yaaminu on 8/9/17.
 */

public class EventManager {
    private static final String TAG = "EventManager";

    private static int ticketNumber = 0;
    @NonNull
    private final UserManager usermanager;

    static final Map<String, Integer> verificationMap = new HashMap<>();

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
                            .put(Ticket.FIELD_TYPE, ticket.getType())
                            .put(Ticket.FIELD_ticketId, ticket.getTicketId())
                            .put(Ticket.FIELD_TICKET_COST, ticket.getTicketCost())
                            .put(Ticket.OWNER_NUMBER, ticket.getOwnerPhone())
                            .put(Ticket.FIELD_TICKET_NUMBER, ticket.getTicketNumber())
                            .put(Ticket.FIELD_DATE_PURCHASED, ticket.getDatePurchased())
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

    public Observable<Ticket> fromQrCodeText(final String eventId, final String ticketJson) {
        return Observable.create(new Observable.OnSubscribe<Ticket>() {
            @Override
            public void call(Subscriber<? super Ticket> subscriber) {
                subscriber.onStart();
                try {
                    JSONObject obj = new JSONObject(ticketJson);
                    final String ticketId = obj.getString(Ticket.FIELD_ticketId),
                            signature = obj.getString(Ticket.FIELD_TICKET_SIGNATURE),
                            ticketEventId = obj.getString(Ticket.FIELD_EVENT_ID);
                    // skip for development.
                    ///remember to remove this
                    if (!BuildConfig.DEBUG /*//TODO i mean this debug build check*/ && !ticketEventId.equals(eventId)) {
                        subscriber.onError(new Throwable("Ticket not for this event"));
                        return;
                    }
                    GenericUtils.ensureNotEmpty(ticketId, signature);
                    //in the real application, we should be able to lookup the ticket and find it. since the
                    //event organizer will sync with the server.
                    Realm realm = Realm.getDefaultInstance();
                    try {
                        realm.beginTransaction();
                        Ticket ticket = realm.where(Ticket.class)
                                .equalTo(Ticket.FIELD_ticketId, ticketId)
                                .findFirst();
                        if (ticket != null) {
                            ticket.setVerifications(ticket.getVerifications() + 1);
                        }
                        realm.commitTransaction();
                    } finally {
                        realm.close();
                    }
                    final Ticket ret = new Ticket(ticketId, eventId, "032233", obj.optString(Ticket.OWNER_NUMBER, "022222"), signature,
                            obj.getLong(Ticket.FIELD_DATE_PURCHASED), obj.getLong(Ticket.FIELD_TICKET_COST), obj.getInt(Ticket.FIELD_TICKET_NUMBER),
                            obj.getString(Ticket.FIELD_EVENT_NAME), obj.optString("type", "unknown"));
                    verificationMap.put(ticketId, verificationMap.get(ticketId) == null ? 1 : verificationMap.get(ticketId) + 1);
                    ret.setVerifications(verificationMap.get(ticketId));
                    subscriber.onNext(ret);
                    subscriber.onCompleted();

                  /*  realm = Realm.getDefaultInstance();
                    Ticket ticket = realm.where(Ticket.class)
                            .equalTo(Ticket.FIELD_ticketId, ticketId)
                            .findFirst();
                    if (ticket == null) {
                        subscriber.onError(new Exception("Ticket not found"));
                    } else {*/
                    // verify ticket
                    ///steps
                    //1. compare the two signatures.
                    //2. calculate the signature again using the same parameters used but
                    //   this time round with the ticket we have here.
                    //3. compare the the output signature to make sure they are the same.

//                    }
                } catch (JSONException e) {
                    PLog.e(TAG, e.getMessage(), e);
                    throw Exceptions.propagate(new Exception("Unable to decode QR Code"));
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
                event.setLikes(Math.abs(new SecureRandom().nextInt()) % 100);
                event.setLiked(true);
                Realm realm = Realm.getDefaultInstance();
                realm.beginTransaction();
                Event tmp = realm.copyFromRealm(realm.copyToRealmOrUpdate(event));
                realm.commitTransaction();
                realm.close();
                subscriber.onNext(tmp);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<String> verifyNumber(final String eventName, final String phoneNumber) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                // TODO: 8/10/17 send a real sms and store the number
                subscriber.onStart();

                String message = "You wanted to buy ticket for   " + eventName + ". Use code 12345 to verify your phone number";
                try {
                    SmsManager.getDefault()
                            .sendTextMessage(PhoneNumberNormaliser.toIEE(phoneNumber, "GH"),
                                    null, message, null, null);
                } catch (NumberParseException e) {
                    subscriber.onError(e);
                    return;
                } catch (SecurityException e) {

                }
                SystemClock.sleep(3000);
                subscriber.onNext(phoneNumber);
                subscriber.onCompleted();
            }
        });
    }

    public Observable<Ticket> bookTicket(final String eventId, final String eventName,
                                         final String billingPhoneNumber, final String buyForNumber,
                                         final long cost,
                                         final String verificationCode, final String ticketType) {
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
                                System.currentTimeMillis(), cost, ++ticketNumber, eventName, ticketType);
                        PLog.d(TAG, "ticket bought %s", ticket);
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(ticket);
                        Event event = realm.where(Event.class).equalTo(Event.FIELD_EVENT_ID, eventId)
                                .findFirst();
                        GenericUtils.ensureNotNull(event);
                        //noinspection ConstantConditions
                        event.setCurrentUserGoing(true);
                        event.setGoing(event.getGoing() + 1);
                        realm.commitTransaction();

                        String message = "Ticket purchase for  " + event.getName() + " successful. Follow this link to download the ticket " +
                                "https://ev.co/t/?tid=82719817283300381";
                        try {
                            SmsManager.getDefault()
                                    .sendTextMessage(PhoneNumberNormaliser.toIEE(buyForNumber, "GH"),
                                            null, message, null, null);
                        } catch (NumberParseException e) {
                            subscriber.onError(e);
                            return;
                        } catch (SecurityException e) {

                        }

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

    public void toggleLikedAsyn(final String eventId) {
        TaskManager.executeNow(new Runnable() {
            @Override
            public void run() {
                Realm realm = Realm.getDefaultInstance();
                try {
                    Event event = realm.where(Event.class)
                            .equalTo(Event.FIELD_EVENT_ID, eventId)
                            .findFirst();
                    GenericUtils.ensureNotNull(event);
                    final boolean liked = !event.isLiked();
                    realm.beginTransaction();
                    event.setLiked(liked);
                    event.setLikes(liked ? event.getLikes() + 1 : Math.max(0, event.getLikes() - 1));
                    realm.commitTransaction();
                } finally {
                    realm.close();
                }
            }
        }, true);
    }
}
