package com.evented.tickets;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.events.data.EventManager;
import com.evented.utils.GenericUtils;
import com.evented.utils.PLog;
import com.evented.utils.TaskManager;
import com.evented.utils.ThreadUtils;
import com.evented.utils.ViewUtils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static android.nfc.NdefRecord.createMime;

/**
 * Created by yaaminu on 8/10/17.
 */

public class TicketDetailsActivity extends AppCompatActivity {
    private static final String TAG = "TicketDetailsActivity";

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
    @BindView(R.id.tv_ticket_type)
    TextView tv_ticket_type;

    @BindView(R.id.progress)
    ProgressBar progress;


    public static final String EXTRA_TICKET_ID = "ticketId";
    private Realm realm;
    private EventManager eventManager;
    private Ticket ticket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        eventManager = EventManager.create();
        setContentView(R.layout.activity_ticket_details);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.tickets_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void handleIntent(Intent intent) {
        if (intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) == null) {
            String ticketId = intent.getStringExtra(EXTRA_TICKET_ID);
            GenericUtils.ensureNotEmpty(ticketId);

            ticket = realm.where(Ticket.class)
                    .equalTo(Ticket.FIELD_ticketId, ticketId)
                    .findFirst();
            GenericUtils.ensureNotNull(ticket);

            tv_date_purchased.setText(getString(R.string.purchased_on, DateUtils.formatDateTime(TicketDetailsActivity.this,
                    ticket.getDatePurchased(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_12HOUR | DateUtils.FORMAT_ABBREV_ALL)));

            tv_ticket_number.setText(getString(R.string.ticket_no, ticket.getTicketNumber()));

            tv_event_name.setText(ticket.getEventName());

            tv_ticket_cost.setText(getString(R.string.ticket_cost, ticket.getFormattedCost()));

            Event event = realm.where(Event.class)
                    .equalTo(Event.FIELD_EVENT_ID, ticket.getEventId()).findFirst();

            tv_ticket_type.setText(getString(R.string.ticket_class, ticket.getType()));
            tv_start_time.setText(getString(R.string.starts_at, event == null ? getString(R.string.date_aunavialable) : DateUtils.formatDateTime(TicketDetailsActivity.this,
                    event.getStartDate(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_12HOUR | DateUtils.FORMAT_ABBREV_ALL)));
            ViewUtils.showViews(progress);

            eventManager
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
        }
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

    void showError(final String message) {
        if (ThreadUtils.isMainThread()) {
            new AlertDialog.Builder(this)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, null);
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showError(message);
                }
            });
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        }

        Tag tag = getIntent().getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tag != null) {
            processTag(tag);
        }
    }

    @Override
    protected void onPause() {
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
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
        } else if (item.getItemId() == R.id.action_verify_ticket_nfc) {
            setupNfcIntent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }


    private NfcAdapter nfcAdapter;
    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;
    private PendingIntent pendingIntent;

    private void setupNfcIntent() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter.isEnabled()) {
            pendingIntent = PendingIntent.getActivity(
                    this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            try {
                ndef.addDataType("application/vnd.com.evented.verify.ticket");
            } catch (IntentFilter.MalformedMimeTypeException e) {
                throw new RuntimeException("fail", e);
            }
            intentFiltersArray = new IntentFilter[]{ndef,};
            techListsArray = new String[][]{new String[]{Ndef.NFC_FORUM_TYPE_1}};
        } else {
            notifyNoNfc(this);
        }
    }

    public static void notifyNoNfc(final Context context) {
        new AlertDialog.Builder(context)
                .setMessage(R.string.no_nfc)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            context.startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
                        } else {
                            context.startActivity(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS));
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, null)
                .create().show();
    }

    private void processTag(final Tag tagFromIntent) {
        if (tagFromIntent != null) {
            final Ticket tmp = realm.copyFromRealm(ticket);
            TaskManager.executeNow(new Runnable() {
                @Override
                public void run() {
                    Ndef ndef = null;
                    try {
                        ndef = Ndef.get(tagFromIntent);
                        ndef.connect();
                        ndef
                                .writeNdefMessage(createNdefMessage(tmp));
                        // TODO: 8/12/17 read the data will
                        PLog.d(TAG, "successfully sent ticket %s", tmp.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                        showError(e.getMessage());
                    } catch (FormatException e) {
                        e.printStackTrace();
                        showError(e.getMessage());
                    } finally {
                        IOUtils.closeQuietly(ndef);
                    }
                }
            }, false);
        }
    }

    private NdefMessage createNdefMessage(Ticket ticket) {
        return new NdefMessage(
                new NdefRecord[]{createMime(
                        "application/vnd.com.evented.verify.ticket", ticket.toString().getBytes())
                });
    }
}
