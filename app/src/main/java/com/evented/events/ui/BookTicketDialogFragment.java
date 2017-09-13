package com.evented.events.ui;

import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.Event;
import com.evented.events.data.EventManager;
import com.evented.events.data.TicketType;
import com.evented.events.data.UserManager;
import com.evented.tickets.Ticket;
import com.evented.tickets.TicketDetailsActivity;
import com.evented.utils.GenericUtils;
import com.evented.utils.ViewUtils;

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
    private EventManager eventManager;
    @BindView(R.id.billing_account_number)
    EditText billing_account_number;
    @BindView(R.id.buy_for)
    EditText buy_for;
    @BindView(R.id.book_ticket_root_layout)
    View book_ticket_root_layout;

    @BindView(R.id.et_verification_code)
    EditText et_verification;

    @BindView(R.id.book_ticket)
    Button book_ticket;

    @BindView(R.id.tv_instruction)
    TextView tv_instruction;

    @BindView(R.id.sp_ticket_types)
    Spinner spTicketTypes;

    Event event;
    private Realm realm;

    int stage = 0;
    private ProgressDialog dialog;

    @BindView(R.id.ticket_type_parent)
    View ticketTypeRootLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            stage = savedInstanceState.getInt("stage", 0);
        }
        eventManager = EventManager.create(UserManager.getInstance());
        realm = Realm.getDefaultInstance();
        String eventId = getArguments().getString(ARG_EVENT_ID);
        event = realm.where(Event.class)
                .equalTo(Event.FIELD_EVENT_ID, eventId)
                .findFirst();
        GenericUtils.ensureNotNull(event);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("stage", stage);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_book_ticket, container, false);
        ButterKnife.bind(this, view);
        dialog = new ProgressDialog(getContext());
        dialog.setCancelable(false);
        spTicketTypes.setAdapter(new TicketTypesAdapter(event.getTicketTypes()));
        if (event.getTicketTypes().size() == 1) {
            spTicketTypes.setSelection(1); //the adapter shifts the item up and place an empty item at first index
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (stage == 0 || stage == 1) {
            tv_instruction.setText(stage == 0 ? R.string.purchase_ticket_instruction : R.string.verification_instruaction);
            ViewUtils.showByFlag(stage == 0, book_ticket_root_layout);
            ViewUtils.showByFlag(stage == 1, et_verification);
            book_ticket.setText(stage == 0 ? R.string.book_ticket : R.string.verify_and_book);
        } else {
            throw new AssertionError();
        }
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
        if (stage == 0) {
            dialog.setMessage(getString(R.string.initialising_transaction));
            dialog.show();
            eventManager.verifyNumber(event.getName(), billing_account_number.getText().toString().trim())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            dialog.dismiss();
                            ViewUtils.hideViews(book_ticket_root_layout, ticketTypeRootLayout);
                            tv_instruction.setText(getString(R.string.verification_instruaction));
                            ViewUtils.showViews(et_verification);
                            book_ticket.setText(R.string.verify_and_book);
                            stage = 1;
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            dialog.dismiss();
                            showDialog(throwable.getMessage());
                        }
                    });
        } else if (stage == 1) {
            completeBooking();
        } else {
            throw new AssertionError();
        }
    }

    private void completeBooking() {
        String billingPhoneNumber = billing_account_number.getText().toString().trim(),
                buyFor = buy_for.getText().toString().trim();
        dialog.setMessage(getString(R.string.booking_ticket));
        dialog.show();
        final TicketType ticketType = (TicketType) spTicketTypes.getSelectedItem();
        eventManager
                .bookTicket(event.getEventId(), event.getName()
                        , billingPhoneNumber, buyFor,
                        ticketType.getCost(),
                        et_verification.getText().toString(), ticketType.getName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Ticket>() {
                    @Override
                    public void call(Ticket ticket) {
                        dialog.dismiss();
                        Intent intent = new Intent(getContext(), TicketDetailsActivity.class);
                        intent.putExtra(TicketDetailsActivity.EXTRA_TICKET_ID, ticket.getTicketId());
                        dismiss();
                        getActivity().startActivity(intent);
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
        if (stage == 0) {
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
        } else if (stage == 1) {
            if (spTicketTypes.getSelectedItemPosition() == 0) {
                showDialog(getString(R.string.ticket_required));
                return false;
            }

            if (et_verification.getText().toString().trim().length() != 4) {
                showDialog(getString(R.string.invalid_verification_code));
                et_verification.setError("");
                return false;
            }
            return true;
        } else {
            throw new AssertionError();
        }
    }

    public void showDialog(String message) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setTitle(R.string.error).create().show();
    }
}
