package com.evented.events.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.evented.R;
import com.evented.events.data.TicketType;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by yaaminu on 8/14/17.
 */

public class AddTicketTypesDialogFragment extends BottomSheetDialogFragment {


    @BindView(R.id.et_ticket_cost)
    EditText et_ticket_cost;
    @BindView(R.id.et_ticket_type)
    EditText et_ticket_type;
    @BindView(R.id.et_available_seat)
    EditText et_available_seat;
    @BindView(R.id.ib_add_ticket_type)
    ImageButton ib_add_ticket_type;
    @BindView(R.id.ticket_class_list)
    ListView ticketClassList;


    List<TicketType> ticketTypes;

    public static AddTicketTypesDialogFragment create(@NonNull List<TicketType> types) {
        AddTicketTypesDialogFragment ticketTypesDialogFragment = new AddTicketTypesDialogFragment();
        ticketTypesDialogFragment.ticketTypes = types;
        return ticketTypesDialogFragment;
    }


    @OnClick(R.id.ib_add_ticket_type)
    void onClick() {
        final TicketType ticketType = validate();
        if (ticketType != null) {
            ticketTypes.add(ticketType);
            adapter.notifyDataSetChanged();
        }
    }

    public List<TicketType> getTicketTypes() {
        return ticketTypes;
    }

    private TicketType validate() {
        String type;
        int maxSeat;
        long amount;

        if (et_ticket_type.getText().toString().trim().length() == 0) {
            et_ticket_type.setError(getString(R.string.cant_be_empty));
            return null;
        }
        type = et_ticket_type.getText().toString().trim();

        if (et_ticket_cost.getText().toString().trim().length() == 0) {
            et_ticket_cost.setError(getString(R.string.cant_be_empty));
            return null;
        }
        try {
            amount =
                    BigDecimal.valueOf(Double.parseDouble(et_ticket_cost.getText().toString().trim()))
                            .multiply(BigDecimal.valueOf(100), MathContext.DECIMAL128).longValue();
        } catch (NumberFormatException e) {
            this.et_ticket_cost.setError(getString(R.string.invalid_amount));
            return null;
        }

        if (et_available_seat.getText().toString().trim().length() == 0) {
            et_available_seat.setError(getString(R.string.cant_be_empty));
            return null;
        }
        try {
            maxSeat =
                    Integer.parseInt(et_available_seat.getText().toString().trim());
        } catch (NumberFormatException e) {
            this.et_available_seat.setError(getString(R.string.invalid_num));
            return null;
        }
        et_available_seat.setText("");
        et_ticket_type.setText("");
        et_ticket_cost.setText("");

        return new TicketType(type, amount, maxSeat);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.add_ticket_class_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (ticketTypes == null) {
            ticketTypes = Collections.emptyList();
        }
        ticketClassList.setAdapter(adapter);
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        Snackbar.make(getActivity().getWindow().getDecorView(), R.string.ticket_type_updated, Snackbar.LENGTH_LONG)
                .show();
    }

    private final BaseAdapter adapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return ticketTypes.size();
        }

        @Override
        public TicketType getItem(int i) {
            return ticketTypes.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(android.R.layout.simple_list_item_1, viewGroup, false);
            }
            ((TextView) view).setText(Html.fromHtml(getItem(i).toReadableString()));
            return view;
        }
    };

}
