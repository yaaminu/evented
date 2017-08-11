package com.evented.tickets;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.events.data.EventManager;
import com.evented.events.data.UserManager;
import com.evented.utils.GenericUtils;
import com.evented.utils.ViewUtils;

import java.math.BigDecimal;
import java.math.MathContext;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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
    @BindView(R.id.tv_ticket_number)
    TextView tv_ticket_number;
    @BindView(R.id.tv_ticket_cost)
    TextView tv_ticket_cost;

    @BindView(R.id.ticket_qr_code)
    ImageView ticketQrCode;

    @BindView(R.id.progress)
    ProgressBar progress;


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

        tv_ticket_number.setText(getString(R.string.ticket_no, ticket.getTicketNumber()));

        tv_event_name.setText(ticket.getEventName());

        tv_ticket_cost.setText(getString(R.string.ticket_cost, "" + BigDecimal.valueOf(ticket.getTicketCost()).divide(BigDecimal.valueOf(100),
                MathContext.DECIMAL128)));

        Event event = realm.where(Event.class)
                .equalTo(Event.FIELD_EVENT_ID, ticket.getEventId()).findFirst();

        tv_start_time.setText(getString(R.string.starts_at, event == null ? getString(R.string.date_aunavialable) : DateUtils.formatDateTime(TicketDetailsActivity.this,
                event.getStartDate(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_12HOUR | DateUtils.FORMAT_ABBREV_ALL)));
        ViewUtils.showViews(progress);

        EventManager.create(UserManager.getInstance())
                .qrCode(realm.copyFromRealm(ticket))
                .subscribeOn(Schedulers.computation())
                .map(new Func1<byte[], Bitmap>() {
                    @Override
                    public Bitmap call(byte[] bytes) {
                        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success, error);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    Action1<Bitmap> success = new Action1<Bitmap>() {
        @Override
        public void call(Bitmap bitmap) {
            ViewUtils.hideViews(progress);
            ticketQrCode.setImageBitmap(bitmap);
        }
    };
    Action1<Throwable> error = new Action1<Throwable>() {
        @Override
        public void call(Throwable e) {
            ViewUtils.hideViews(progress);
            // TODO: 8/11/17 show error
            showError(e.getMessage());
        }
    };

    void showError(String message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null);
    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
