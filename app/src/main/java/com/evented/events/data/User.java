package com.evented.events.data;

import android.support.annotation.NonNull;

import com.evented.utils.GenericUtils;
import com.parse.ParseObject;

/**
 * Created by yaaminu on 8/9/17.
 */

public class User {

    public final String phoneNumber, userId;
    public final boolean verified;

    public User(@NonNull String userId, @NonNull String phoneNumber, boolean verified) {
        GenericUtils.ensureNotEmpty(userId, userId, phoneNumber);
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.verified = verified;
    }

    @NonNull
    public static User create(@NonNull ParseObject parseObject) {
        String phone = parseObject.getString(Constants.PHONE_NUMBER),
                userId = parseObject.getObjectId();
        boolean verified = parseObject.getBoolean(Constants.VERIFIED);
        return new User(phone, userId, verified);
    }

    @Override
    public String toString() {
        return "User{" +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", userId='" + userId + '\'' +
                ", verified='" + verified + '\'' +
                '}';
    }
}
