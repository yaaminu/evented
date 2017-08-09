package com.evented.events.data;

public class EventBuilder {
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
    private int publicity = 0;
    private int maxSeats = 0;
    private int likes = 0;
    private int going = 0;
    private BillingAcount billingAcount;
    private long entranceFee = 0;

    public EventBuilder setEventId(String eventId) {
        this.eventId = eventId;
        return this;
    }

    public EventBuilder setBillingAcount(BillingAcount billingAcount) {
        this.billingAcount = billingAcount;
        return this;
    }

    public EventBuilder setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public EventBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public EventBuilder setFlyers(String flyers) {
        this.flyers = flyers;
        return this;
    }

    public EventBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public EventBuilder setVenue(String venue) {
        this.venue = venue;
        return this;
    }

    public EventBuilder setStartDate(long startDate) {
        this.startDate = startDate;
        return this;
    }

    public EventBuilder setEndDate(long endDate) {
        this.endDate = endDate;
        return this;
    }

    public EventBuilder setDateUpdated(long dateUpdated) {
        this.dateUpdated = dateUpdated;
        return this;
    }

    public EventBuilder setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public EventBuilder setPublicity(int publicity) {
        this.publicity = publicity;
        return this;
    }

    public EventBuilder setMaxSeats(int maxSeats) {
        this.maxSeats = maxSeats;
        return this;
    }

    public EventBuilder setLikes(int likes) {
        this.likes = likes;
        return this;
    }

    public EventBuilder setGoing(int going) {
        this.going = going;
        return this;
    }

    public EventBuilder setEntranceFee(long entranceFee) {
        this.entranceFee = entranceFee;
        return this;
    }


    public Event createEvent() {
        return new Event(eventId, createdBy, name, flyers, description, venue, startDate, endDate, dateUpdated,
                dateCreated, publicity, maxSeats, likes, going, entranceFee,billingAcount);
    }
}