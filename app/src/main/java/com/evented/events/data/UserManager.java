package com.evented.events.data;

import org.jetbrains.annotations.Nullable;

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
        return new User("yaaminu", "ffjlak873", "233224444");
    }
}
