package com.evented.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AlertDialog;

import com.evented.R;

/**
 * Created by yaaminu on 8/12/17.
 */
public class SimpleBottomSheetDialogFragment extends BottomSheetDialogFragment {

    static final String EXTRA_EVENT_ID = "eventId";
    static final String EXTRA_EVENT_NAME = "eventName";

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String eventId = getArguments().getString(EXTRA_EVENT_ID);
        final String eventName = getArguments().getString(EXTRA_EVENT_NAME);

        String[] items = new String[3];
        items[0] = getString(R.string.verify_tickets);
        items[1] = getString(R.string.update_event);
        items[2] = getString(R.string.view_tickets);

        return new AlertDialog.Builder(getContext()).setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Intent intent = new Intent(getContext(), VerifyTicketActivity.class);
                    intent.putExtra(VerifyTicketActivity.EXTRA_EVENT_ID, eventId);
                    startActivity(intent);
                } else if (i == 1) {
                    Intent intent = new Intent(getContext(), EventDetailsActivity.class);
                    intent.putExtra(EventDetailsActivity.EXTRA_EVENT_ID, eventId);
                    intent.putExtra(EventDetailsActivity.EXTRA_EVENT_NAME, eventName);
                    startActivity(intent);
                } else if (i == 2) {
                    Intent intent = new Intent(getContext(), TicketsListActivity.class);
                    intent.putExtra(TicketsListActivity.EXTRA_EVENT_ID, eventId);
                    intent.putExtra(TicketsListActivity.EXTRA_EVENT_TITLE, getString(R.string.tickets_list, eventName));
                    startActivity(intent);
                }
            }
        }).create();
    }
}
