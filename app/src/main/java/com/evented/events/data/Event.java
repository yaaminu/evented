package com.evented.events.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.evented.events.ui.Venue;

import org.jetbrains.annotations.Nullable;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by yaaminu on 8/8/17.
 */

public class Event extends RealmObject implements Parcelable {

    public static final String FIELD_START_DATE = "startDate",
            END_DATE = "endDate";
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
    private int maxSeats;
    private int likes;
    private int going;
    private long entranceFee;

    private BillingAcount billingAcount;
    private boolean liked;
    private boolean currentUserGoing;
    private String organizerContact;
    private String webLink;


    public Event() {
    }

    Event(String eventId, String createdBy, String name, String flyers, String description, Venue venue
            , long startDate, long endDate, long dateUpdated, long dateCreated, int publicity, int maxSeats,
          int likes, int going, long entranceFee, BillingAcount billingAcount, int category) {
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
        this.maxSeats = maxSeats;
        this.likes = likes;
        this.going = going;
        this.entranceFee = entranceFee;
        this.billingAcount = billingAcount;
    }


    protected Event(Parcel in) {
        eventId = in.readString();
        createdBy = in.readString();
        name = in.readString();
        flyers = in.readString();
        description = in.readString();
        category = in.readInt();
        venue = in.readParcelable(Venue.class.getClassLoader());
        startDate = in.readLong();
        endDate = in.readLong();
        dateUpdated = in.readLong();
        dateCreated = in.readLong();
        publicity = in.readInt();
        maxSeats = in.readInt();
        likes = in.readInt();
        going = in.readInt();
        entranceFee = in.readLong();
        liked = in.readByte() != 0;
        currentUserGoing = in.readByte() != 0;
        organizerContact = in.readString();
        webLink = in.readString();
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

    public String getCreatedBy() {
        return createdBy;
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

    public long getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(long dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getPublicity() {
        return publicity;
    }

    public void setPublicity(int publicity) {
        this.publicity = publicity;
    }

    public int getMaxSeats() {
        return maxSeats;
    }

    public void setMaxSeats(int maxSeats) {
        this.maxSeats = maxSeats;
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

    public long getEntranceFee() {
        return entranceFee;
    }

    public void setEntranceFee(long entranceFee) {
        this.entranceFee = entranceFee;
    }

    public void setBillingAcount(BillingAcount billingAcount) {
        this.billingAcount = billingAcount;
    }

    @Override
    public String toString() {
        return "Event{" +
                "eventId='" + eventId + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", name='" + name + '\'' +
                ", flyers='" + flyers + '\'' +
                ", description='" + description + '\'' +
                ", venue='" + venue + '\'' +
                ", startDate=" + new Date(startDate) +
                ", endDate=" + new Date(endDate) +
                ", dateUpdated=" + new Date(dateUpdated) +
                ", dateCreated=" + new Date(dateCreated) +
                ", publicity=" + publicity +
                ", maxSeats=" + maxSeats +
                ", likes=" + likes +
                ", going=" + going +
                ", entranceFee=" + entranceFee +
                ", billingAcount=" + billingAcount +
                '}';
    }

    @Nullable
    public BillingAcount getBillingAcount() {
        return billingAcount;
    }

    public int getCategory() {
        return category;
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

    public String getOrganizerContact() {
        return organizerContact;
    }

    public String getWebLink() {
        return webLink;
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
        parcel.writeLong(startDate);
        parcel.writeLong(endDate);
        parcel.writeLong(dateUpdated);
        parcel.writeLong(dateCreated);
        parcel.writeInt(publicity);
        parcel.writeInt(maxSeats);
        parcel.writeInt(likes);
        parcel.writeInt(going);
        parcel.writeLong(entranceFee);
        parcel.writeByte((byte) (liked ? 1 : 0));
        parcel.writeByte((byte) (currentUserGoing ? 1 : 0));
        parcel.writeString(organizerContact);
        parcel.writeString(webLink);
    }
}
