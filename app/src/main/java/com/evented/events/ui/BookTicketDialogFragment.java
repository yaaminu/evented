package com.evented.events.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.events.data.EventManager;
import com.evented.events.data.UserManager;
import com.evented.tickets.Ticket;
import com.evented.utils.GenericUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.realm.Realm;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by yaaminu on 8/10/17.
 */

public class BookTicketDialogFragment extends BottomSheetDialogFragment {

    static final String ARG_EVENT_ID = "eventId";
    @BindView(R.id.billing_account_number)
    EditText billing_account_number;
    @BindView(R.id.buy_for)
    EditText buy_for;

    @BindView(R.id.book_ticket)
    Button book_ticket;
    Event event;
    private Realm realm;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
        String eventId = getArguments().getString(ARG_EVENT_ID);
        event = realm.where(Event.class)
                .equalTo(Event.FEILD_EVENT_ID, eventId)
                .findFirst();
        GenericUtils.ensureNotNull(event);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_book_ticket, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroy() {
        realm.close();
        super.onDestroy();
    }


    @OnClick(R.id.book_ticket)
    void onClick() {
        if (!validate()) {
            return;
        }
        String billingPhoneNumber = billing_account_number.getText().toString().trim(),
                buyFor = buy_for.getText().toString().trim();

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage(getString(R.string.booking_ticket));
        dialog.setCancelable(false);
        dialog.show();
        EventManager.create(UserManager.getInstance())
                .bookTicket(event.getEventId(), billingPhoneNumber, buyFor, event.getEntranceFee())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Ticket>() {
                    @Override
                    public void call(Ticket ticket) {
                        dialog.dismiss();
                        // TODO: 8/10/17 show ticket details
                        showDialog(ticket.toString());
                        dismiss();
                        //noinspection ConstantConditions
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        dialog.dismiss();
                        //error occurred
                        showDialog(throwable.getMessage());
                    }
                });
    }

    @OnTextChanged(R.id.billing_account_number)
    void onTextChanged(Editable text) {
        buy_for.setText(text);
    }

    private boolean validate() {
        if (billing_account_number.getText().toString().trim().isEmpty()) {
            showDialog(getString(R.string.error_no_billing_account_number));
            billing_account_number.setError("");
            return false;
        }
        if (buy_for.getText().toString().trim().isEmpty()) {
            showDialog(getString(R.string.error_ticket_onwer));
            buy_for.setError("");
            return false;
        }
        return true;
    }

    public void showDialog(String message) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setTitle(R.string.error).create().show();
    }
}
