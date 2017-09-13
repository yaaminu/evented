package com.evented.tickets;

import com.evented.utils.GenericUtils;
import com.parse.ParseObject;

import java.math.BigDecimal;
import java.math.MathContext;

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
    public static final String FIELD_TYPE = "type";


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

    private String type;

    public Ticket() {
    }

    public Ticket(String ticketId, String eventId, String purchasedBy,
                  String ownerPhone, String ticketSignature,
                  long datePurchased, long ticketCost, int ticketNumber, String eventName, String type) {
        this.ticketId = ticketId;
        this.eventId = eventId;
        this.purchasedBy = purchasedBy;
        this.ownerPhone = ownerPhone;
        this.ticketSignature = ticketSignature;
        this.datePurchased = datePurchased;
        this.ticketCost = ticketCost;
        this.ticketNumber = ticketNumber;
        this.eventName = eventName;
        this.type = type;

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
                ", type='" + type + '\'' +
                '}';
    }

    public String getType() {
        return type;
    }

    public String getFormattedCost() {
        return "GH₵" + GenericUtils.format(BigDecimal.valueOf(getTicketCost()).divide(BigDecimal.valueOf(100),
                MathContext.DECIMAL128).doubleValue());
    }

    public static Ticket create(ParseObject obj) {
        return new Ticket(
                obj.getObjectId(),
                obj.getString("eventId"),
                obj.getString("purchasedBy"),
                obj.getString("ownerPhone"),
                obj.getString("ticketSignature"),
                obj.getCreatedAt().getTime(),
                obj.getLong("ticketCost"),
                obj.getInt("ticketNumber"),
                obj.getString("eventName"),
                obj.getString("ticketType")
        );
    }
}
