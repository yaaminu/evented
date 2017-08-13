package com.evented.tickets;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by yaaminu on 8/8/17.
 */

public class Ticket extends RealmObject {
    public static final String FIELD_ticketId = "ticketId";
    public static final String FIELD_DATE_PURCHASED = "datePurchased",
            FIELD_EVENT_ID = "eventId", FIELD_EVENT_NAME = "eventName",
            FIELD_TICKET_NUMBER = "ticketNumber", FIELD_TICKET_COST = "ticketCost",
            FIELD_TICKET_SIGNATURE = "ticketSignature";
    public static final String OWNER_NUMBER = "ownerPhone";


    @PrimaryKey
    private String ticketId;
    private String eventName;
    private int ticketNumber;

    @Index
    private String eventId;
    @Index
    private String purchasedBy;
    private String ownerPhone;
    private long datePurchased;
    private long ticketCost;
    private String ticketSignature;
    private int verifications;

    public Ticket() {
    }

    public Ticket(String ticketId, String eventId, String purchasedBy,
                  String ownerPhone, String ticketSignature,
                  long datePurchased, long ticketCost, int ticketNumber, String eventName) {
        this.ticketId = ticketId;
        this.eventId = eventId;
        this.purchasedBy = purchasedBy;
        this.ownerPhone = ownerPhone;
        this.ticketSignature = ticketSignature;
        this.datePurchased = datePurchased;
        this.ticketCost = ticketCost;
        this.ticketNumber = ticketNumber;
        this.eventName = eventName;

    }

    public String getEventName() {
        return eventName;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getEventId() {
        return eventId;
    }

    public String getPurchasedBy() {
        return purchasedBy;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public long getDatePurchased() {
        return datePurchased;
    }

    public long getTicketCost() {
        return ticketCost;
    }

    public String getTicketSignature() {
        return ticketSignature;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    public void setVerifications(int verifications) {
        this.verifications = verifications;
    }

    public int getVerifications() {
        return verifications;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId='" + ticketId + '\'' +
                ", eventName='" + eventName + '\'' +
                ", ticketNumber=" + ticketNumber +
                ", eventId='" + eventId + '\'' +
                ", purchasedBy='" + purchasedBy + '\'' +
                ", ownerPhone='" + ownerPhone + '\'' +
                ", datePurchased=" + datePurchased +
                ", ticketCost=" + ticketCost +
                ", ticketSignature='" + ticketSignature + '\'' +
                ", verifications=" + verifications +
                '}';
    }
}
