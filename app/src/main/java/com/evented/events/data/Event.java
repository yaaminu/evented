package com.evented.events.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by yaaminu on 8/8/17.
 */

public class Event extends RealmObject implements Parcelable {
    @SuppressWarnings("unused")
    public static final String FIELD_START_DATE = "startDate",
            END_DATE = "endDate";
    @SuppressWarnings("unused")
    public static final int PUBLICITY_PUBLIC = 0,
            PUBLICITY_PRIVATE = 1,
            PUBLICITY_SECRETE = 2;
    public static final String FIELD_EVENT_ID = "eventId";
    public static final int CATEGORY_ALL = 0;
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_GOING = "going";

    @PrimaryKey
    private String eventId;

    private String createdBy;
    private String name;
    private String flyers;
    private String description;
    private int category;
    private Venue venue;
    private long startDate;
    private long endDate;
    private long dateUpdated;
    private long dateCreated;
    private int publicity;
    private int likes;
    private int going;

    private BillingAcount billingAcount;
    private boolean liked;
    private boolean currentUserGoing;
    private String organizerContact;
    private String webLink;
    private RealmList<TicketType> ticketTypes;


    public Event() {
    }

    Event(String eventId, String createdBy, String name, String flyers, String description, Venue venue
            , long startDate, long endDate, long dateUpdated, long dateCreated, int publicity,
          int likes, int going, BillingAcount billingAcount, int category) {
        this.eventId = eventId;
        this.createdBy = createdBy;
        this.name = name;
        this.category = category;
        this.flyers = flyers;
        this.description = description;
        this.venue = venue;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dateUpdated = dateUpdated;
        this.dateCreated = dateCreated;
        this.publicity = publicity;
        this.likes = likes;
        this.going = going;
        this.billingAcount = billingAcount;
        this.ticketTypes = new RealmList<>();
    }


    protected Event(Parcel in) {
        eventId = in.readString();
        createdBy = in.readString();
        name = in.readString();
        flyers = in.readString();
        description = in.readString();
        category = in.readInt();
        venue = in.readParcelable(Venue.class.getClassLoader());
        //noinspection unchecked
        ticketTypes = arrayListToRealmList(in.readArrayList(null));
        startDate = in.readLong();
        endDate = in.readLong();
        dateUpdated = in.readLong();
        dateCreated = in.readLong();
        publicity = in.readInt();
        likes = in.readInt();
        going = in.readInt();
        liked = in.readByte() != 0;
        currentUserGoing = in.readByte() != 0;
        organizerContact = in.readString();
        webLink = in.readString();
        billingAcount = in.readParcelable(BillingAcount.class.getClassLoader());
    }

    private RealmList<TicketType> arrayListToRealmList(ArrayList<TicketType> arrayList) {
        RealmList<TicketType> realmList = new RealmList<>();
        realmList.addAll(arrayList);
        return realmList;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }


    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFlyers() {
        return flyers;
    }

    public void setFlyers(String flyers) {
        this.flyers = flyers;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }


    public void setDateUpdated(long dateUpdated) {
        this.dateUpdated = dateUpdated;
    }


    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }


    public void setPublicity(int publicity) {
        this.publicity = publicity;
    }


    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getGoing() {
        return going;
    }

    public void setGoing(int going) {
        this.going = going;
    }


    public void setBillingAcount(BillingAcount billingAcount) {
        this.billingAcount = billingAcount;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public boolean isLiked() {
        return liked;
    }

    public void setCurrentUserGoing(boolean currentUserGoing) {
        this.currentUserGoing = currentUserGoing;
    }

    public boolean isCurrentUserGoing() {
        return currentUserGoing;
    }

    public void setOrganizerContact(String organizerContact) {
        this.organizerContact = organizerContact;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(eventId);
        parcel.writeString(createdBy);
        parcel.writeString(name);
        parcel.writeString(flyers);
        parcel.writeString(description);
        parcel.writeInt(category);
        parcel.writeParcelable(venue, i);
        parcel.writeList(ticketTypes);
        parcel.writeLong(startDate);
        parcel.writeLong(endDate);
        parcel.writeLong(dateUpdated);
        parcel.writeLong(dateCreated);
        parcel.writeInt(publicity);
        parcel.writeInt(likes);
        parcel.writeInt(going);
        parcel.writeByte((byte) (liked ? 1 : 0));
        parcel.writeByte((byte) (currentUserGoing ? 1 : 0));
        parcel.writeString(organizerContact);
        parcel.writeString(webLink);
        parcel.writeParcelable(billingAcount, i);
    }

    public RealmList<TicketType> getTicketTypes() {
        return ticketTypes;
    }

    public void setTicketTypes(@NonNull RealmList<TicketType> tickTypes) {
        this.ticketTypes = tickTypes;
    }

    public int getMaxSeats() {
        int total = 0;
        for (TicketType ticketType : ticketTypes) {
            total += ticketType.getMaxSeat();
        }
        return total;
    }
}
