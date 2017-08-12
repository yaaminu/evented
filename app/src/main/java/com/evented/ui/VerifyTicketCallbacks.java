package com.evented.ui;

import com.evented.tickets.Ticket;

/**
 * Created by yaaminu on 8/12/17.
 */
interface VerifyTicketCallbacks {
    int NFC = 1, QRCOCDE = 2;

    void onTicketVerified(Ticket ticket);

    int getVerificationStrategy();
}
