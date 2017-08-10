package com.evented.tickets;

import java.util.Date;

import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by yaaminu on 8/8/17.
 */

public class Ticket {
    public static final String FIELD_ticketId = "ticketId";


    @PrimaryKey
    private String ticketId;

    private int ticketNumber;

    @Index
    private String eventId;
    @Index
    private String purchasedBy;
    private String ownerPhone;
    private long datePurchased;
    private long ticketCost;
    private String ticketSignatuture;

    public Ticket() {
    }

    public Ticket(String ticketId, String eventId, String purchasedBy,
                  String ownerPhone, String ticketSignatuture,
                  long datePurchased, long ticketCost, int ticketNumber) {
        this.ticketId = ticketId;
        this.eventId = eventId;
        this.purchasedBy = purchasedBy;
        this.ownerPhone = ownerPhone;
        this.ticketSignatuture = ticketSignatuture;
        this.datePurchased = datePurchased;
        this.ticketCost = ticketCost;
        this.ticketNumber = ticketNumber;

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

    public String getTicketSignatuture() {
        return ticketSignatuture;
    }

    public int getTicketNumber() {
        return ticketNumber;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "ticketId='" + ticketId + '\'' +
                ", eventId='" + eventId + '\'' +
                ", purchasedBy='" + purchasedBy + '\'' +
                ", ownerPhone='" + ownerPhone + '\'' +
                ", datePurchased=" + new Date(datePurchased) +
                ", ticketCost=" + ticketCost +
                ", ticketSignatuture='" + ticketSignatuture + '\'' +
                ", ticketNumber='" + ticketNumber + '\'' +
                '}';
    }
}
