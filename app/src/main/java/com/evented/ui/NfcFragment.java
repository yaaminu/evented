package com.evented.ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.ui.BaseFragment;
import com.evented.tickets.TicketDetailsActivity;
import com.evented.utils.GenericUtils;
import com.evented.utils.PLog;
import com.evented.utils.TaskManager;

import org.apache.commons.io.IOUtils;

import java.io.IOException;

import butterknife.BindView;

/**
 * Created by yaaminu on 8/12/17.
 */

public class NfcFragment extends BaseFragment {

    private static final String TAG = "NfcFragment";
    private VerifyTicketCallbacks callbacks;
    private NfcAdapter nfcAdapter;
    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;
    private PendingIntent pendingIntent;

    @BindView(R.id.tv_nfc_data)
    TextView tv_nfc_data;

    @BindView(R.id.progress)
    View progress;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        GenericUtils.assertThat(context instanceof VerifyTicketCallbacks, "must implement " + VerifyTicketCallbacks.class.getName());
        callbacks = ((VerifyTicketCallbacks) context);
    }

    @Override
    protected int getLayout() {
        return R.layout.nfc_fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupNfcIntent();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(getActivity(), pendingIntent, intentFiltersArray, techListsArray);
        }
        handleIntent(getActivity().getIntent());
    }

    private void setupNfcIntent() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(getActivity());
        if (nfcAdapter != null) {
            pendingIntent = PendingIntent.getActivity(
                    getActivity(), 0, new Intent(getActivity(), getActivity().getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            try {
                ndef.addDataType("application/vnd.com.evented.verify.ticket");
            } catch (IntentFilter.MalformedMimeTypeException e) {
                throw new RuntimeException("fail", e);
            }
            intentFiltersArray = new IntentFilter[]{ndef,};
            techListsArray = new String[][]{new String[]{Ndef.NFC_FORUM_TYPE_1}};
        } else {
            TicketDetailsActivity.notifyNoNfc(getActivity());
        }
    }


    void handleIntent(Intent intent) {
        final Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if (tagFromIntent != null) {
            TaskManager.executeNow(new Runnable() {
                @Override
                public void run() {
                    Ndef ndef = null;
                    try {

                        ndef = Ndef.get(tagFromIntent);
                        ndef.connect();
                        NdefMessage message = ndef
                                .getNdefMessage();
                        final String payload = new String(message.getRecords()[0].getPayload());
                        PLog.d(TAG, payload);
                        showTicket(payload);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (FormatException e) {
                        e.printStackTrace();
                    } finally {
                        IOUtils.closeQuietly(ndef);
                    }
                }
            }, false);
        }
    }

    private void showTicket(final String message) {
        getActivity()
                .runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_nfc_data.setError(message);
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(getActivity());
        }
    }

}
