package com.evented.events.data;

import io.realm.RealmObject;

/**
 * Created by yaaminu on 8/8/17.
 */

public class Event extends RealmObject {

    private String eventId;
    private String createdBy;
    private String name;
    private String flyers;
    private String description;
    private String venue;
    private long startDate;
    private long endDate;
    private long dateUpdated;
    private long dateCreated;
    private int publicity;
    private int maxSeats;
    private int likes;
    private int going;
    private long entranceFee;

    public Event() {
    }

    Event(String eventId, String createdBy, String name, String flyers, String description, String venue
            , long startDate, long endDate, long dateUpdated, long dateCreated, int publicity, int maxSeats,
          int likes, int going, long entranceFee) {
        this.eventId = eventId;
        this.createdBy = createdBy;
        this.name = name;
        this.flyers = flyers;
        this.description = description;
        this.venue = venue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dateUpdated = dateUpdated;
        this.dateCreated = dateCreated;
        this.publicity = publicity;
        this.maxSeats = maxSeats;
        this.likes = likes;
        this.going = going;
        this.entranceFee = entranceFee;

    }
}
