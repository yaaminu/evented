package com.evented.events.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

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
    public static final String FIELD_LIKED = "liked";
    public static final String FIELD_LIKES = "likes";


    public static final String CLASS_NAME = "event";

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

    private BillingAcount billingAccount;
    private boolean liked;
    private boolean currentUserGoing;
    private String organizerContact;
    private String webLink;
    private RealmList<TicketType> ticketTypes;


    public Event() {
    }

    public ParseObject toParseObject(@NonNull User currentUser) {
        ParseObject parseObject = ParseObject.create(CLASS_NAME);
        parseObject.put("webLink", getWebLink());
        parseObject.put("organizerContact", getOrganizerContact());
        parseObject.put("endDate", getEndDate());
        parseObject.put("startDate", getStartDate());
        parseObject.put("name", getName());
        parseObject.put("publicity", getPublicity());
        parseObject.put("likes", getLikes());
        parseObject.put("going", getGoing());
        parseObject.put("venue", venue.toJsonString());
        parseObject.put("billingAccount", getBillingAccount().toJsonString());
        if (flyers != null) {
            parseObject.put("flyers", flyers);
        }
        parseObject.put("createdBy", currentUser.userId);
        parseObject.put("category", category);
        if (getEventId() != null) {
            parseObject.setObjectId(getEventId());
        }
        parseObject.put("description", getDescription());
        parseObject.put("ticketTypes", encodeListToJsonArray(ticketTypes));
        return parseObject;
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
        this.billingAccount = billingAcount;
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
        billingAccount = in.readParcelable(BillingAcount.class.getClassLoader());
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
        this.billingAccount = billingAcount;
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
        parcel.writeParcelable(billingAccount, i);
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

    public String getWebLink() {
        return webLink;
    }

    public String getOrganizerContact() {
        return organizerContact;
    }


    public int getPublicity() {
        return publicity;
    }

    public BillingAcount getBillingAccount() {
        return billingAccount;
    }

    private static String encodeListToJsonArray(List<TicketType> ticketTypes) {
        JSONArray jsonArray = new JSONArray();

        try {
            for (TicketType ticketType : ticketTypes) {
                jsonArray.put(ticketType.toJSONString());
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonArray.toString();
    }

    private static RealmList<TicketType> decodeListFromJsonArray(String text) {
        try {
            JSONArray jsonArray = new JSONArray(text);
            RealmList<TicketType> realmList = new RealmList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                realmList.add(TicketType.fromJson(jsonArray.getString(i)));
            }
            return realmList;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static Event create(@NonNull ParseObject obj) {
        Event event = new Event(obj.getObjectId(),
                obj.getString("createdBy"),
                obj.getString("name"),
                obj.getString("flyers"),
                obj.getString("description"),
                Venue.fromJson(obj.getString("venue")),
                obj.getLong("startDate"),
                obj.getLong("endDate"),
                obj.getUpdatedAt().getTime(),
                obj.getCreatedAt().getTime(),
                obj.getInt("publicity"),
                obj.getInt("likes"),
                obj.getInt("going"),
                BillingAcount.fromJson(obj.getString("billingAccount")),
                obj.getInt("category")
        );
        event.setTicketTypes(decodeListFromJsonArray(obj.getString("ticketTypes")));
        event.setOrganizerContact(obj.getString("organizerContact"));
        event.setWebLink(obj.getString("webLink"));
        return event;
    }
}
