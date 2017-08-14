package com.evented.utils;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.evented.tickets.Ticket;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by yaaminu on 8/13/17.
 */

public class TicketCsvUtils {


    final SimpleDateFormat format;


    @SuppressLint("SimpleDateFormat")
    public TicketCsvUtils() {
        format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    }

    private String getHeader() {
        String[] headers = new String[4];
        headers[0] = ("Ticket No.");
        headers[1] = ("Date Purchased");
        headers[2] = ("Cost");
        headers[3] = "Class";
        //noinspection StringBufferReplaceableByString
        return new StringBuilder()
                .append(headers[0])
                .append(",")
                .append(headers[1])
                .append(",")
                .append(headers[2])
                .append(",")
                .append(headers[3])
                .append("\n")
                .toString();
    }

    private String getRow(Ticket ticket) {
        String[] row = new String[4];
        row[0] = (ticket.getTicketNumber() + "");
        row[1] = (format.format(new Date(ticket.getDatePurchased())));
        row[2] = (ticket.getFormattedCost());
        row[3] = ticket.getType();
        //noinspection StringBufferReplaceableByString
        return new StringBuilder()
                .append(row[0])
                .append(" ,")
                .append(row[1])
                .append("       ,")
                .append(row[2])
                .append(",")
                .append(row[3])
                .append("   \n")
                .toString();
    }

    public File generateCsv(@NonNull List<Ticket> tickets, @NonNull String destination) throws IOException {
        File file = new File(destination);
        if (!file.getParentFile().isDirectory()) {
            if (!file.getParentFile().mkdirs()) {
                throw new IOException("failed to save file");
            }
        }
        checkIfCancelled();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            //write header
            IOUtils.write(getHeader(), fileOutputStream);

            checkIfCancelled();
            for (Ticket ticket : tickets) {
                checkIfCancelled();
                IOUtils.write(getRow(ticket), fileOutputStream);
            }
            return file;
        } catch (IOException e) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
            throw e;
        } finally {
            fileOutputStream.close();
        }
    }

    private void checkIfCancelled() throws InterruptedIOException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedIOException("cancelled");
        }
    }
}
