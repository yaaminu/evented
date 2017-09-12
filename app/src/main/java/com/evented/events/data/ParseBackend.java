package com.evented.events.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;

import com.evented.BuildConfig;
import com.evented.utils.GenericUtils;
import com.evented.utils.PLog;
import com.evented.utils.PhoneNumberNormaliser;
import com.evented.utils.ThreadUtils;
import com.google.i18n.phonenumbers.NumberParseException;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

/**
 * Created by yaaminu on 9/12/17.
 */

public class ParseBackend {

    private static final String TAG = "ParseBackend";

    private static class Holder {
        private static ParseBackend instance = new ParseBackend();

    }

    public static ParseBackend getInstance() {
        return Holder.instance;
    }


    @Nullable
    public User getCurrentUser() {
        final ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            return User.create(currentUser);
        }
        return null;
    }

    public Observable<User> loginOrSignup(Context context, final String phoneNumber) {
        return logout(context)
                .flatMap(new Func1<Object, Observable<ParseObject>>() {
                    @Override
                    public Observable<ParseObject> call(Object o) {
                        return Observable.create(new Observable.OnSubscribe<ParseObject>() {
                            @Override
                            public void call(final Subscriber<? super ParseObject> subscriber) {
                                subscriber.onStart();
                                try {
                                    subscriber.onNext(doLoginOrSignup(phoneNumber));
                                    subscriber.onCompleted();
                                } catch (ParseException e) {
                                    PLog.e(TAG, e.getMessage(), e);
                                    subscriber.onError(e);
                                }
                            }
                        });
                    }
                }).map(new Func1<ParseObject, User>() {
                    @Override
                    public User call(ParseObject parseObject) {
                        return User.create(parseObject);
                    }
                });
    }

    @NonNull
    private ParseUser doLoginOrSignup(@NonNull String phoneNumber) throws ParseException {
        final char[] password = phoneNumberToPassword(phoneNumber);
        try {
            try {
                final String standardizedNum = PhoneNumberNormaliser.toIEE(phoneNumber, "GH");

                ParseUser parseUser = ParseUser.logIn(phoneNumber, String.valueOf(password));

                parseUser.put(Constants.VERIFIED, false);
                parseUser.save();

                try {
                    String message = "Your verification code is 1234";
                    SmsManager.getDefault()
                            .sendTextMessage(standardizedNum,
                                    null, message, null, null);
                } catch (SecurityException ignored) {
                }
                return parseUser;
            } catch (NumberParseException e) {
                throw new ParseException(ParseException.INVALID_JSON, "Phone number invalid");
            }
        } catch (ParseException e) {
            PLog.d(TAG, e.getMessage(), e);
            if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                return doSignUp(phoneNumber, password);
            }
            throw e;
        }
    }

    private char[] phoneNumberToPassword(@NonNull String phoneNumber) {
        PLog.w(TAG, "using fake password generator");
        return phoneNumber.toCharArray();
    }

    @NonNull
    private ParseUser doSignUp(@NonNull String phoneNumber, char[] password) throws ParseException {
        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(phoneNumber);
        parseUser.put(Constants.VERIFIED, false);
        parseUser.put(Constants.PHONE_NUMBER, phoneNumber);
        parseUser.setPassword(String.valueOf(password));
        parseUser.signUp();
        return ParseUser.getCurrentUser();
    }

    @NotNull
    public Observable<User> verify(@NotNull final String verificationCode) {
        return Observable.create(new Observable.OnSubscribe<ParseObject>() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void call(Subscriber<? super ParseObject> subscriber) {
                try {
                    if (!"1234".equals(verificationCode)) {
                        throw new ParseException(ParseException.VALIDATION_ERROR,
                                "invalid verification code");
                    }
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    GenericUtils.assertThat(currentUser != null, "must be logged in");
                    currentUser.put(Constants.VERIFIED, true);
                    currentUser.save();
                    subscriber.onNext(currentUser);
                    subscriber.onCompleted();
                } catch (ParseException e) {
                    subscriber.onError(e);
                }
            }
        }).map(new Func1<ParseObject, User>() {
            @Override
            public User call(ParseObject parseObject) {
                return User.create(parseObject);
            }
        });
    }

    public static void initialize(Context context) {
        com.parse.Parse.initialize(new com.parse.Parse.Configuration.Builder(context)
                .server(BuildConfig.SERVER_URL)
                .enableLocalDataStore()
                .applicationId(BuildConfig.APP_ID)
                .build());
        com.parse.Parse.setLogLevel(com.parse.Parse.LOG_LEVEL_VERBOSE);
    }


    public Observable<?> logout(final Context context) {
        ParseUser user = ParseUser.getCurrentUser();
        if (user == null) {
            return Observable.just(true);
        }

        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                try {
                    GenericUtils.assertThat(!ThreadUtils.isMainThread());
                    GenericUtils.clearSharedPrefs(context);
                    bolts.Task task = ParseUser.logOutInBackground();
                    task.waitForCompletion();
                    if (task.getError() != null) {
                        subscriber.onError(task.getError());
                    } else {
                        subscriber.onNext("");
                        subscriber.onCompleted();
                    }
                } catch (InterruptedException e) {
                    throw new AssertionError();
                }
            }
        });
    }
}
