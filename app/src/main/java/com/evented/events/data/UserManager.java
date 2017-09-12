package com.evented.events.data;

import android.content.Context;

import org.jetbrains.annotations.Nullable;

import rx.Observable;

/**
 * Created by yaaminu on 8/9/17.
 */

public class UserManager {

    private static final UserManager INSTANCE = new UserManager();

    private UserManager() {
    }

    public static UserManager getInstance() {
        return INSTANCE;
    }

    @Nullable
    public User getCurrentUser() {
        return ParseBackend.getInstance().getCurrentUser();
    }

    public rx.Observable<User> login(Context context, final String phone) {
        return ParseBackend.getInstance().loginOrSignup(context.getApplicationContext(), phone);
    }

    public boolean isCurrentUserVerified() {
        final User currentUser = getCurrentUser();
        return currentUser != null && currentUser.verified;
    }

    public rx.Observable<User> verify(final String verificationCode) {
        return ParseBackend.getInstance()
                .verify(verificationCode);
    }

    public Observable<?> logout(Context context) {
        return ParseBackend.getInstance()
                .logout(context.getApplicationContext());
    }
}
