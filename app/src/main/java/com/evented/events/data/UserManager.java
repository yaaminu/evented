package com.evented.events.data;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.telephony.SmsManager;

import com.evented.utils.Config;
import com.evented.utils.GenericUtils;
import com.evented.utils.PhoneNumberNormaliser;
import com.google.i18n.phonenumbers.NumberParseException;

import org.jetbrains.annotations.Nullable;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by yaaminu on 8/9/17.
 */

public class UserManager {

    private static final UserManager INSTANCE = new UserManager();
    private static final String LOGGED_IN = "logged_in";
    private static final String USER_ID = "userId";
    private static final String PHONE_NUMBER = "phoneNumber";
    private static final String USER_NAME = "userName";
    private static final String VERIFIED = "verified";
    private static final String SESSION_PREF = "session";

    private UserManager() {
    }

    public static UserManager getInstance() {
        return INSTANCE;
    }

    @Nullable
    public User getCurrentUser() {
        SharedPreferences preferences = Config.getPreferences(SESSION_PREF);
        if (!preferences.getBoolean(LOGGED_IN, false)) {
            return null;
        }
        String userName = preferences.getString(USER_NAME, null),
                userPhone = preferences.getString(PHONE_NUMBER, null),
                userId = preferences.getString(USER_ID, null);
        if (GenericUtils.isEmpty(userId) || GenericUtils.isEmpty(userPhone) || GenericUtils.isEmpty(userName)) {
            return null;
        }
        return new User(userName, userId, userPhone);
    }

    public rx.Observable<User> login(final String phone) {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void call(Subscriber<? super User> subscriber) {
                subscriber.onStart();
                SystemClock.sleep(2000);
                final User user = new User("Unspecified", "userID", phone);
                Config.getPreferences(SESSION_PREF).edit().putBoolean(LOGGED_IN, true)
                        .putString(USER_ID, user.userId)
                        .putString(PHONE_NUMBER, user.phoneNumber)
                        .putString(USER_NAME, user.userName)
                        .commit();
                // TODO: 8/13/17 send a verification code to the phone number
                String message = "Your verification code is 1234";
                try {
                    SmsManager.getDefault()
                            .sendTextMessage(PhoneNumberNormaliser.toIEE(phone, "GH"),
                                    null, message, null, null);
                } catch (NumberParseException e) {
                    subscriber.onError(e);
                    return;
                } catch (SecurityException e) {

                }
                subscriber.onNext(user);
                subscriber.onCompleted();
            }
        });
    }

    public boolean isCurrentUserVerified() {
        return getCurrentUser() != null && Config.getPreferences(SESSION_PREF)
                .getBoolean(VERIFIED, false);
    }

    public rx.Observable<User> verify(final String verificationCode) {
        return Observable.create(new Observable.OnSubscribe<User>() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void call(Subscriber<? super User> subscriber) {
                subscriber.onStart();
                SystemClock.sleep(2000);
                final User currentUser = getCurrentUser();
                GenericUtils.assertThat(currentUser != null, "No user logged in");
                if ("12345".equals(verificationCode)) {
                    Config.getPreferences(SESSION_PREF)
                            .edit()
                            .putBoolean(VERIFIED, true)
                            .commit();
                    subscriber.onNext(currentUser);
                    subscriber.onCompleted();
                } else {
                    subscriber.onError(new Exception("Invalid verification code"));
                }
            }
        });
    }

    public Observable<?> logout() {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                subscriber.onStart();
                SystemClock.sleep(3000);
                Config.getPreferences(SESSION_PREF)
                        .edit()
                        .clear()
                        .commit();
                GenericUtils.clearSharedPrefs(Config.getApplicationContext());
                subscriber.onNext(true);
                subscriber.onCompleted();
            }
        });
    }
}
