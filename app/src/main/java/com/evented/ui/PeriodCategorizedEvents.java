package com.evented.ui;

import android.support.annotation.NonNull;

import com.evented.events.data.Event;

import java.util.List;

/**
 * Created by yaaminu on 8/9/17.
 */

public class PeriodCategorizedEvents {
    public final List<Event> events;
    public final String title;
    public final long oldestTime;

    public PeriodCategorizedEvents(long oldestTime, @NonNull String title, @NonNull List<Event> events) {
        this.title = title;
        this.events = events;
        this.oldestTime = oldestTime;
    }

}
