package com.evented.events.data;

/**
 * Created by yaaminu on 8/9/17.
 */

public class User {

    public final String userName, phoneNumber, userId;

    public User(String userName, String userId, String phoneNumber) {
        this.userName = userName;
        this.userId = userId;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
