package com.evented.utils;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import com.evented.tickets.Ticket;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by yaaminu on 8/13/17.
 */

public class TicketCsvUtils {


    final SimpleDateFormat format;
    public static final NumberFormat FORMAT = DecimalFormat.getNumberInstance();

    static {
        FORMAT.setMaximumFractionDigits(2);
        FORMAT.setMinimumFractionDigits(2);
        FORMAT.setRoundingMode(RoundingMode.HALF_UP);
    }

    @SuppressLint("SimpleDateFormat")
    public TicketCsvUtils() {
        format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    }

    private String getHeader() {
        String[] headers = new String[3];
        headers[0] = ("Ticket No.");
        headers[1] = ("Date Purchased");
        headers[2] = ("Cost");
        //noinspection StringBufferReplaceableByString
        return new StringBuilder()
                .append(headers[0])
                .append(",")
                .append(headers[1])
                .append(",")
                .append(headers[2])
                .append("\n")
                .toString();
    }

    private String getRow(Ticket ticket) {
        String[] row = new String[3];
        row[0] = (ticket.getTicketNumber() + "");
        row[1] = (format.format(new Date(ticket.getDatePurchased())));
        row[2] = (FORMAT.format(BigDecimal.valueOf(ticket.getTicketCost())
                .divide(BigDecimal.valueOf(100), MathContext.DECIMAL128)));
        //noinspection StringBufferReplaceableByString
        return new StringBuilder()
                .append(row[0])
                .append(" ,")
                .append(row[1])
                .append("       ,")
                .append(row[2])
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
