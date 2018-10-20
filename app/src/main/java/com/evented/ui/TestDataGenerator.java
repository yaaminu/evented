package com.evented.ui;

import android.graphics.Bitmap;

import com.evented.R;
import com.evented.events.data.BillingAcount;
import com.evented.events.data.Event;
import com.evented.events.data.EventBuilder;
import com.evented.events.data.EventManager;
import com.evented.events.data.TicketType;
import com.evented.events.data.User;
import com.evented.events.data.UserManager;
import com.evented.events.data.Venue;
import com.evented.utils.Config;
import com.evented.utils.GenericUtils;
import com.evented.utils.PLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmList;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static android.graphics.BitmapFactory.decodeResource;
import static org.apache.commons.io.IOUtils.closeQuietly;

/**
 * Created by yaaminu on 9/12/17.
 */

public class TestDataGenerator {
    private static final String TAG = "TestDataGenerator";

    private static final Semaphore lock = new Semaphore(1, false);

    static void createEvents() {
        if (!UserManager.getInstance().isCurrentUserVerified()) {
            return;
        }
        final String description = "This is the description of the event. It must give precise information of what " +
                "the event is all about and all other valuable information";
        final RealmList<TicketType> ticketTypes = new RealmList<>();

        new Thread() {

            private final Venue address = new Venue("Silverbird Cinemas Accra", "address", -0.17353469999999996, 5.6218986);
            private File flyer;

            @Override
            public void run() {

                ticketTypes.add(new TicketType("Singe", 1000, 200));
                ticketTypes.add(new TicketType("Couple", 1700, 120));
                Realm realm = Realm.getDefaultInstance();
                try {
                    lock.acquireUninterruptibly();
                    if (realm.where(Event.class)
                            .count() > 5) {
                        return;
                    }
                    File dir = Config.getApplicationContext().getExternalFilesDir("flyers");
                    GenericUtils.assertThat(null != dir);
                    if (!dir.isDirectory() && !dir.mkdirs()) {
                        throw new AssertionError();
                    }
                    Bitmap bitmap = decodeResource(Config.getApplicationContext()
                            .getResources(), R.drawable.flyer2);
                    flyer = new File(dir, System.currentTimeMillis() + "flyer.jpeg");
                    FileOutputStream out = new FileOutputStream(flyer);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    closeQuietly(out);
                    User currentUser = UserManager.getInstance().getCurrentUser();

                    createToday(currentUser, realm);
                    createTomorrow(realm, currentUser);
                    createEventNextWeek(realm, currentUser);
                    createEventNextMonth(realm, currentUser);
                    createEventAfterNextMonth(realm, currentUser);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    realm.close();
                    lock.release();
                }
            }

            private void createToday(User currentUser, Realm realm) {
                List<Event> events = new ArrayList<>(3);
                BillingAcount billingAcount =
                        new BillingAcount("yaaminu",
                                currentUser.phoneNumber, "MTN");


                for (int i = 0; i < 3; i++) {
                    events.add(new EventBuilder()
                            .setBillingAcount(billingAcount)
                            .setCategory(Math.max(1, i % 9))
                            .setCreatedBy(currentUser.userId)
                            .setDateCreated(System.currentTimeMillis())
                            .setFlyers(flyer.getAbsolutePath())
                            .setDateUpdated(System.currentTimeMillis())
                            .setStartDate(System.currentTimeMillis())
                            .setEndDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(4))
                            .setDescription(description)
                            .setLikes(Math.abs(new SecureRandom().nextInt()) % 100)
                            .setGoing(Math.abs(new SecureRandom().nextInt()) % 100)
                            .setPublicity(Event.PUBLICITY_PUBLIC)
                            .setName("Event name " + i)
                            .setVenue(new Venue("Silverbird Cinemas Accra", "address",
                                    -0.17353469999999996 + new SecureRandom().nextDouble() % 0.1,
                                    5.6218986 + new SecureRandom().nextDouble() % 0.1))
                            .createEvent());
                    events.get(i).setLiked(i % 5 == 0);
                    events.get(i).setCurrentUserGoing(i % 2 == 0);
                    events.get(i).setWebLink("https://facebook.com");
                    events.get(i).setOrganizerContact("0266349205");
                    events.get(i).setTicketTypes(ticketTypes);
                    EventManager.create()
                            .createEvent(events.get(i))
                            .subscribeOn(Schedulers.immediate())
                            .subscribe(onSuccess, onError);
                }
            }

            private void createTomorrow(Realm realm, User currentUser) {
                BillingAcount billingAcount =
                        new BillingAcount("username",
                                currentUser.phoneNumber, "MTN");
                List<Event> events = new ArrayList<>(3);
                for (int i = 0; i < 3; i++) {
                    events.add(new EventBuilder()
                            .setCreatedBy(currentUser.userId)
                            .setCategory(Math.max(1, i % 9))
                            .setFlyers(flyer.getAbsolutePath())
                            .setBillingAcount(billingAcount)
                            .setDateCreated(System.currentTimeMillis())
                            .setDateUpdated(System.currentTimeMillis())
                            .setStartDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1))
                            .setEndDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(4))
                            .setDescription(description)
                            .setGoing(Math.abs(new SecureRandom().nextInt()) % 100)
                            .setLikes(Math.abs(new SecureRandom().nextInt()) % 100)
                            .setPublicity(Event.PUBLICITY_PUBLIC)
                            .setName("Event name " + i)
                            .setGoing(Math.abs(new SecureRandom().nextInt()) % 100)
                            .setVenue(address)
                            .createEvent());
                    events.get(i).setLiked(i % 5 == 0);
                    events.get(i).setCurrentUserGoing(i % 2 == 0);
                    events.get(i).setTicketTypes(ticketTypes);
                    events.get(i).setWebLink("https://facebook.com");
                    events.get(i).setOrganizerContact("0266349205");
                    EventManager.create()
                            .createEvent(events.get(i))
                            .subscribeOn(Schedulers.immediate())
                            .subscribe(onSuccess, onError);
                }
            }

            private void createEventNextWeek(Realm realm, User currentUser) {
                List<Event> events = new ArrayList<>(3);
                BillingAcount billingAcount =
                        new BillingAcount("username",
                                currentUser.phoneNumber, "MTN");
                for (int i = 0; i < 3; i++) {
                    events.add(new EventBuilder()
                            .setCreatedBy(currentUser.userId)
                            .setFlyers(flyer.getAbsolutePath())
                            .setDateCreated(System.currentTimeMillis())
                            .setDateUpdated(System.currentTimeMillis())
                            .setStartDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3))
                            .setEndDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(11))
                            .setCategory(Math.max(1, i % 9))
                            .setDescription(description)
                            .setBillingAcount(billingAcount)
                            .setPublicity(Event.PUBLICITY_PUBLIC)
                            .setGoing(Math.abs(new SecureRandom().nextInt()) % 100)
                            .setLikes(Math.abs(new SecureRandom().nextInt()) % 100)
                            .setName("Event name " + i)
                            .setVenue(address)
                            .createEvent());
                    events.get(i).setLiked(i % 5 == 0);
                    events.get(i).setCurrentUserGoing(i % 2 == 0);
                    events.get(i).setTicketTypes(ticketTypes);
                    events.get(i).setWebLink("https://facebook.com");
                    events.get(i).setOrganizerContact("0266349205");
                    EventManager.create()
                            .createEvent(events.get(i))
                            .subscribeOn(Schedulers.immediate())
                            .subscribe(onSuccess, onError);
                }
            }

            private void createEventNextMonth(Realm realm, User currentUser) {
                List<Event> events = new ArrayList<>(3);
                BillingAcount billingAcount =
                        new BillingAcount("username",
                                currentUser.phoneNumber, "MTN");
                for (int i = 0; i < 3; i++) {
                    events.add(new EventBuilder()
                            .setCreatedBy(currentUser.userId)
                            .setDateCreated(System.currentTimeMillis())
                            .setBillingAcount(billingAcount)
                            .setCategory(Math.max(1, i % 9))
                            .setFlyers(flyer.getAbsolutePath())
                            .setDateUpdated(System.currentTimeMillis())
                            .setStartDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(33))
                            .setEndDate(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(44))
                            .setDescription(description)
                            .setLikes(Math.abs(new SecureRandom().nextInt()) % 100)
                            .setGoing(Math.abs(new SecureRandom().nextInt()) % 100)
                            .setPublicity(Event.PUBLICITY_PUBLIC)
                            .setName("Event name " + i)
                            .setVenue(address)
                            .createEvent());
                    events.get(i).setLiked(i % 5 == 0);
                    events.get(i).setCurrentUserGoing(i % 2 == 0);
                    events.get(i).setWebLink("https://facebook.com");
                    events.get(i).setOrganizerContact("0266349205");
                    events.get(i).setTicketTypes(ticketTypes);
                    EventManager.create()
                            .createEvent(events.get(i))
                            .subscribeOn(Schedulers.immediate())
                            .subscribe(onSuccess, onError);
                }
            }

            private void createEventAfterNextMonth(Realm realm, User currentUser) {
                List<Event> events = new ArrayList<>(3);
                BillingAcount billingAcount =
                        new BillingAcount("username",
                                currentUser.phoneNumber, "MTN");

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                int afterNextMonth = calendar.get(Calendar.MONTH) + 2;
                int maxMonth = calendar.getActualMaximum(Calendar.MONTH);
                calendar.set(Calendar.MONTH, afterNextMonth > maxMonth ? (afterNextMonth - maxMonth) - 1/*start over*/ : afterNextMonth);

                for (int i = 0; i < 3; i++) {
                    events.add(new EventBuilder()
                            .setCreatedBy(currentUser.userId)
                            .setDateCreated(System.currentTimeMillis())
                            .setFlyers(flyer.getAbsolutePath())
                            .setDateUpdated(System.currentTimeMillis())
                            .setStartDate(calendar.getTimeInMillis())
                            .setEndDate(calendar.getTimeInMillis() + TimeUnit.DAYS.toMillis(3))
                            .setDescription(description)
                            .setCategory((1 + i) % 9)
                            .setLikes(Math.abs(new SecureRandom().nextInt()) % 100)
                            .setPublicity(Event.PUBLICITY_PUBLIC)
                            .setBillingAcount(billingAcount)
                            .setGoing(Math.abs(new SecureRandom().nextInt()) % 100)
                            .setName("Event name " + i)
                            .setVenue(address)
                            .createEvent());
                    events.get(i).setLiked(i % 5 == 0);
                    events.get(i).setCurrentUserGoing(i % 2 == 0);
                    events.get(i).setTicketTypes(ticketTypes);
                    events.get(i).setWebLink("https://facebook.com");
                    events.get(i).setOrganizerContact("0266349205");
                    EventManager.create()
                            .createEvent(events.get(i))
                            .subscribeOn(Schedulers.immediate())
                            .subscribe(onSuccess, onError);
                }
            }
        }.start();
    }

    private static Action1 onSuccess = new Action1() {
        public void call(Object next) {

        }
    };

    private static Action1<Throwable> onError = new Action1<Throwable>() {
        public void call(Throwable throwable) {
            PLog.e(TAG, throwable.getMessage(), throwable);
        }
    };
}
