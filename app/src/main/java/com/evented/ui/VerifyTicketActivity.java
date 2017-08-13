package com.evented.ui;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.events.ui.BaseFragment;
import com.evented.tickets.Ticket;
import com.evented.utils.GenericUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by yaaminu on 8/11/17.
 */

public class VerifyTicketActivity extends AppCompatActivity implements VerifyTicketCallbacks {
    public static final String EXTRA_EVENT_ID = "eventId";
    int currentItem;

    private Realm realm;
    private Event event;
    @Nullable
    private Ticket ticket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        currentItem = 0;


        setContentView(R.layout.activity_verify_ticket);
        ButterKnife.bind(this);

        // TODO: 8/12/17 deal with activity rotations

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        handleIntent(getIntent());
    }


    private void handleIntent(Intent intent) {
        if (!NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            String eventId = intent.getStringExtra(EXTRA_EVENT_ID);
            GenericUtils.assertThat(!GenericUtils.isEmpty(eventId), "event id required");

            event = realm.where(Event.class)
                    .equalTo(Event.FIELD_EVENT_ID, eventId)
                    .findFirst();
            GenericUtils.ensureNotNull(event);
        }
        Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tagFromIntent != null) {
            onClick(findViewById(R.id.use_nfc));
        } else {
            onClick(findViewById(R.id.scan_qrcode));
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); //order is important.
        handleIntent(intent);
    }

    @OnClick({R.id.use_nfc, R.id.scan_qrcode})
    void onClick(View v) {
        if (currentItem == v.getId()) {
            return;
        }
        ticket = null;

        if (currentItem != 0) {
            findViewById(currentItem).setSelected(false);

        }
        currentItem = v.getId();
        v.setSelected(true);

        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, getFragment(currentItem));

        transaction.replace(R.id.ticket_details_container, new EmptyInstructionsFragment());
        transaction.commit();
    }

    @NonNull
    private BaseFragment getFragment(int currentItem) {
        if (currentItem == R.id.use_nfc) {
            return new NfcFragment();
        } else if (currentItem == R.id.scan_qrcode) {
            return new QrCodeFragment();
        } else {
            throw new AssertionError();
        }
    }

    //will be called as a callback
    @Override
    public void onTicketVerified(Ticket ticket) {
        this.ticket = ticket;
        if (ticket.getVerifications() > 1) {
            final Snackbar snackbar = Snackbar.make(getWindow().getDecorView(), R.string.overused_ticket_warning, Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.i_know, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    });
            snackbar.getView()
                    .setBackgroundResource(R.color.red);
            TextView textView = snackbar.getView().findViewById(android.support.design.R.id.snackbar_action);
            textView.setTextColor(getResources().getColor(R.color.white));
            snackbar
                    .show();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.ticket_details_container, new MiniTicketDetailsFragment())
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Event getEvent() {
        return event;
    }

    @Nullable
    @Override
    public Ticket getTicket() {
        return ticket;
    }

    @Override
    public void clearCurrentTicket() {
        ticket = null;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.ticket_details_container, new EmptyInstructionsFragment())
                .commit();
    }

    @Override
    public int getVerificationStrategy() {
        return currentItem == R.id.use_nfc ? NFC : QRCOCDE;
    }
}
