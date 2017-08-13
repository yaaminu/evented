package com.evented.utils;

import com.evented.tickets.Ticket;

import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yaaminu on 8/13/17.
 */
public class TicketCsvUtilsTest {
    @Test
    public void generateCsv() throws Exception {
        TicketCsvUtils ticketCsvUtils = new TicketCsvUtils();
        List<Ticket> tickets = new ArrayList<>(100);
        for (int i = 0; i < 100; i++) {
            tickets.add(new Ticket("ticketId" + i, "eventId", "", "lkdf", "kda;f", System.currentTimeMillis(),
                    100 + (i % 10000), i + 1, "Event name"));
        }

        File destination = ticketCsvUtils.generateCsv(tickets, "/tmp/ouput_" + System.currentTimeMillis() + ".csv");

        Runtime.getRuntime()
                .exec("xdg-open " + destination.getAbsolutePath());
    }

}