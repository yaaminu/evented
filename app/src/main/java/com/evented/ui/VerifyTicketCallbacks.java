package com.evented.ui;

import com.evented.events.data.Event;
import com.evented.tickets.Ticket;

/**
 * Created by yaaminu on 8/12/17.
 */
interface VerifyTicketCallbacks {
    int NFC = 1, QRCOCDE = 2;

    void onTicketVerified(Ticket ticket);

    int getVerificationStrategy();

    Event getEvent();

    Ticket getTicket();

    void clearCurrentTicket();
}
