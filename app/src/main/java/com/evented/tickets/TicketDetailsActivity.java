package com.evented.tickets;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.utils.GenericUtils;

import java.math.BigDecimal;
import java.math.MathContext;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by yaaminu on 8/10/17.
 */

public class TicketDetailsActivity extends AppCompatActivity {

    @BindView(R.id.tv_event_name)
    TextView tv_event_name;
    @BindView(R.id.tv_start_time)
    TextView tv_start_time;
    @BindView(R.id.tv_date_purchased)
    TextView tv_date_purchased;
    @BindView(R.id.tv_number)
    TextView tv_number;
    @BindView(R.id.tv_ticket_cost)
    TextView tv_ticket_cost;

    @BindView(R.id.ticket_qr_code)
    ImageView ticketQrCode;

    public static final String EXTRA_TICKET_ID = "ticketId";
    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String ticketId = getIntent().getStringExtra(EXTRA_TICKET_ID);
        GenericUtils.ensureNotEmpty(ticketId);

        setContentView(R.layout.activity_ticket_details);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();
        Ticket ticket = realm.where(Ticket.class)
                .equalTo(Ticket.FIELD_ticketId, ticketId)
                .findFirst();
        GenericUtils.ensureNotNull(ticket);

        tv_date_purchased.setText(getString(R.string.purchased_on, DateUtils.formatDateTime(TicketDetailsActivity.this,
                ticket.getDatePurchased(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_12HOUR | DateUtils.FORMAT_ABBREV_ALL)));

        tv_number.setText(getString(R.string.ticket_no, ticket.getTicketNumber()));

        Event event = realm.where(Event.class)
                .equalTo(Event.FIELD_EVENT_ID, ticket.getEventId()).findFirst();

        tv_event_name.setText(event.getName());
        tv_ticket_cost.setText(getString(R.string.ticket_cost, "" + BigDecimal.valueOf(ticket.getTicketCost()).divide(BigDecimal.valueOf(100),
                MathContext.DECIMAL128)));

        tv_start_time.setText(getString(R.string.starts_at, DateUtils.formatDateTime(TicketDetailsActivity.this,
                event.getStartDate(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_12HOUR | DateUtils.FORMAT_ABBREV_ALL)));


        ticketQrCode.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.denu));

    }


    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }
}
